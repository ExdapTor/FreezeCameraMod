package com.freezecameramod.comun;

import net.minecraft.network.chat.Component;

public class FormatText {
    public static final String COLOR_VARIABLE = "§a";

    // Formatea una variable como texto literal con color
    public static Component varDefault(String contenido) {
        return Component.literal(COLOR_VARIABLE + contenido + "§r"); // §r resetea formato al final
    }
    // Añadir otro color
    public static Component varColor(String contenido, String codigoColor) {
        return Component.literal(codigoColor + contenido + "§r");
    }
}
