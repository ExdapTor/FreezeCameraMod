package com.freezecameramod.commands;

import com.freezecameramod.comun.SavedCameraManager;
import com.freezecameramod.config.ConfigManager;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.network.chat.Component;
import java.util.Map;
import java.util.Set;

import static com.freezecameramod.comun.FormatText.varDefault;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal;

public class ListCameraCommand {
    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(literal("freezecameramod")
                    .then(literal("listCameras")
                            .executes(context -> {
                                var cameras = SavedCameraManager.getSavedCameras();

                                if (cameras.isEmpty()) {
                                    context.getSource().sendFeedback(Component.translatable("message.freezecamera.listCamerasNull"));
                                } else {
                                    context.getSource().sendFeedback(Component.translatable("message.freezecamera.listCamerasSuccess"));
                                    for (Map.Entry<String, SavedCameraManager.CameraMemory> entry : cameras.entrySet()) {
                                        String name = entry.getKey();
                                        SavedCameraManager.CameraMemory data = entry.getValue();

                                        context.getSource().sendFeedback(Component.translatable("message.freezecamera.listCamerasObtained", name, data.yaw(), data.pitch()));
                                    }
                                }

                                return 1;
                            })
                    )
            );
        });
    }
}
