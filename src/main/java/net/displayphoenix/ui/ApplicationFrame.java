package net.displayphoenix.ui;


import net.displayphoenix.Application;
import net.displayphoenix.init.ColorInit;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ApplicationFrame extends JFrame implements ComponentListener {

    private JWindow topLayer;
    private int topLayerOffX;
    private int topLayerOffY;

    public ApplicationFrame(String title) {
        super(title);
        this.addComponentListener(this);
        this.addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowLostFocus(WindowEvent e) {
                super.windowLostFocus(e);
                closeTopLayer();
            }
        });
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
                closeTopLayer();
            }
        });
    }

    public void setTopLayer(Component component) {
        setTopLayer(component, 0, 0);
    }

    public void setTopLayer(Component component, int offsetX, int offsetY) {
        JWindow frame = new JWindow() {
            @Override
            protected void addImpl(Component comp, Object constraints, int index) {
                super.addImpl(comp, constraints, index);
                Color lBColor = UIManager.getColor("Label.background");
                Color lFColor = UIManager.getColor("Label.foreground");
                Color bBColor = UIManager.getColor("Button.background");
                Color bFColor = UIManager.getColor("Button.foreground");
                if (lFColor.equals(comp.getForeground()) || bFColor.equals(comp.getForeground())) {
                    comp.setForeground(comp instanceof JLabel || comp instanceof JButton || comp instanceof JComboBox ? Application.getTheme().getColorTheme().getTextColor() : Application.getTheme().getColorTheme().getSecondaryColor());
                }
                if (lBColor.equals(comp.getBackground()) || bBColor.equals(comp.getBackground())) {
                    comp.setBackground(Application.getTheme().getColorTheme().getPrimaryColor());
                }
                comp.setFont(comp.getFont() != null ? Application.getTheme().getFont().deriveFont(comp.getFont().getSize()) : Application.getTheme().getFont());
                if (comp instanceof Container) {
                    try {
                        ((Container) comp).addContainerListener(new ContainerAdapter() {
                            @Override
                            public void componentAdded(ContainerEvent e) {
                                if (lFColor.equals(e.getComponent().getForeground()) || bFColor.equals(e.getComponent().getForeground())) {
                                    e.getComponent().setForeground(e.getComponent() instanceof JLabel || e.getComponent() instanceof JButton || e.getComponent() instanceof JComboBox ? Application.getTheme().getColorTheme().getTextColor() : Application.getTheme().getColorTheme().getSecondaryColor());
                                }
                                if (lBColor.equals(e.getComponent().getBackground()) || bBColor.equals(e.getComponent().getBackground())) {
                                    e.getComponent().setBackground(Application.getTheme().getColorTheme().getPrimaryColor());
                                }
                            }
                        });
                    } catch (NoClassDefFoundError error) {
                        error.printStackTrace();
                    }
                    new Object() {
                        private void setColors(Container container) {
                            for (Component component : container.getComponents()) {
                                if (lFColor.equals(component.getForeground()) || bFColor.equals(component.getForeground())) {
                                    component.setForeground(component instanceof JLabel || component instanceof JButton || component instanceof JComboBox ? Application.getTheme().getColorTheme().getTextColor() : Application.getTheme().getColorTheme().getSecondaryColor());
                                }
                                if (lBColor.equals(component.getBackground()) || bBColor.equals(component.getBackground())) {
                                    component.setBackground(Application.getTheme().getColorTheme().getPrimaryColor());
                                }
                                component.setFont(component.getFont() != null ? Application.getTheme().getFont().deriveFont(component.getFont().getSize()) : Application.getTheme().getFont());
                                if (component instanceof Container) {
                                    setColors((Container) component);
                                    ((Container) component).addContainerListener(new ContainerAdapter() {
                                        @Override
                                        public void componentAdded(ContainerEvent e) {
                                            if (lFColor.equals(e.getComponent().getForeground())) {
                                                e.getComponent().setForeground(e.getComponent() instanceof JLabel || comp instanceof JButton || comp instanceof JComboBox ? Application.getTheme().getColorTheme().getTextColor() : Application.getTheme().getColorTheme().getSecondaryColor());
                                            }
                                            if (lBColor.equals(e.getComponent().getBackground())) {
                                                e.getComponent().setBackground(Application.getTheme().getColorTheme().getPrimaryColor());
                                            }
                                            if (e.getComponent() instanceof Container) {
                                                setColors((Container) e.getComponent());
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    }.setColors((Container) comp);
                }
            }
        };
        frame.setBackground(ColorInit.TRANSPARENT);
        frame.add(component);
        frame.setBounds(0, 0, component.getWidth(), component.getHeight());
        frame.pack();
        setTopLayer(frame, offsetX, offsetY);
    }

    public void setTopLayer(JWindow window, int offsetX, int offsetY) {
        closeTopLayer();
        this.topLayer = window;
        this.topLayerOffX = offsetX;
        this.topLayerOffY = offsetY;
        this.topLayer.setLocation(getX() + Math.round((getWidth() / 2F) - (this.topLayer.getWidth() / 2F)) + this.topLayerOffX, getY() + Math.round((getHeight() / 2F) - (this.topLayer.getHeight() / 2F)) + this.topLayerOffY);
        this.topLayer.setAlwaysOnTop(true);
        this.topLayer.setVisible(true);
    }

    public void closeTopLayer() {
        if (this.topLayer != null) {
            this.topLayer.setAlwaysOnTop(false);
            this.topLayer.setVisible(false);
            this.topLayer.dispose();
            this.topLayer = null;
        }
    }

    public boolean isTopLayerUsed() {
        return this.topLayer != null;
    }

    @Override
    public void componentResized(ComponentEvent e) {
        if (this.topLayer != null) {
            this.topLayer.setLocation(getX() + Math.round((getWidth() / 2F) - (this.topLayer.getWidth() / 2F)) + this.topLayerOffX, getY() + Math.round((getHeight() / 2F) - (this.topLayer.getHeight() / 2F)) + this.topLayerOffY);
        }
    }

    @Override
    public void componentMoved(ComponentEvent e) {
        if (this.topLayer != null) {
            this.topLayer.setLocation(getX() + Math.round((getWidth() / 2F) - (this.topLayer.getWidth() / 2F)) + this.topLayerOffX, getY() + Math.round((getHeight() / 2F) - (this.topLayer.getHeight() / 2F)) + this.topLayerOffY);
        }
    }

    @Override
    public void componentShown(ComponentEvent e) {

    }

    @Override
    public void componentHidden(ComponentEvent e) {

    }
}
