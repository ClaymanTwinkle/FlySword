package com.flysword.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

public class EnchantmentSwordBeam extends Enchantment {
    public static final String NAME = "swordbeam";

    protected EnchantmentSwordBeam() {
        super(Enchantment.Rarity.UNCOMMON, EnumEnchantmentType.WEAPON, EntityEquipmentSlot.values());
        this.setName(NAME);
    }

    public boolean canApply(ItemStack stack)
    {
        return stack.getItem() instanceof ItemSword || super.canApply(stack);
    }

    @Override
    public int getMaxLevel() {
        return 4;
    }
}
