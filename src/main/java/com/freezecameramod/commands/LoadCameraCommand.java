package com.freezecameramod.commands;

import com.freezecameramod.comun.SavedCameraManager;
import com.freezecameramod.config.ConfigManager;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

import static com.freezecameramod.comun.FormatText.varDefault;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;


public class LoadCameraCommand {
    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(
                    literal("freezecameramod")
                    .then(literal("loadCamera")
                            .then(argument("cameraName", StringArgumentType.string())
                                    .suggests(((context, builder) -> {
                                        return SharedSuggestionProvider.suggest(SavedCameraManager.getSavedCameraNames(), builder);
                                    }))

                            .executes(context -> {
                                String cameraName = StringArgumentType.getString(context, "cameraName");
                                //Cargar la camara guardada con ese nombre
                                Component camNameText = varDefault(cameraName);

                                if (SavedCameraManager.loadSavedCamera(cameraName)) {
                                    context.getSource().sendFeedback(Component.translatable("message.freezecamera.loadCameraSuccess",camNameText));
                                } else {
                                    context.getSource().sendFeedback(Component.translatable("message.freezecamera.loadCameraError",camNameText));
                                }

                                return 1;
                            })
                        )
                    )
            );
        });
    }
}
