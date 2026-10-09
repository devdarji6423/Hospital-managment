package com.hms.ui;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import java.awt.Color;
import java.util.function.Supplier;
import javax.swing.UIManager;

/** Loads the line icons in /com/hms/icons, tinted to match the theme. */
public final class Icons {

    private Icons() {
    }

    /** Icon in the normal text colour (follows light/dark switches). */
    public static FlatSVGIcon get(String name, int size) {
        return tinted(name, size, () -> UIManager.getColor("Label.foreground"));
    }

    public static FlatSVGIcon get(String name, int size, Color color) {
        return tinted(name, size, () -> color);
    }

    private static FlatSVGIcon tinted(String name, int size, Supplier<Color> color) {
        FlatSVGIcon icon = new FlatSVGIcon("com/hms/icons/" + name + ".svg", size, size);
        icon.setColorFilter(new FlatSVGIcon.ColorFilter(c -> color.get()));
        return icon;
    }
}
