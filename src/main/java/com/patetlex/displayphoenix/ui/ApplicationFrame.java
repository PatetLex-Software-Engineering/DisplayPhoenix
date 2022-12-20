package com.patetlex.displayphoenix.ui;


import com.patetlex.displayphoenix.Application;
import com.patetlex.displayphoenix.init.ColorInit;
import com.patetlex.displayphoenix.util.ComponentHelper;
import org.cef.ui.WebPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ApplicationFrame extends JFrame implements ComponentListener, Cloneable {

    public static boolean CAN_EXIT = true;

    private List<JWindow> topLayers = new ArrayList<>();
    private int topLayerOffX;
    private int topLayerOffY;
    private int closeActionCache;
    private int widthCache;
    private int heightCache;
    private boolean resizableCache;
    private Application.IOpenWindow openListener;

    private List<Runnable> revalidationListeners = new ArrayList<>();

    public ApplicationFrame(String title, int closeAction, int width, int height, boolean resizable, Application.IOpenWindow openListener) {
        super(title);
        this.closeActionCache = closeAction;
        this.widthCache = width;
        this.heightCache = height;
        this.openListener = openListener;
        this.resizableCache = resizable;
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

    public void open() {
        open(this);
    }

    public static void open(ApplicationFrame frame) {
        frame.setIconImage(Application.getIcon().getImage());
        frame.setDefaultCloseOperationSuper(frame.closeActionCache == JFrame.EXIT_ON_CLOSE ? JFrame.DISPOSE_ON_CLOSE : frame.closeActionCache);
        frame.setBounds(0, 0, frame.widthCache, frame.heightCache);
        frame.getContentPane().setBackground(Application.getTheme().getColorTheme().getPrimaryColor());

        frame.openListener.creation(frame);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
                if (CAN_EXIT && frame.closeActionCache == JFrame.EXIT_ON_CLOSE) {
                    Application.exit(0);
                }
            }
        });

        frame.setResizable(frame.resizableCache);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public void addTopLayer(Component component) {
        addTopLayer(component, 0, 0);
    }

    public void addTopLayer(Component component, int offsetX, int offsetY) {
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
        addTopLayer(frame, offsetX, offsetY);
    }

    public void addTopLayer(JWindow window, int offsetX, int offsetY) {
        closeTopLayer();
        this.topLayers.add(window);
        this.topLayerOffX = offsetX;
        this.topLayerOffY = offsetY;
        window.setLocation(getX() + Math.round((getWidth() / 2F) - (window.getWidth() / 2F)) + this.topLayerOffX, getY() + Math.round((getHeight() / 2F) - (window.getHeight() / 2F)) + this.topLayerOffY);
        window.setAlwaysOnTop(true);
        window.setVisible(true);
    }

    public void closeTopLayer() {
        for (JWindow window : this.topLayers) {
            window.setAlwaysOnTop(false);
            window.setVisible(false);
            window.dispose();
        }
        this.topLayers.clear();
    }

    public boolean isTopLayerUsed() {
        return this.topLayers.size() > 0;
    }

    public void addRevalidationListener(Runnable runnable) {
        this.revalidationListeners.add(runnable);
    }

    public Application.IOpenWindow getOpenListener() {
        return openListener;
    }

    @Override
    public int getDefaultCloseOperation() {
        return this.closeActionCache;
    }

    @Override
    public void setDefaultCloseOperation(int operation) {
        super.setDefaultCloseOperation(operation);
        this.closeActionCache = operation;
    }

    protected void setDefaultCloseOperationSuper(int operation) {
        super.setDefaultCloseOperation(operation);
    }

    @Override
    public boolean isResizable() {
        return this.resizableCache;
    }

    @Override
    public void setResizable(boolean resizable) {
        this.resizableCache = resizable;
        super.setResizable(resizable);
    }

    @Override
    public void componentResized(ComponentEvent e) {
        for (JWindow window : this.topLayers) {
            window.setLocation(getX() + Math.round((getWidth() / 2F) - (window.getWidth() / 2F)) + this.topLayerOffX, getY() + Math.round((getHeight() / 2F) - (window.getHeight() / 2F)) + this.topLayerOffY);
        }
    }

    @Override
    public void componentMoved(ComponentEvent e) {
        for (JWindow window : this.topLayers) {
            window.setLocation(getX() + Math.round((getWidth() / 2F) - (window.getWidth() / 2F)) + this.topLayerOffX, getY() + Math.round((getHeight() / 2F) - (window.getHeight() / 2F)) + this.topLayerOffY);
        }
    }

    @Override
    public void componentShown(ComponentEvent e) {

    }

    @Override
    public void componentHidden(ComponentEvent e) {

    }

    @Override
    public void revalidate() {
        super.revalidate();
        for (Runnable runnable : this.revalidationListeners) {
            runnable.run();
        }
    }

    @Override
    public Object clone() {
        ApplicationFrame newFrame = new ApplicationFrame(this.getTitle(), this.getDefaultCloseOperation(), this.getWidth(), this.getHeight(), this.isResizable(), this.getOpenListener());
        newFrame.setBackground(this.getBackground());
        newFrame.setForeground(this.getForeground());
        ComponentHelper.forEachSubComponentOf(newFrame, new Consumer<Component>() {
            @Override
            public void accept(Component component) {
                Color lBColor = UIManager.getColor("Label.background");
                Color lFColor = UIManager.getColor("Label.foreground");
                Color bBColor = UIManager.getColor("Button.background");
                Color bFColor = UIManager.getColor("Button.foreground");
                if (lFColor.equals(component.getForeground()) || bFColor.equals(component.getForeground())) {
                    component.setForeground(component instanceof JLabel || component instanceof JButton || component instanceof JComboBox ? Application.getTheme().getColorTheme().getTextColor() : Application.getTheme().getColorTheme().getSecondaryColor());
                }
                if (lBColor.equals(component.getBackground()) || bBColor.equals(component.getBackground())) {
                    component.setBackground(Application.getTheme().getColorTheme().getPrimaryColor());
                }
                if (component.getFont() == UIManager.getFont("Label.font") || component.getFont() == UIManager.getFont("Button.font") || component.getFont() == UIManager.getFont("defaultFont")) {
                    component.setFont(component.getFont() != null ? Application.getTheme().getFont().deriveFont(component.getFont().getSize()) : Application.getTheme().getFont());
                }
            }
        });
        newFrame.setLocation(this.getLocation());
        return newFrame;
    }
}
