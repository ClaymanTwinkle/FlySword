package com.flysword;

import com.flysword.enchantment.MyEnchantments;
import com.flysword.entity.EntitySword;
import com.flysword.key.ModKeys;
import com.flysword.loader.EntityLoader;
import com.flysword.loader.EntityRenderLoader;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Logger;

@Mod(modid = FlySwordMod.MODID, name = FlySwordMod.NAME, version = FlySwordMod.VERSION)
public class FlySwordMod {
    public static final String MODID = "flysword";
    public static final String NAME = "Fly Sword Mod";
    public static final String VERSION = "1.0";

    private static Logger logger;

    @Mod.Instance(FlySwordMod.MODID)
    public static FlySwordMod instance;

    @SidedProxy(clientSide = "com.flysword.client.ClientProxy", serverSide = "com.flysword.CommonProxy")
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();

        EntityLoader.registerEntities();
        EntityRenderLoader.registerRenders();
        MyEnchantments.init();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);

        ModKeys.init();
    }

    @SubscribeEvent
    public void stopUsing(PlayerInteractEvent.RightClickItem event) {
        World world = event.getWorld();
        if (!world.isRemote) {
            EntityPlayer player = event.getEntityPlayer();
            if (player != null) {
                ItemStack stack = player.getHeldItemMainhand();
                if (!player.isRiding() && EnchantmentHelper.getEnchantmentLevel(MyEnchantments.sFlySword, stack) > 0) {
                    stack.damageItem(1, player);
                    if(stack.getItem().isDamaged(stack) || player.isCreative()) {
                        EntitySword entitySword = new EntitySword(world);
                        entitySword.setItemStack(new ItemStack(stack.getItem()));
                        entitySword.setPositionAndUpdate(player.posX, player.posY, player.posZ);
                        world.spawnEntity(entitySword);
                        event.getEntity().startRiding(entitySword);
                    }
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void renderPlayer(RenderPlayerEvent.Post event) {
    }
}
