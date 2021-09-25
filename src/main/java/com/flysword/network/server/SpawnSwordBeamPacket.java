package com.flysword.network.server;

import com.flysword.enchantment.ModEnchantments;
import com.flysword.entity.EntitySwordBeam;
import com.flysword.utils.PlayerUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.Random;

public class SpawnSwordBeamPacket implements IMessage, IMessageHandler<SpawnSwordBeamPacket, IMessage> {

    @Override
    public void fromBytes(ByteBuf buf) {

    }

    @Override
    public void toBytes(ByteBuf buf) {

    }

    @Override
    public IMessage onMessage(SpawnSwordBeamPacket message, MessageContext ctx) {
        EntityPlayer player = ctx.getServerHandler().player;
        if (player != null) {
            ItemStack stack = player.getHeldItemMainhand();

            int level;
            if( (level = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.sSwordBeam, stack)) > 0) {
                stack.damageItem(new Random().nextBoolean()?1:0, player);
                PlayerUtils.playSoundAtEntity(player.world, player, SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, SoundCategory.PLAYERS, 0.4F, 0.5F);
                float damage = getDamage(player, stack) * (level * 0.25f);
                EntitySwordBeam beam = new EntitySwordBeam(player.world, player).setLevel(level).setDamage(damage);
                beam.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, beam.getVelocity(), 1.0F);
                player.world.spawnEntity(beam);
            }
        }
        return null;
    }

    /** Returns player's base damage (with sword) plus 1.0F per level */
    private float getDamage(EntityPlayer player, ItemStack heldItemStack) {
        float f = (float)player.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
        float f1 = EnchantmentHelper.getModifierForCreature(heldItemStack, EnumCreatureAttribute.UNDEFINED);
        return f + f1;
    }
}

