package io.github.codeutilities.mixin.player;

import blue.endless.jankson.annotation.Nullable;
import io.github.codeutilities.util.networking.DFInfo;
import io.github.codeutilities.util.networking.State;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {

    /**
     * @author CodeUtilities
     * @reason yea
     */
    @Inject(method = "openScreen(Lnet/minecraft/client/gui/screen/Screen;)V", at = @At("HEAD"), cancellable = true)
    public void openScreen(@Nullable Screen screen, CallbackInfo cbi) {
        if(MinecraftClient.getInstance().player == null) {
            DFInfo.currentState.setMode(State.Mode.OFFLINE);
        }
    }

}
