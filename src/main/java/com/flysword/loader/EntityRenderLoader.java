package com.flysword.loader;

import com.flysword.EntitySword;
import com.flysword.RenderSword;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityRenderLoader {

    @SideOnly(Side.CLIENT)
    public static void registerRenders() {
        RenderingRegistry.registerEntityRenderingHandler(EntitySword.class, RenderSword::new);
    }
}
