package com.freezecameramod.comun;

import com.freezecameramod.FreezeCameraClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class CameraUtils {
    public static int setCameraAngles(float yaw, float pitch) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.player != null) {
            client.player.setYaw(yaw);
            client.player.setPitch(pitch);
            FreezeCameraClient.setYaw(yaw);
            FreezeCameraClient.setPitch(pitch);
            FreezeCameraClient.setFrozen(true);
            client.player.sendMessage(Text.translatable(
                    "message.freezecamera.setFrozenCamera",
                    yaw,
                    pitch), false);

            return 1;
        } else {
            return 0;
        }
    }
    public static float getYaw() {
        MinecraftClient client = MinecraftClient.getInstance();
        return client.player.getYaw();
    }
    public static float getPitch() {
        MinecraftClient client = MinecraftClient.getInstance();
        return client.player.getPitch();
    }
}
