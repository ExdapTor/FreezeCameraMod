package com.freezecameramod.config;

import com.google.gson.*;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.mojang.text2speech.Narrator.LOGGER;

public class ConfigManager {
    private static boolean configError=false; //Por si hay algun error

    // Versión actual del mod. Se usa para detectar si el archivo de configuración está desactualizado.
    private static final String CURRENT_MOD_VERSION = "${version}";
    // Objeto que contiene la configuración actual cargada desde el archivo JSON.
    public static FreezeCameraConfig config;
    // Instancia de Gson configurada para guardar JSON con formato legible (espacios, saltos de línea).
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Devuelve la ruta completa del archivo de configuración,
     * dentro de la carpeta ".minecraft/config/freezecameramod/freezecamera.json"
     */
    private static Path getConfigPath() {
        Path path = MinecraftClient.getInstance().runDirectory.toPath() // Carpeta raíz del instance de Minecraft.
                .resolve("config")
                .resolve("freezecameramod")
                .resolve("freezecamera.json");
        System.out.println("getConfigPath() calling: " + path.toAbsolutePath());
        return path;
    }

    /**
     * Carga la configuración desde disco.
     * Si el archivo no existe, crea uno nuevo con valores por defecto.
     * Si hay error leyendo (ej JSON mal formado), avisa por consola y chat,
     * carga valores por defecto y guarda el archivo.
     */
    public static FreezeCameraConfig loadConfig() {
        try {
            System.out.println("[FreezeCameraMod] Loading config...");
            Path path = getConfigPath();
            // Archivo no existe: crear config nueva por defecto y guardar
            if (!Files.exists(path)) {
                System.out.println("[FreezeCameraMod] Config file doesn't exist. Making it...");
                config = new FreezeCameraConfig();
                config.modVersion = ModVersion.VERSION;
                saveConfig(); // Crear archivo por defecto
            } else {
                // Si el archivo existe, se lee el contenido como texto
                String rawJson = Files.readString(path);

                // Pasar el texto a JSON
                JsonObject json = JsonParser.parseString(rawJson).getAsJsonObject();

                // Deserializar el JSON directamente a un objeto FreezeCameraConfig
                config = gson.fromJson(json, FreezeCameraConfig.class);

                // Si falta modVersion o es distinta, actualizar
                boolean needsUpdate = !json.has("modVersion") ||
                        !CURRENT_MOD_VERSION.equals(json.get("modVersion").getAsString());
                if(needsUpdate) {
                    System.out.println("[FreezeCameraMod] The config needs a update.\n[FreezeCameraMod] Updating...");
                    mergeMissingFields(json); // Pasa el json del archivo
                    config.modVersion = ModVersion.VERSION; //Guardar correctamente la version
                    saveConfig();
                }
            }
            config.onLoaded(); // ← ¡IMPORTANTE!
        } catch (IOException | JsonSyntaxException e) {
            // Error leyendo configuración: aviso en consola y chat,
            // Crear backup
            backupConfigFile();

            // se carga config por defecto y se guarda para evitar crasheos
            System.err.println("[FreezeCameraMod] Config error, applying default values");

            config = new FreezeCameraConfig();
            config.modVersion = CURRENT_MOD_VERSION;
            saveConfig();

            MinecraftClient client = MinecraftClient.getInstance();
            LOGGER.warn(I18n.translate(
                    "message.freezecamera.configError"));
            configError = true;
        }

        return config;
    }

    /**
     * Guarda la configuración actual en disco, creando carpetas si no existen
     */
    public static void saveConfig() {
        Path path = getConfigPath();
        try {
            Files.createDirectories(path.getParent()); //Asegura que la carpeta exista
            Files.writeString(path, gson.toJson(config));
            System.out.println("[FreezeCameraMod] Config saved in: " + path.toAbsolutePath());
        } catch (IOException e) {
            System.out.println("[FreezeCameraMod] Error saving config: " + e.getMessage());
        }
    }

    /**
     * Auxiliar para añadir campos que falten en config (por ejemplo, tras actualizar mod)
     * Se usa reflexión para copiar campos desde config por defecto a config actual si faltan
     */
    private static void mergeMissingFields(JsonObject json) {
        //Configuracion por defecto en el codigo
        FreezeCameraConfig defaultConfig = new FreezeCameraConfig();
        //Asegurarse de que la version sea correcta (no necesario, pero se asegura)
        defaultConfig.modVersion = CURRENT_MOD_VERSION; //Version del codigo

        try{
            // Recorre todos los campos públicos definidos en la clase FreezeCameraConfig
            for (Field field : FreezeCameraConfig.class.getFields()) {
                // Si el campo no existe en el JSON original, se copia desde la configuración por defecto
                if (!json.has(field.getName())) {
                    field.set(config, field.get(defaultConfig)); // Se actualiza el campo faltante en config
                }
            }
        } catch (IllegalStateException | IllegalAccessException e) {
            // Si ocurre un error, se imprime el mensaje
            System.err.println("[FreezeCameraMod] Config update error: " + e.getMessage());
        }
    }

    public static boolean hasConfigError() {
        return configError;
    }

    public static FreezeCameraConfig getConfig() {
        return config;
    }

    public static void backupConfigFile() {
        Path path = getConfigPath();
        File configFile = path.toFile();

        if (!Files.exists(path)) {
            System.out.println("[FreezeCameraMod] Backup canceled: the config file doesn't exist.");
            return;
        }

        try {
            String timestamp = new SimpleDateFormat("yyyy.MM.dd_HH.mm.ss").format(new Date());
            File backupFile = new File(configFile.getParentFile(), "freeze_camera_backup_" + timestamp + ".json");
            Files.copy(configFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("[FreezeCameraMod] Backup saved in: " + backupFile.getName());
        } catch (IOException e) {
            System.err.println("[FreezeCameraMod] Error saving backup: " + e.getMessage());
        }
    }

}
