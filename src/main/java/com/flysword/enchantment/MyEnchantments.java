package com.flysword.enchantment;

import com.flysword.FlySwordMod;
import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class MyEnchantments {
    public static Enchantment sFlySword;


    public static void init() {
        ForgeRegistries.ENCHANTMENTS.register(sFlySword = new EnchantmentFlySword().setRegistryName(FlySwordMod.MODID, EnchantmentFlySword.NAME));
    }
}
