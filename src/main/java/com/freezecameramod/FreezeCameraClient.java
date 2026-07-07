package com.freezecameramod;

import com.freezecameramod.commands.*;
import com.freezecameramod.config.ConfigManager;
import com.freezecameramod.keybinds.FreezeCameraKeyBind;
import com.freezecameramod.keybinds.FreezeSlotKeyBind;
import com.freezecameramod.mixin.ScreenAccessor;
import com.mojang.authlib.minecraft.client.MinecraftClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;


public class FreezeCameraClient implements ClientModInitializer {
    private static KeyMapping freezeKey;
    private static boolean frozen = false;
    private static float yaw;
    private static float pitch;
    Minecraft client = Minecraft.getInstance();

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
                StringWidget errorMessage = new StringWidget(
                        Component.translatable("message.freezecamera.configError").withColor(0xFFFF5555), //Color cyan
                        client.font
                );
                errorMessage.setPosition(10, 10);
                ((ScreenAccessor) screen).getDrawables().add(errorMessage);
            }
        });
    }

    public static boolean isFrozen(){
        return frozen;
    }
}
