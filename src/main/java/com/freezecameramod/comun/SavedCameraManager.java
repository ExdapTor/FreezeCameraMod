package com.freezecameramod.comun;

import com.freezecameramod.config.FreezeCameraConfig;
import com.google.gson.Gson;

import java.io.*;
import java.util.*;

import com.freezecameramod.config.ModVersion;
import com.freezecameramod.config.ConfigManager;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class SavedCameraManager {
    public static Map<String, CameraMemory> savedCameras = new HashMap<>();

    public static void loadFromConfig(Map<String, CameraMemory> savedMap) {
        savedCameras.clear();
        for (Map.Entry<String, CameraMemory> entry : savedMap.entrySet()) {
            String name = entry.getKey();
            CameraMemory orientation = entry.getValue();
            CameraMemory cameraData = new CameraMemory(orientation.yaw, orientation.pitch);
            savedCameras.put(name, cameraData);
        }
    }

    public static Map<String, CameraMemory> getAll() {
        return savedCameras;
    }

    public static Set<String> getSavedCameraNames() {
        return savedCameras.keySet();
    }

    public static Map<String, CameraMemory> getSavedCameras() {
        return Collections.unmodifiableMap(savedCameras);
    }

    public static boolean loadSavedCamera(String name) {
        if (savedCameras.containsKey(name)) {
            CameraMemory data = savedCameras.get(name);
            CameraUtils.setCameraAngles(data.yaw, data.pitch);
            return true;
        }
        return false;
    }

    public static void saveCamera(String name, float yaw, float pitch) {
        // Actualizar en memoria
        savedCameras.put(name, new CameraMemory(yaw, pitch));

        // Actualizar la config para guardar en disco
        updateConfigFromSavedCameras();

        // Guardar config en disco
        ConfigManager.saveConfig();
    }

    public static boolean deleteCamera(String name) {
        if (savedCameras.remove(name) != null) {
            updateConfigFromSavedCameras();
            ConfigManager.saveConfig();
            return true;
        }
        return false;
    }

    private static void updateConfigFromSavedCameras() {
        if (ConfigManager.config == null) {
            ConfigManager.config = new FreezeCameraConfig();
        }

        Map<String, CameraMemory> newMap = new HashMap<>();

        for (Map.Entry<String, CameraMemory> entry : savedCameras.entrySet()) {
            CameraMemory data = entry.getValue();
            newMap.put(entry.getKey(), new CameraMemory(data.yaw(), data.pitch()));
        }

        ConfigManager.config.savedCameras = newMap;
    }

    //Camara en memoria
    public record CameraMemory(float yaw, float pitch) {}
}

/*Antiguos
    private static final File FILE = new File("config/freezecameramod/freezecamera.json");
    private static final Gson GSON = new com.google.gson.GsonBuilder().setPrettyPrinting().create();
    private static final Map<String, CameraData> savedCameras = new HashMap<>();
    private static boolean loaded = false;

    static {
        loadFromDisk(); // carga inicial al cargar la clase
    }

    public static boolean loadSavedCamera(String name) {
        if (!FILE.exists()) return false;

        try (Reader reader = new FileReader(FILE)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            if (!root.has("savedCameras")) return false;

            JsonObject cameras = root.getAsJsonObject("savedCameras");
            if (!cameras.has(name)) return false;

            JsonObject cam = cameras.getAsJsonObject(name);
            float yaw = cam.get("yaw").getAsFloat();
            float pitch = cam.get("pitch").getAsFloat();

            System.out.println("Loading camera '" + name + "' with yaw=" + yaw + ", pitch=" + pitch);
            CameraUtils.setCameraAngles(yaw, pitch);
            return true;
        } catch (Exception e) {
            System.err.println("File loading error:");
            e.printStackTrace();
            return false;
        }
    }

    public static void saveCamera(String name, float yaw, float pitch) {
        try {
            // Crear la carpeta si no existe
            FILE.getParentFile().mkdirs();

            JsonObject root = new JsonObject();

            // Leer el JSON actual si existe
            if (FILE.exists()) {
                try (Reader reader = new FileReader(FILE)) {
                    root = JsonParser.parseReader(reader).getAsJsonObject();
                } catch (Exception e) {
                    System.err.println("Error leyendo JSON existente:");
                    e.printStackTrace();
                }
            }

            // Obtener o crear el objeto "savedCameras"
            JsonObject camerasObject;
            if (root.has("savedCameras") && root.get("savedCameras").isJsonObject()) {
                camerasObject = root.getAsJsonObject("savedCameras");
            } else {
                camerasObject = new JsonObject();
                root.add("savedCameras", camerasObject);
            }

            // Crear la entrada de la nueva cámara
            JsonObject cameraData = new JsonObject();
            cameraData.addProperty("yaw", yaw);
            cameraData.addProperty("pitch", pitch);
            camerasObject.add(name, cameraData);

            // Guardar el JSON actualizado
            try (Writer writer = new FileWriter(FILE)) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                gson.toJson(root, writer);
            }

        } catch (Exception e) {
            System.err.println("Error al guardar la cámara:");
            e.printStackTrace();
        }
    }

    public record CameraData(float yaw, float pitch) {}

    private static void loadFromDisk() {
        if (!FILE.exists()) {
            loaded = true; // para evitar intentar cargar varias veces si no existe
            return;
        }

        try (Reader reader = new FileReader(FILE)) {
            SavedCamerasFile file = GSON.fromJson(reader, SavedCamerasFile.class);
            if (file != null && file.savedCameras != null) {
                savedCameras.clear();
                savedCameras.putAll(file.savedCameras);
            }
            loaded = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class SavedCamerasFile {
        public String modVersion;
        public Map<String, CameraData> savedCameras;
    }

    public static Collection<String> getSavedCameraNames() {
        if (!FILE.exists()) return Set.of();

        try (Reader reader = new FileReader(FILE)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            if (!root.has("savedCameras")) return Set.of();

            JsonObject cameras = root.getAsJsonObject("savedCameras");
            return cameras.keySet();

        } catch (Exception e) {
            e.printStackTrace();
            return Set.of();
        }
    }
    public static boolean deleteCamera(String name) {
        if (!loaded) loadFromDisk();
        if (savedCameras.containsKey(name)) {
            savedCameras.remove(name);
            ConfigManager.save(); // Guardar el nuevo estado del mapa
            return true;
        }
        return false;
    }
*/