package com.flysword;

import com.flysword.enchantment.ModEnchantments;
import com.flysword.entity.EntitySword;
import com.flysword.network.server.SpawnSwordBeamPacket;
import com.flysword.utils.PacketDispatcher;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = FlySwordMod.MODID, name = FlySwordMod.NAME, version = FlySwordMod.VERSION)
public class FlySwordMod {
    public static final String MODID = "flysword";
    public static final String NAME = "Fly Sword Mod";
    public static final String VERSION = "1.1.0";

    @Mod.Instance(FlySwordMod.MODID)
    public static FlySwordMod instance;

    @SidedProxy(clientSide = "com.flysword.client.ClientProxy", serverSide = "com.flysword.CommonProxy")
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
        PacketDispatcher.initialize();
        MinecraftForge.EVENT_BUS.register(this);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    @SubscribeEvent
    public void stopUsing(PlayerInteractEvent.RightClickItem event) {
        World world = event.getWorld();
        if (!world.isRemote) {
            EntityPlayer player = event.getEntityPlayer();
            if (player != null) {
                ItemStack stack = player.getHeldItemMainhand();
                if (!player.isRiding() && EnchantmentHelper.getEnchantmentLevel(ModEnchantments.sFlySword, stack) > 0) {
                    stack.damageItem(1, player);
                    if (stack.getItem().isDamaged(stack) || player.isCreative()) {
                        player.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
                        EntitySword entitySword = new EntitySword(world);
                        entitySword.setItemStack(stack);
                        entitySword.setOwnerId(player.getUniqueID());
                        entitySword.setPositionAndUpdate(player.posX, player.posY, player.posZ);
                        world.spawnEntity(entitySword);
                        event.getEntity().startRiding(entitySword);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void playAttack(PlayerInteractEvent.LeftClickEmpty event) {
        World world = event.getWorld();
        if (world.isRemote) {
            PacketDispatcher.sendToServer(new SpawnSwordBeamPacket());
        }
    }

}
