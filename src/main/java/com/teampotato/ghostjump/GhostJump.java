package com.teampotato.ghostjump;

import com.lion.graveyard.blocks.SarcophagusBlock;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Mod(GhostJump.ID)
public class GhostJump {
    public static final String ID = "ghostjump";

    public static final ForgeConfigSpec configSpec;
    public static final ForgeConfigSpec.IntValue chance;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> entity;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push("GhostJump");
        chance = builder.defineInRange("Chance", 7, 1, 100);
        entity = builder.defineList("SummonedEntities", Arrays.asList("graveyard:reaper"), o -> o instanceof String);
        builder.pop();
        configSpec = builder.build();
    }

    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.isCanceled()) return;
        BlockPos pos = event.getPos();
        Level level = event.getLevel();
        if (!(level.getBlockState(pos).getBlock() instanceof SarcophagusBlock)) return;
        MinecraftServer server = level.getServer();
        if (server == null) return;
        if (ThreadLocalRandom.current().nextInt(0, 101) > chance.get()) return;
        entity.get().forEach(
                spawnEntity ->{
                    EntityType<?> entityType = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(spawnEntity));
                    Mob entity = (Mob) entityType.create(level);
                    entity.setPos(pos.getX(), pos.getY()+1, pos.getZ());
                    level.addFreshEntity(entity);
                }
        );
    }

    public GhostJump() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, configSpec);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, this::onRightClickBlock);
    }
}
