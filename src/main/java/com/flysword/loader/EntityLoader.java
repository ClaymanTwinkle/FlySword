package com.flysword.loader;

import com.flysword.entity.EntitySword;
import com.flysword.FlySwordMod;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityRegistry;

import static com.flysword.FlySwordMod.MODID;

public final class EntityLoader {
    public static void registerEntities() {
        EntityRegistry.registerModEntity(new ResourceLocation(MODID, "EntitySword"), EntitySword.class, MODID + ".EntitySword", 1, FlySwordMod.instance, 256, 1, true);


    }
}
