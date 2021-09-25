package com.flysword.loader;

import com.flysword.entity.EntitySword;
import com.flysword.FlySwordMod;
import com.flysword.entity.EntitySwordBeam;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityRegistry;

import static com.flysword.FlySwordMod.MODID;

public final class EntityLoader {
    private static int sEntityId = 0;

    public static void registerEntities() {
        EntityRegistry.registerModEntity(new ResourceLocation(MODID, "EntitySword"), EntitySword.class, MODID + ".EntitySword", sEntityId++, FlySwordMod.instance, 64, 1, true);
        EntityRegistry.registerModEntity(new ResourceLocation(MODID, "EntitySwordBeam"), EntitySwordBeam.class, MODID + ".EntitySwordBeam", sEntityId++, FlySwordMod.instance, 64, 10, true);
    }
}
