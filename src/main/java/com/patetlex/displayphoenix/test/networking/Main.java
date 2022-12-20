package com.patetlex.displayphoenix.test.networking;

import com.patetlex.displayphoenix.Application;
import com.patetlex.displayphoenix.enums.WidgetStyle;
import com.patetlex.displayphoenix.system.web.DeviceConnection;
import com.patetlex.displayphoenix.ui.ColorTheme;
import com.patetlex.displayphoenix.ui.Theme;
import com.patetlex.displayphoenix.ui.widget.TextField;
import com.patetlex.displayphoenix.util.ImageHelper;
import com.patetlex.displayphoenix.util.PanelHelper;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.function.Consumer;

public class Main {

    /**
     * Testing/Creating networking package
     *
     * @param args
     */
    public static void main(String[] args) {
        Theme theme = new Theme(new ColorTheme(Color.GRAY, Color.WHITE, Color.BLACK), WidgetStyle.POPPING, Font.getFont(Font.SERIF));
        ImageIcon icon = ImageHelper.getImage("popping_warning");

        Application.create(Application.class, icon, theme);

        try {
            DeviceConnection.startServer(1234, new Consumer<byte[]>() {
                @Override
                public void accept(byte[] bytes) {
                    System.out.println(new String(bytes));
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            DeviceConnection.connectTo("localhost", 1234, new Consumer<byte[]>() {
                @Override
                public void accept(byte[] bytes) {
                    System.out.println(new String(bytes));
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
