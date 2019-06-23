package com.flysword;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.UUID;

public class CommonProxy {

    public void registerItemRenderer(Item item, int meta, String id) {

    }
    public void registerModels()
    {

    }
    @SuppressWarnings("unchecked")
    public EntityPlayer getPlayerFromUUID(String uuid) {
        UUID uid = UUID.fromString(uuid);
        return FMLCommonHandler.instance ().getMinecraftServerInstance ().getPlayerList ().getPlayerByUUID(uid);


    }
}