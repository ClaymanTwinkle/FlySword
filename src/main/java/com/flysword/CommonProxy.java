package com.flysword;

import com.flysword.enchantment.MyEnchantments;
import com.flysword.key.ModKeys;
import com.flysword.loader.EntityLoader;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.util.UUID;

public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {
        EntityLoader.registerEntities();
        MyEnchantments.init();
    }

    public void init(FMLInitializationEvent event) {
    }

    public void postInit(FMLPostInitializationEvent event) {

    }
}