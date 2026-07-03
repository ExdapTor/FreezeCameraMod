package com.freezecameramod.commands;

import com.mojang.brigadier.arguments.FloatArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;

import static com.freezecameramod.comun.CameraUtils.setCameraAngles;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class SetCameraCommand {
    public static void register(){
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            var pitchArg = argument("pitch", FloatArgumentType.floatArg())
                .executes(context -> setCameraAngles(
                        FloatArgumentType.getFloat(context, "yaw"),
                        FloatArgumentType.getFloat(context, "pitch")
                ));

            var yawArg = argument("yaw", FloatArgumentType.floatArg())
                    .then(pitchArg);

            dispatcher.register(
                literal("freezecameramod").then(literal("setCamera").then(yawArg))
            );
        });

    }
}
