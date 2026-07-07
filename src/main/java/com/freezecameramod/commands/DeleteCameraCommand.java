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

public class DeleteCameraCommand {
    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(
                    literal("freezecameramod")
                            .then(literal("deleteCamera")
                                    .then(argument("cameraName", StringArgumentType.string())
                                            .suggests((context, builder) ->
                                                    CommandSource.suggestMatching(SavedCameraManager.getSavedCameraNames(), builder)
                                            )
                                            .executes(context -> {
                                                String name = StringArgumentType.getString(context, "cameraName");
                                                Text camNameText = varDefault(name);

                                                if (SavedCameraManager.deleteCamera(name)) {
                                                    context.getSource().sendFeedback(Text.translatable("message.freezecamera.deleteCameraSuccess", camNameText));
                                                } else {
                                                    context.getSource().sendFeedback(Text.translatable("message.freezecamera.deleteCameraError", camNameText));
                                                }

                                                return 1;
                                            })
                                    )
                            )

            );
        });
    }
}
