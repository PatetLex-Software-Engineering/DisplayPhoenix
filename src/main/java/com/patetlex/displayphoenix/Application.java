package com.patetlex.displayphoenix;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.patetlex.displayphoenix.bitly.Bitly;
import com.patetlex.displayphoenix.bitly.elements.BitWidgetStyle;
import com.patetlex.displayphoenix.blockly.Blockly;
import com.patetlex.displayphoenix.canvasly.tools.Tool;
import com.patetlex.displayphoenix.canvasly.tools.impl.*;
import com.patetlex.displayphoenix.enums.WidgetStyle;
import com.patetlex.displayphoenix.exception.AppNotCreatedException;
import com.patetlex.displayphoenix.file.Data;
import com.patetlex.displayphoenix.file.indexly.Indexly;
import com.patetlex.displayphoenix.gamely.Gamely;
import com.patetlex.displayphoenix.gamely.engine.GameEngine;
import com.patetlex.displayphoenix.gamely.engine.impl.GameEngine3D;
import com.patetlex.displayphoenix.gamely.physics.impl.GamePhysics2D;
import com.patetlex.displayphoenix.gamely.ui.GamePanel;
import com.patetlex.displayphoenix.gamely.ui.ext.GameGLFWPanel;
import com.patetlex.displayphoenix.generation.Module;
import com.patetlex.displayphoenix.init.ColorInit;
import com.patetlex.displayphoenix.interfaces.FileIteration;
import com.patetlex.displayphoenix.lang.Local;
import com.patetlex.displayphoenix.lang.Localizer;
import com.patetlex.displayphoenix.maps.Maps;
import com.patetlex.displayphoenix.maps.event.IMapListener;
import com.patetlex.displayphoenix.maps.event.MapEvent;
import com.patetlex.displayphoenix.maps.event.events.MarkerClickEvent;
import com.patetlex.displayphoenix.maps.ui.MapPanel;
import com.patetlex.displayphoenix.system.exe.SystemProcessor;
import com.patetlex.displayphoenix.ui.ApplicationFrame;
import com.patetlex.displayphoenix.ui.ColorTheme;
import com.patetlex.displayphoenix.ui.Theme;
import com.patetlex.displayphoenix.ui.animation.Clipper;
import com.patetlex.displayphoenix.ui.widget.OverlayOnHoverWidget;
import com.patetlex.displayphoenix.ui.widget.RoundedButton;
import com.patetlex.displayphoenix.util.*;
import org.cef.CefApp;
import org.cef.ui.WebPanel;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL11;;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author TBroski
 */
public class Application {

    private static final SystemProcessor systemProcessor = new SystemProcessor();
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private static final int BUTTON_WIDTH = 100;
    private static final int BUTTON_HEIGHT = 20;

    private static Class exeClass;
    private static String version;
    private static boolean CREATED;
    private static String title;
    private static ImageIcon icon;
    private static Theme theme;
    private static Local selectedLocal = Local.EN_US;

    private static int session;

    private static List<Runnable> runOnCreate = new ArrayList<>();
    private static List<ApplicationFrame> openFrames = new ArrayList<>();

    /**
     * Creates the app, used for organization and constants.
     *
     * @param appIcon The app icon.
     * @param appTheme Theme of the app, subject to change.
     */
    public static void create(Class mainClass, ImageIcon appIcon, Theme appTheme) {
        exeClass = mainClass;
        title = readAppProperty("name");
        icon = appIcon;
        theme = appTheme;
        version = readAppProperty("version");
        CREATED = true;

        CefApp.getInstance().dispose();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                dispose();
            }
        });

        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.WARNING);
        logger.setUseParentHandlers(false);
        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException e) {
            e.printStackTrace();
        }
        System.out.println("- Native hook registered");

        OpenCVHelper.create();
        System.out.println("- OpenCV registered");


        Module.registerModule(Module.JAVA);
        Module.registerModule(Module.JAVASCRIPT);
        Module.registerModule(Module.LUA);
        System.out.println("- Code (Module) generators registered");

        Bitly.registerWidgetStyle(BitWidgetStyle.TOGGLE);
        Bitly.registerWidgetStyle(BitWidgetStyle.TEXT);
        Bitly.registerWidgetStyle(BitWidgetStyle.NUMBER);
        Bitly.registerWidgetStyle(BitWidgetStyle.BLOCKLY);
        Bitly.registerWidgetStyle(BitWidgetStyle.RESOURCE);
        Bitly.registerWidgetStyle(BitWidgetStyle.CANVAS);
        Bitly.registerWidgetStyle(BitWidgetStyle.IMAGE);
        Bitly.registerWidgetStyle(BitWidgetStyle.OPTIONS);
        System.out.println("- Bitly widget styles registered");

        Localizer.create();
        System.out.println("- Translations loaded");

        session = Data.create();
        System.out.println("- Saved values loaded");

        Blockly.load();
        System.out.println("- Blockly loaded");

        Bitly.load();
        System.out.println("- Bitly loaded");

        Gamely.load();
        System.out.println("- Gamely loaded");

        Indexly.registerExtension(StringHelper.id(title), appIcon);

        Tool.REGISTERED_TOOLS.add(new BucketTool());
        Tool.REGISTERED_TOOLS.add(new EraserTool());
        Tool.REGISTERED_TOOLS.add(new ImageTool());
        Tool.REGISTERED_TOOLS.add(new PencilTool());
        Tool.REGISTERED_TOOLS.add(new PickerTool());
        Tool.REGISTERED_TOOLS.add(new TextTool());
        System.out.println("- Canvasly tools registered");

        for (Runnable runnable : runOnCreate) {
            runnable.run();
        }
    }

    /**
     * Safely exit the application.
     *
     * @param status  Exit status
     */
    public static void exit(int status) {
        CefApp.getInstance().dispose();
        System.exit(status);
    }

    /**
     * Disposes and reopens all ApplicationFrames
     *
     * @see ApplicationFrame#ApplicationFrame(String, int, int, int, boolean, IOpenWindow) 
     */
    public static void softRestart() {
        List<ApplicationFrame> validFrames = new ArrayList<>();
        for (ApplicationFrame frame : openFrames) {
            if (frame.isVisible()) {
                validFrames.add(frame);
            }
        }
        openFrames.clear();
        for (ApplicationFrame frame : validFrames) {
            ApplicationFrame newFrame = (ApplicationFrame) frame.clone();
            newFrame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    super.windowClosed(e);
                    openFrames.remove(newFrame);
                }
            });
            frame.dispose();
            ApplicationFrame.open(newFrame);
            openFrames.add(newFrame);
        }
    }

    /**
     * Fully restart the app, executes main file
     */
    public static void restart() {
        File exe = getExecutable();
        if (!exe.isDirectory()) {
            session--;
            getSystemProcessor().executeFile(exe);
        }
        exit(0);
    }

    private static void dispose() {
        CefApp.getInstance().dispose();
        try {
            GlobalScreen.unregisterNativeHook();
        } catch (NativeHookException var1) {
            var1.printStackTrace();
        }
        Data.save();
        String tmp = System.getProperty("java.io.tmpdir");
        if (tmp != null) {
            FileHelper.forEachSubFile(new File(tmp), new FileIteration() {
                public void iterate(File file) {
                    if (file != null && file.getName() != null) {
                        if (file.getName().contains("JNativeHook")) {
                            file.delete();
                        }
                    }
                }
            });
        }
        System.out.println(getTitle() + " safely exited.");
    }

    /**
     * Opens a simple text window, non editable. Used for possible errors with no expectation
     *
     * @param paste Text in text field
     * @return Returns opened window
     */
    public static JFrame openPaste(String paste) {
        return openWindow("Paste", JFrame.DISPOSE_ON_CLOSE, parentFrame -> {
            JTextArea textField = new JTextArea(paste);
            textField.setColumns(20);
            textField.setEditable(false);
            textField.setForeground(getTheme().getColorTheme().getTextColor());
            textField.setBackground(getTheme().getColorTheme().getPrimaryColor());
            parentFrame.add(textField);
        }, 600, 1000, true);
    }

    /**
     * Opens a window within themed wrapper
     * All subcomponents are initially wrapped in color theme unless already changed
     *
     * @param title Title of window
     * @param closeAction Close action of window
     * @param windowCreation Init components
     * @param width Width of window
     * @param height Height of window
     * @return Returns the frame for additional manipulation
     */
    public static ApplicationFrame openWindow(String title, int closeAction, IOpenWindow windowCreation, int width, int height, boolean resizable) {
        ApplicationFrame frame = new ApplicationFrame(title, closeAction, width, height, resizable, windowCreation);
        ComponentHelper.forEachSubComponentOf(frame, new Consumer<Component>() {
            @Override
            public void accept(Component component) {
                Color lBColor = UIManager.getColor("Label.background");
                Color lFColor = UIManager.getColor("Label.foreground");
                Color bBColor = UIManager.getColor("Button.background");
                Color bFColor = UIManager.getColor("Button.foreground");
                if (lFColor.equals(component.getForeground()) || bFColor.equals(component.getForeground())) {
                    component.setForeground(component instanceof JLabel || component instanceof JButton || component instanceof JComboBox ? theme.getColorTheme().getTextColor() : theme.getColorTheme().getSecondaryColor());
                }
                if (lBColor.equals(component.getBackground()) || bBColor.equals(component.getBackground())) {
                    component.setBackground(theme.getColorTheme().getPrimaryColor());
                }
                if (component.getFont() == UIManager.getFont("Label.font") || component.getFont() == UIManager.getFont("Button.font") || component.getFont() == UIManager.getFont("defaultFont")) {
                    if (theme.getFont() != null)
                        component.setFont(component.getFont() != null ? theme.getFont().deriveFont((float) component.getFont().getSize()) : theme.getFont());
                }
            }
        });
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
                openFrames.remove(frame);
            }
        });
        frame.open();
        openFrames.add(frame);
        return frame;
    }
    public static ApplicationFrame openWindow(int closeAction, IOpenWindow windowCreation) {
        return openWindow(title, closeAction, windowCreation, theme.getWidth(), theme.getHeight(), theme.isResizeable());
    }
    public static ApplicationFrame openWindow(String title, IOpenWindow windowCreation) {
        return openWindow(title, JFrame.EXIT_ON_CLOSE, windowCreation, theme.getWidth(), theme.getHeight(), theme.isResizeable());
    }
    public static ApplicationFrame openWindow(String title, int closeAction, IOpenWindow windowCreation) {
        return openWindow(title, closeAction, windowCreation, theme.getWidth(), theme.getHeight(), theme.isResizeable());
    }
    public static ApplicationFrame openWindow(int closeAction, IOpenWindow windowCreation, int width, int height) {
        return openWindow(title, closeAction, windowCreation, width, height, theme.isResizeable());
    }
    public static ApplicationFrame openWindow(String title, int closeAction, IOpenWindow windowCreation, int width, int height) {
        return openWindow(title, closeAction, windowCreation, width, height, theme.isResizeable());
    }
    public static ApplicationFrame openWindow(IOpenWindow windowCreation) {
        return openWindow(JFrame.EXIT_ON_CLOSE, windowCreation);
    }

    /**
     * Simple way to obtain a JPanel containing theme widgets;
     * Each widget is represented by a generated template.
     *
     * @return  JPanel of theme widget
     */
    public static JPanel getColorThemeChangePanel(ColorTheme... themes) {
        Component[] widgets = new Component[themes.length];
        for (int i = 0; i < themes.length; i++) {
            int finalI = i;
            JPanel widget = new JPanel() {
                private Clipper clipper = new Clipper(0.005F, 0.5F, 0F).smooth();
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    g.setColor(themes[finalI].getPrimaryColor());
                    g.fillRect(0, 0, getWidth(), getHeight());
                    g.setColor(themes[finalI].getTextColor());
                    g.setFont(theme.getFont());
                    String label = Localizer.translate("label.example.text");
                    int height = (int) g.getFontMetrics().getStringBounds(label, g).getHeight();
                    g.drawString(label, 0, height);
                    g.setFont(g.getFont().deriveFont(g.getFont().getSize() * 0.75F));
                    g.drawString(label, 0, height * 2);
                    g.setFont(g.getFont().deriveFont(g.getFont().getSize() * 0.75F));
                    g.drawString(label, 0, height * 3);
                    g.setColor(themes[finalI].getSecondaryColor());
                    g.fillRect(0, getHeight() - Math.round(getHeight() * 0.3F), getWidth(), getHeight() - Math.round(getHeight() * 0.3F));
                    g.setColor(themes[finalI].getAccentColor());
                    g.fillRect(0, getHeight() - Math.round(getHeight() * 0.3F) - 5, getWidth(), 5);
                    Graphics2D g2d = (Graphics2D) g;
                    if (this.clipper.getCurrentValue() != 0) {
                        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, this.clipper.getCurrentValue()));
                        g.setColor(theme.getColorTheme().getSecondaryColor());
                        g.fillRect(0, 0, getWidth(), getHeight());
                    }
                    if (theme.getColorTheme().equals(themes[finalI])) {
                        ((Graphics2D) g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
                        g.setColor(theme.getColorTheme().getAccentColor());
                        g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                    }
                }
                public JPanel init() {
                    this.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseEntered(MouseEvent e) {
                            super.mouseEntered(e);
                            clipper.increment();
                        }

                        @Override
                        public void mouseExited(MouseEvent e) {
                            super.mouseExited(e);
                            clipper.decrement();
                        }
                    });
                    this.clipper.addListener(new Runnable() {
                        @Override
                        public void run() {
                            repaint();
                        }
                    });
                    return this;
                }
            }.init();
            widget.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                    Application.switchTheme(themes[finalI]);
                    for (Component component : widgets) {
                        component.repaint();
                    }
                    softRestart();
                }
            });
            widget.setCursor(new Cursor(Cursor.HAND_CURSOR));
            widget.setPreferredSize(new Dimension(150, 150));
            widgets[i] = PanelHelper.join(widget);
        }
        return PanelHelper.grid(3, widgets);
    }

    /**
     * Simple way to obtain a JPanel containing local widgets;
     * Each widget is represented by its respective flag.
     *
     * @return  JPanel of local widgets
     */
    public static JPanel getLocalChangePanel() {
        Component[] widgets = new Component[Local.values().length];
        for (int i = 0; i < Local.values().length; i++) {
            OverlayOnHoverWidget widget = new Object() {
                public OverlayOnHoverWidget getLocalWidget(Local local) {
                    OverlayOnHoverWidget localOverlay = new OverlayOnHoverWidget(ImageHelper.getImage("lang/" + local.getTag()), theme.getColorTheme().getSecondaryColor(), 0.5F, 0.005F) {
                        @Override
                        protected void paintComponent(Graphics g) {
                            super.paintComponent(g);
                            if (selectedLocal == local) {
                                ((Graphics2D) g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
                                g.setColor(theme.getColorTheme().getAccentColor());
                                g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                            }
                        }
                    };
                    localOverlay.setPreferredSize(new Dimension(100, 50));
                    localOverlay.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            selectedLocal = local;
                        }
                    });
                    return localOverlay;
                }
            }.getLocalWidget(Local.values()[i]);
            widget.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                    for (Component component : widgets) {
                        component.repaint();
                    }
                    softRestart();
                }
            });
            widgets[i] = PanelHelper.join(widget);
        }
        return PanelHelper.grid(3, widgets);
    }

    /**
     * Opens Ok prompt with themed components
     *
     * @param title Title of prompt
     * @param text Text of prompt
     * @param okListener Adds a listener to ok button
     * @param isWarning Whether it is a warning
     */
    public static void prompt(String title, String text, MouseListener okListener, boolean isWarning) {
        PromptedButton promptedButton = new PromptedButton("Ok", okListener).closeWindow();
        promptWithButtons(title, text, isWarning, promptedButton);
    }
    public static void prompt(String title, String text, boolean isWarning) {
        prompt(title, text, new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
            }
        }, isWarning);
    }

    /**
     * Opens Yes-No prompt
     *
     * @param title Title of window
     * @param text Text in window
     * @param isWarning Is a warning
     * @param yesListener Adds a listener to yes button
     * @param noListener Adds a listener to no button
     */
    public static void promptYesOrNo(String title, String text, boolean isWarning, MouseListener yesListener, MouseListener noListener) {
        promptWithButtons(title, text, isWarning, new PromptedButton(Localizer.translate("yes"), yesListener).closeWindow(), new PromptedButton(Localizer.translate("no"), noListener).closeWindow());
    }
    public static void promptYesOrNo(String title, String text, boolean isWarning, MouseListener yesListener) {
        promptWithButtons(title, text, isWarning, new PromptedButton(Localizer.translate("yes"), yesListener).closeWindow(), new PromptedButton(Localizer.translate("no"), new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
            }
        }).closeWindow());
    }

    /**
     * Opens customizable prompt
     *
     * @param title Title of window
     * @param text Text of window
     * @param isWarning Is a warning
     * @param promptedButtons Prompted buttons with listeners and text
     */
    public static void promptWithButtons(String title, String text, boolean isWarning, PromptedButton... promptedButtons) {
        if (!CREATED) {
            try {
                throw new AppNotCreatedException("App not created");
            } catch (AppNotCreatedException e) {
                e.printStackTrace();
            }
        }
        JFrame dialogBox = new JFrame(title);
        dialogBox.setLayout(new BorderLayout());
        dialogBox.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        dialogBox.setIconImage(isWarning ? ImageHelper.getImage(theme.getWidgetStyle().getName() + "_warning").getImage() : icon.getImage());
        dialogBox.setBackground(theme.getColorTheme().getPrimaryColor());

        JPanel panel = getPromptPanel(text, dialogBox, promptedButtons);
        panel.setOpaque(true);
        panel.setBackground(theme.getColorTheme().getPrimaryColor());
        dialogBox.add(panel, BorderLayout.CENTER);

        dialogBox.setResizable(false);
        dialogBox.setLocationRelativeTo(null);
        dialogBox.setAlwaysOnTop(isWarning);
        dialogBox.setVisible(true);
    }

    private static JPanel getPromptPanel(String text, JFrame dialogBox, PromptedButton... promptedButtons) {
        JPanel panel = new JPanel();
        if (dialogBox == null) {
            panel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    g.setColor(theme.getColorTheme().getPrimaryColor());
                    g.fillRoundRect(0, 0, getWidth(), getHeight(), 50, 50);
                }
            };
        }
        panel.setOpaque(false);
        panel.setBackground(ColorInit.TRANSPARENT);

        JPanel labelPanel = new JPanel();
        labelPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 10, 0));
        labelPanel.setOpaque(false);
        JLabel textLabel = new JLabel(text);
        textLabel.setForeground(theme.getColorTheme().getTextColor());
        textLabel.setHorizontalAlignment(SwingConstants.CENTER);
        textLabel.setFont(theme.getFont());
        if (dialogBox != null)
            dialogBox.setBounds(0,0, textLabel.getPreferredSize().getWidth() > 400 ? (int) textLabel.getPreferredSize().getWidth() + 20 : 400, 200);
        panel.setPreferredSize(new Dimension(textLabel.getPreferredSize().getWidth() > 400 ? (int) textLabel.getPreferredSize().getWidth() + 20 : 400, 200));
        labelPanel.add(textLabel);

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 45, 0));
        buttonsPanel.setOpaque(false);

        for (PromptedButton promptedButton : promptedButtons) {
            RoundedButton button = new RoundedButton(promptedButton.getButtonText());
            if (promptedButton.closeWindow)
                button.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (dialogBox != null) {
                            dialogBox.dispose();
                        }
                    }
                });
            button.addMouseListener(promptedButton.getActionListener());
            button.setForeground(theme.getColorTheme().getTextColor());
            button.setHorizontalAlignment(SwingConstants.CENTER);
            button.setFont(theme.getFont());
            button.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
            buttonsPanel.add(button);
        }

        panel.add(PanelHelper.northAndCenterElements(labelPanel, buttonsPanel));
        return panel;
    }

    public static JPanel getPromptPanel(String text, PromptedButton... promptedButtons) {
        return getPromptPanel(text, null, promptedButtons);
    }

    /**
     * Switches theme
     *
     * @param newTheme Theme to change
     */
    public static void switchTheme(ColorTheme newTheme) {
        if (CREATED) {
            theme = new Theme(newTheme, theme.getWidgetStyle(), theme.getFont(), theme.getWidth(), theme.getHeight(), theme.isResizeable());
        } else {
            runOnCreate.add(() -> {
                theme = new Theme(newTheme, theme.getWidgetStyle(), theme.getFont(), theme.getWidth(), theme.getHeight(), theme.isResizeable());
            });
        }
    }

    /**
     * Run upon app creation
     *
     * @param runnable  Code to run
     */
    public static void runOnCreation(Runnable runnable) {
        runOnCreate.add(runnable);
    }

    /**
     * @return App icon
     */
    public static ImageIcon getIcon() {
        if (!CREATED) {
            try {
                throw new AppNotCreatedException("App not created");
            } catch (AppNotCreatedException e) {
                e.printStackTrace();
            }
        }
        return icon;
    }

    /**
     * @return Current app theme
     */
    public static Theme getTheme() {
        if (!CREATED) {
            try {
                throw new AppNotCreatedException("App not created");
            } catch (AppNotCreatedException e) {
                e.printStackTrace();
            }
        }
        return theme;
    }

    /**
     * @return Selected local
     */
    public static Local getSelectedLocal() {
        return selectedLocal;
    }

    /**
     * @param local  Local to set
     */
    public static void setLocal(Local local) {
        selectedLocal = local;
    }

    /**
     * @return App version
     */
    public static String getVersion() {
        if (version != null) {
            return version;
        }
        return readAppProperty("version");
    }

    /**
     * @return App title
     */
    public static String getTitle() {
        if (title != null) {
            return title;
        }
        return readAppProperty("name");
    }

    /**
     * @return  Application state
     */
    public static boolean isCreated() {
        return CREATED;
    }

    /**
     * @return  Number of sessions
     * In essence, the number of times the app has been run.
     *
     * Note: Does not include
     * @see Application#restart()
     * and is reset on
     * @see Data#clearCache()
     */
    public static int getSession() {
        return session;
    }

    /**
     * @return  File directory of execution file (.exe)
     * ex.
     * .../user/DisplayPhoenix.exe
     * returns .../user/
     */
    public static File getExecutable() {
        //return new File(ClassLoader.getSystemClassLoader().getResource("").getPath()).getParentFile();
        try {
            return new File(exeClass.getProtectionDomain().getCodeSource().getLocation().toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @return  System processor
     */
    public static SystemProcessor getSystemProcessor() {
        return systemProcessor;
    }

    private static String readAppProperty(String key) {
        try {
            InputStream appPropertyStream = ClassLoader.getSystemClassLoader().getResourceAsStream("app_properties.json");
            if (appPropertyStream == null) {
                File appPropertyFile = new File("src/main/resources/app_properties.json");
                appPropertyFile.createNewFile();
                JsonObject object = new JsonObject();
                object.add("name", new JsonPrimitive("ExampleApp"));
                object.add("version", new JsonPrimitive("1.0.0.0"));
                FileWriter writer = new FileWriter(appPropertyFile);
                writer.write(gson.toJson(object));
                writer.flush();
                writer.close();
                throw new Exception("app_properties.json not created!");
            }
            JsonObject object = gson.fromJson(FileHelper.readAllLines(appPropertyStream), JsonObject.class);
            return object.get(key).getAsString();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static class PromptedButton {

        private String buttonText;
        private MouseListener actionListener;
        private boolean closeWindow;

        /**
         * Encases button text and a action listener for prompt
         *
         * @param buttonText Text of button
         * @param action Button action
         */
        public PromptedButton(String buttonText, MouseListener action) {
            this.buttonText = buttonText;
            this.actionListener = action;
        }

        public PromptedButton closeWindow() {
            this.closeWindow = true;
            return this;
        }

        public MouseListener getActionListener() {
            return actionListener;
        }

        public String getButtonText() {
            return buttonText;
        }
    }

    /**
     * Window wrapper interface
     */
    public interface IOpenWindow {
        void creation(ApplicationFrame parentFrame);
    }
}
