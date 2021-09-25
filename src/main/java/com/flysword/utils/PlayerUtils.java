package com.flysword.utils;

import net.minecraft.entity.Entity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class PlayerUtils {
    /**
     * Plays a sound on the server with randomized volume and pitch; no effect if called on client
     * @param f		Volume: nextFloat() * f + add
     * @param add	Pitch: 1.0F / (nextFloat() * f + add)
     */
    public static void playSoundAtEntity(World world, Entity entity, SoundEvent sound, SoundCategory category, float f, float add) {
        float volume = world.rand.nextFloat() * f + add;
        float pitch = 1.0F / (world.rand.nextFloat() * f + add);
        world.playSound(null, entity.getPosition(), sound, category, volume, pitch);
    }
}
