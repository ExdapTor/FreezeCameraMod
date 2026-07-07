package com.freezecameramod.commands;

import com.freezecameramod.comun.SavedCameraManager;
import com.freezecameramod.config.ConfigManager;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

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
                                        return CommandSource.suggestMatching(SavedCameraManager.getSavedCameraNames(), builder);
                                    }))

                            .executes(context -> {
                                String cameraName = StringArgumentType.getString(context, "cameraName");
                                //Cargar la camara guardada con ese nombre
                                Text camNameText = varDefault(cameraName);

                                if (SavedCameraManager.loadSavedCamera(cameraName)) {
                                    context.getSource().sendFeedback(Text.translatable("message.freezecamera.loadCameraSuccess",camNameText));
                                } else {
                                    context.getSource().sendFeedback(Text.translatable("message.freezecamera.loadCameraError",camNameText));
                                }

                                return 1;
                            })
                        )
                    )
            );
        });
    }
}
