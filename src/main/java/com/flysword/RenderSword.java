package com.flysword;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SideOnly(Side.CLIENT)
public class RenderSword extends Render<EntitySword> {

    public RenderSword(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public void doRender(@Nonnull EntitySword entity, double x, double y, double z, float entityYaw, float partialTicks) {
        World world = entity.world;
        if (world == null)
            return;
        this.bindEntityTexture(entity);
        GlStateManager.pushMatrix();
        {
            GlStateManager.translate(x, y+1, z);
            GlStateManager.rotate(-45, 0, 0, 1);
            GlStateManager.rotate(90, 1, 1, 0);

            GlStateManager.rotate(entityYaw+90, 0, 0, 1);

            RenderItem itemRender = Minecraft.getMinecraft().getRenderItem();
            itemRender.renderItem(entity.getItemStack(), itemRender.getItemModelWithOverrides(entity.getItemStack(), entity.world, null));
        }
        GlStateManager.popMatrix();

        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntitySword entity) {
        return TextureMap.LOCATION_BLOCKS_TEXTURE;
    }
}
