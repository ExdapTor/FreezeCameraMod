package com.freezecameramod;

import com.freezecameramod.commands.*;
import com.freezecameramod.comun.SavedCameraManager;
import com.freezecameramod.config.ConfigManager;
import com.freezecameramod.keybinds.FreezeCameraKeyBind;
import com.freezecameramod.keybinds.FreezeSlotKeyBind;
import com.freezecameramod.mixin.ScreenAccessor;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.joml.Matrix4f;


public class FreezeCameraClient implements ClientModInitializer {
    private static KeyBinding freezeKey;
    private static boolean frozen = false;
    private static float yaw;
    private static float pitch;
    MinecraftClient client = MinecraftClient.getInstance();

    // Método para inicializar los tres valores a la vez
    public static void initFrozen(boolean frozenValue, float pitchValue, float yawValue) {
        frozen = frozenValue;
        pitch = pitchValue;
        yaw = yawValue;
    }

    public static float getFrozenYaw() {
        return yaw;
    }

    public static float getFrozenPitch() {
        return pitch;
    }

    public static void setFrozen(boolean frozen) {
        FreezeCameraClient.frozen = frozen;
    }

    public static void setYaw(float yaw) {
        FreezeCameraClient.yaw = yaw;
    }

    public static void setPitch(float pitch) {
        FreezeCameraClient.pitch = pitch;
    }

    @Override
    public void onInitializeClient() {
        System.out.println("[FreezeCameraMod] FreezeCameraClient loaded.");
        ConfigManager.loadConfig();   // <-- Carga o crea la configuración

        if (ConfigManager.hasConfigError()) { // Si hubo algun error
            ConfigError(); // Mostrar mensaje en pantalla si hay error
        }

        //Teclas
        FreezeCameraKeyBind.register();
        FreezeSlotKeyBind.register();


        //Comandos
        SetCameraCommand.register(); //Registrar el comando
        LoadCameraCommand.register();
        SaveCameraCommand.register();
        DeleteCameraCommand.register();
        ListCameraCommand.register();
    }

    private void ConfigError() {
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (screen instanceof TitleScreen) {
                TextWidget errorMessage = new TextWidget(
                        Text.translatable("message.freezecamera.configError"),
                        client.textRenderer
                );
                errorMessage.setPosition(10, 10);
                errorMessage.setTextColor(0xFFFF5555); // Cyan
                ((ScreenAccessor) screen).getDrawables().add(errorMessage);
            }
        });
    }

    public static boolean isFrozen(){
        return frozen;
    }
}
