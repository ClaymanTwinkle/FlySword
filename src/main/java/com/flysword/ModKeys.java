package com.flysword;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.input.Keyboard;

public class ModKeys {
    public static KeyBinding sKeyFlySwordDown;

    public static void init() {
        sKeyFlySwordDown = new KeyBinding("key.fly_sword_down", Keyboard.KEY_X, "key.categories.gameplay");
        ClientRegistry.registerKeyBinding(sKeyFlySwordDown);
    }
}
