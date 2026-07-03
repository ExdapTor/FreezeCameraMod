package com.freezecameramod.commands;

import com.freezecameramod.comun.CameraUtils;
import com.freezecameramod.comun.SavedCameraManager;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.network.chat.Component;

import static com.freezecameramod.comun.FormatText.varDefault;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal;

public class SaveCameraCommand {
    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(
                    literal("freezecameramod")
                            .then(literal("saveCamera")
                                    .then(argument("cameraName", StringArgumentType.word())
                                            .executes(context -> {
                                                String cameraName = StringArgumentType.getString(context, "cameraName");
                                                float yaw = CameraUtils.getYaw();
                                                float pitch = CameraUtils.getPitch();
                                                Component camNameText = varDefault(cameraName);
                                                Component yawText = varDefault(String.valueOf(yaw));
                                                Component pitchText = varDefault(String.valueOf(pitch));

                                                SavedCameraManager.saveCamera(cameraName, yaw, pitch);

                                                context.getSource().sendFeedback(
                                                        Component.translatable("message.freezecamera.saveCameraSuccess", camNameText, yawText, pitchText)
                                                );
                                                return 1;
                                            })
                                    )
                            )
            );
        });
    }
}