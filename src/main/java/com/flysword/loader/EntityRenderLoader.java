package com.flysword.loader;

import com.flysword.entity.EntitySword;
import com.flysword.entity.EntitySwordBeam;
import com.flysword.render.RenderEntitySwordBeam;
import com.flysword.render.RenderSword;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityRenderLoader {

    @SideOnly(Side.CLIENT)
    public static void registerRenders() {
        RenderingRegistry.registerEntityRenderingHandler(EntitySword.class, RenderSword::new);
        RenderingRegistry.registerEntityRenderingHandler(EntitySwordBeam.class, new RenderEntitySwordBeam.Factory());
    }
}
