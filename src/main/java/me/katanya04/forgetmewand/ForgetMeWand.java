package me.katanya04.forgetmewand;

import me.katanya04.forgetmewand.items.ModItems;
import net.fabricmc.api.ModInitializer;

public class ForgetMeWand implements ModInitializer {
    public static final String MOD_ID = "forgetmewand";
    @Override
    public void onInitialize() {
        ModItems.initialize();
    }
}
