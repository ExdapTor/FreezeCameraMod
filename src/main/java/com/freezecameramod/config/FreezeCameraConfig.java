package com.freezecameramod.config;

import java.util.HashMap;
import java.util.HashMap;
import java.util.Map;

import com.freezecameramod.comun.SavedCameraManager;
import com.freezecameramod.comun.SavedCameraManager.CameraMemory;
import com.freezecameramod.config.ModVersion;

public class FreezeCameraConfig {

    public Map<String, CameraMemory> savedCameras = new HashMap<>();
    public String modVersion = ModVersion.VERSION;

    public void onLoaded() {
        SavedCameraManager.loadFromConfig(savedCameras);
    }
}