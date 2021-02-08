package com.patetlex.displayphoenix.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.patetlex.displayphoenix.Application;
import com.patetlex.displayphoenix.bitly.Bitly;
import com.patetlex.displayphoenix.bitly.elements.BitSave;
import com.patetlex.displayphoenix.bitly.elements.BitWidgetStyle;
import com.patetlex.displayphoenix.bitly.ui.BitWidget;
import com.patetlex.displayphoenix.file.Data;
import com.patetlex.displayphoenix.ui.ApplicationFrame;
import com.patetlex.displayphoenix.ui.widget.FadeOnHoverWidget;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class BitlyHelper {

    private static final Gson gson = new Gson();

    public static void loadBitResource(String identifier) {
        loadBitResource(identifier, identifier);
    }

    public static void loadBitResource(String identifier, String path) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(ClassLoader.getSystemClassLoader().getResourceAsStream("bitly/" + path + ".json")));

            StringBuilder output = new StringBuilder();
            String out;
            while ((out = reader.readLine()) != null) {
                output.append(out + "\n");
            }
            Bitly.loadBit(identifier, output.toString(), ImageHelper.renderImage(ImageHelper.getImage("bitly/" + identifier + ".png").getImage()));
            JsonObject bitObject = gson.fromJson(output.toString(), JsonObject.class);
            List<BitWidget[]> widgets = gson.fromJson(bitObject.get("widgets").toString(), new TypeToken<List<BitWidget[]>>() {}.getType());
            Data.cache(null, "/bitly/");
            Data.cache(null, "/bitly/elements/");
            for (BitWidget[] page : widgets) {
                for (BitWidget widget : page) {
                    if (widget.getStyle() == BitWidgetStyle.CANVAS) {
                        for (String fileName : widget.getExternalFiles(true, null).keySet()) {
                            Data.cache(widget.getExternalFiles(true, null).get(fileName), "/bitly/elements/" + fileName);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static JPanel getBitSelectionPanel(ApplicationFrame frame, List<BitSave> bits) {
        return getBitSelectionPanel(frame, bits, new MouseAdapter() {});
    }
    public static JPanel getBitSelectionPanel(ApplicationFrame frame, List<BitSave> bits, MouseListener mouseListener) {
        class Renderer extends JLabel implements ListCellRenderer<BitSave> {
            @Override
            public Component getListCellRendererComponent(JList<? extends BitSave> list, BitSave value, int index, boolean isSelected, boolean cellHasFocus) {
                setIcon(ImageHelper.resize(value.getImplementedBit().getBit().getIcon(), 48));
                setText(String.valueOf(value.getImplementedBit().getValue("NAME")));
                float r = frame.getWidth() * 0.15F;
                setPreferredSize(new Dimension(Math.round(r), Math.round(r / 4)));
                setBackground(isSelected ? Application.getTheme().getColorTheme().getAccentColor() : Application.getTheme().getColorTheme().getSecondaryColor());
                setFont(Application.getTheme().getFont());
                setHorizontalAlignment(SwingConstants.CENTER);
                setForeground(Application.getTheme().getColorTheme().getTextColor());
                setOpaque(false);
                return this;
            }

            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(getBackground());
                g.fillRoundRect(0, 0, this.getWidth(), this.getHeight(), this.getHeight(), this.getHeight());
                super.paintComponent(g);
            }
        }
        return getBitSelectionPanel(frame, bits, new Renderer(), mouseListener);
    }
    public static JPanel getBitSelectionPanel(ApplicationFrame frame, List<BitSave> bits, ListCellRenderer<BitSave> renderer, MouseListener mouseListener) {
        JList<BitSave> bitJList = ComponentHelper.createJList(renderer, bits);
        bitJList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        bitJList.setVisibleRowCount(-1);
        bitJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bitJList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getClickCount() == 2) {
                    frame.getContentPane().removeAll();

                    FadeOnHoverWidget exit = new FadeOnHoverWidget(new ImageIcon(ImageHelper.overlay(ImageHelper.rotate(ImageHelper.getImage(Application.getTheme().getWidgetStyle().getName() + "_plus").getImage(), 45), Application.getTheme().getColorTheme().getAccentColor(), 1)), new ImageIcon(ImageHelper.overlay(ImageHelper.rotate(ImageHelper.getImage(Application.getTheme().getWidgetStyle().getName() + "_plus").getImage(), 45), Color.RED, 1)), 0.05F);
                    exit.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            super.mouseClicked(e);
                            ApplicationFrame.open((ApplicationFrame) frame.clone());
                            frame.dispose();
                        }
                    });
                    exit.setPreferredSize(new Dimension(150, 150));

                    frame.add(PanelHelper.centerAndSouthElements(bitJList.getSelectedValue().getBitPanel(), PanelHelper.join(exit)));
                    frame.getContentPane().revalidate();
                    frame.getContentPane().repaint();
                } else {
                    mouseListener.mouseClicked(e);
                }
            }
        });
        JScrollPane scrollBar = new JScrollPane(bitJList);
        scrollBar.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        return PanelHelper.join(bitJList);
    }
}
