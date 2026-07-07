package com.freezecameramod.comun;

import net.minecraft.text.Text;

public class FormatText {
    public static final String COLOR_VARIABLE = "§a";

    // Formatea una variable como texto literal con color
    public static Text varDefault(String contenido) {
        return Text.literal(COLOR_VARIABLE + contenido + "§r"); // §r resetea formato al final
    }
    // Añadir otro color
    public static Text varColor(String contenido, String codigoColor) {
        return Text.literal(codigoColor + contenido + "§r");
    }
}
