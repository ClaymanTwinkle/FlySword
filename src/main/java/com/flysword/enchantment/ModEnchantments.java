package com.flysword.enchantment;

import com.flysword.FlySwordMod;
import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class ModEnchantments {
    public static Enchantment sFlySword;
    public static Enchantment sSwordBeam;

    public static void init() {
        ForgeRegistries.ENCHANTMENTS.register(sFlySword = new EnchantmentFlySword().setRegistryName(FlySwordMod.MODID, EnchantmentFlySword.NAME));
        ForgeRegistries.ENCHANTMENTS.register(sSwordBeam = new EnchantmentSwordBeam().setRegistryName(FlySwordMod.MODID, EnchantmentSwordBeam.NAME));
    }
}
