package net.displayphoenix;

import com.google.gson.Gson;
import net.displayphoenix.blockly.Blockly;
import net.displayphoenix.blockly.elements.workspace.ImplementedBlock;
import net.displayphoenix.canvasly.tools.Tool;
import net.displayphoenix.canvasly.tools.impl.*;
import net.displayphoenix.exception.AppNotCreatedException;
import net.displayphoenix.file.Data;
import net.displayphoenix.generation.Module;
import net.displayphoenix.generation.impl.JavaModule;
import net.displayphoenix.lang.Local;
import net.displayphoenix.lang.Localizer;
import net.displayphoenix.ui.Theme;
import net.displayphoenix.ui.widget.OverlayOnHoverWidget;
import net.displayphoenix.ui.widget.RoundedButton;
import net.displayphoenix.util.ImageHelper;
import net.displayphoenix.util.PanelHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author TBroski
 */
public class Application {

    private static final int BUTTON_WIDTH = 100;
    private static final int BUTTON_HEIGHT = 20;

    private static String version;
    private static boolean CREATED;
    private static String title;
    private static ImageIcon icon;
    private static Theme theme;
    private static Local selectedLocal = Local.EN_US;

    /**
     * Creates the app, used for organization and constants.
     *
     * @param appTitle Title of the app, the default for all windows unless set otherwise.
     * @param appIcon The app icon.
     * @param appTheme Theme of the app, subject to change.
     * @param appVersion The version of the app.
     */
    public static void create(String appTitle, ImageIcon appIcon, Theme appTheme, String appVersion) {
        title = appTitle;
        icon = appIcon;
        theme = appTheme;
        version = appVersion;
        CREATED = true;

        Localizer.create();
        Data.create();
        Module.registerModule(Module.JAVA);
        Module.registerModule(Module.JAVASCRIPT);
        Tool.REGISTERED_TOOLS.add(new BucketTool());
        Tool.REGISTERED_TOOLS.add(new EraserTool());
        Tool.REGISTERED_TOOLS.add(new ImageTool());
        Tool.REGISTERED_TOOLS.add(new PencilTool());
        Tool.REGISTERED_TOOLS.add(new PickerTool());
        Tool.REGISTERED_TOOLS.add(new TextTool());
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
            textField.setPreferredSize(new Dimension(600, 600));
            parentFrame.add(textField);
        }, 600, 600, theme.isResizeable());
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
    public static JFrame openWindow(String title, int closeAction, IOpenWindow windowCreation, int width, int height, boolean resizeable) {
        JFrame frame = new JFrame(title) {
            @Override
            protected void addImpl(Component comp, Object constraints, int index) {
                super.addImpl(comp, constraints, index);
                Color lBColor = UIManager.getColor("Label.background");
                Color lFColor = UIManager.getColor("Label.foreground");
                Color bBColor = UIManager.getColor("Button.background");
                Color bFColor = UIManager.getColor("Button.foreground");
                if (lFColor.equals(comp.getForeground()) || bFColor.equals(comp.getForeground())) {
                    comp.setForeground(comp instanceof JLabel || comp instanceof JButton || comp instanceof JComboBox ? theme.getColorTheme().getTextColor() : theme.getColorTheme().getSecondaryColor());
                }
                if (lBColor.equals(comp.getBackground()) || bBColor.equals(comp.getBackground())) {
                    comp.setBackground(theme.getColorTheme().getPrimaryColor());
                }
                comp.setFont(comp.getFont() != null ? theme.getFont().deriveFont(comp.getFont().getSize()) : theme.getFont());
                if (comp instanceof Container) {
                    try {
                        ((Container) comp).addContainerListener(new ContainerAdapter() {
                            @Override
                            public void componentAdded(ContainerEvent e) {
                                if (lFColor.equals(e.getComponent().getForeground()) || bFColor.equals(e.getComponent().getForeground())) {
                                    e.getComponent().setForeground(e.getComponent() instanceof JLabel || e.getComponent() instanceof JButton || e.getComponent() instanceof JComboBox ? theme.getColorTheme().getTextColor() : theme.getColorTheme().getSecondaryColor());
                                }
                                if (lBColor.equals(e.getComponent().getBackground()) || bBColor.equals(e.getComponent().getBackground())) {
                                    e.getComponent().setBackground(theme.getColorTheme().getPrimaryColor());
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
                                    component.setForeground(component instanceof JLabel || component instanceof JButton || component instanceof JComboBox ? theme.getColorTheme().getTextColor() : theme.getColorTheme().getSecondaryColor());
                                }
                                if (lBColor.equals(component.getBackground()) || bBColor.equals(component.getBackground())) {
                                    component.setBackground(theme.getColorTheme().getPrimaryColor());
                                }
                                component.setFont(component.getFont() != null ? theme.getFont().deriveFont(component.getFont().getSize()) : theme.getFont());
                                if (component instanceof Container) {
                                    setColors((Container) component);
                                    ((Container) component).addContainerListener(new ContainerAdapter() {
                                        @Override
                                        public void componentAdded(ContainerEvent e) {
                                            if (lFColor.equals(e.getComponent().getForeground())) {
                                                e.getComponent().setForeground(e.getComponent() instanceof JLabel || comp instanceof JButton || comp instanceof JComboBox ? theme.getColorTheme().getTextColor() : theme.getColorTheme().getSecondaryColor());
                                            }
                                            if (lBColor.equals(e.getComponent().getBackground())) {
                                                e.getComponent().setBackground(theme.getColorTheme().getPrimaryColor());
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
        frame.setIconImage(icon.getImage());
        frame.setDefaultCloseOperation(closeAction);
        frame.setBounds(0,0, width, height);
        frame.getContentPane().setBackground(theme.getColorTheme().getPrimaryColor());

        windowCreation.creation(frame);

        if (closeAction == JFrame.EXIT_ON_CLOSE) {
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    super.windowClosing(e);
                    Data.save();
                }
            });
        }

        frame.setResizable(resizeable);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        return frame;
    }
    public static JFrame openWindow(int closeAction, IOpenWindow windowCreation) {
        return openWindow(title, closeAction, windowCreation, theme.getWidth(), theme.getHeight(), theme.isResizeable());
    }
    public static JFrame openWindow(String title, IOpenWindow windowCreation) {
        return openWindow(title, JFrame.EXIT_ON_CLOSE, windowCreation, theme.getWidth(), theme.getHeight(), theme.isResizeable());
    }
    public static JFrame openWindow(String title, int closeAction, IOpenWindow windowCreation) {
        return openWindow(title, closeAction, windowCreation, theme.getWidth(), theme.getHeight(), theme.isResizeable());
    }
    public static JFrame openWindow(int closeAction, IOpenWindow windowCreation, int width, int height) {
        return openWindow(title, closeAction, windowCreation, width, height, theme.isResizeable());
    }
    public static JFrame openWindow(IOpenWindow windowCreation) {
        return openWindow(JFrame.EXIT_ON_CLOSE, windowCreation);
    }

    public static JPanel getLocalChangePanel() {
        Component[] widgets = new Component[Local.values().length];
        for (int i = 0; i < Local.values().length; i++) {
            OverlayOnHoverWidget widget = getLocalWidget(Local.values()[i]);
            widget.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                    for (Component component : widgets) {
                        component.repaint();
                    }
                }
            });
            widgets[i] = PanelHelper.join(widget);
        }
        return PanelHelper.grid(3, widgets);
    }

    private static OverlayOnHoverWidget getLocalWidget(Local local) {
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

    /**
     * Opens Ok prompt with themed components
     *
     * @param title Title of prompt
     * @param text Text of prompt
     * @param okListener Adds a listener to ok button
     * @param isWarning Whether it is a warning
     */
    public static void prompt(String title, String text, ActionListener okListener, boolean isWarning) {
        if (!CREATED) {
            try {
                throw new AppNotCreatedException("App not created");
            } catch (AppNotCreatedException e) {
                e.printStackTrace();
            }
        }
        JFrame dialogBox = new JFrame(title);
        dialogBox.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        dialogBox.setBounds(0,0,400, 200);
        dialogBox.setIconImage(isWarning ? ImageHelper.getImage(theme.getWidgetStyle().getName() + "_warning").getImage() : icon.getImage());

        JPanel labelPanel = new JPanel();
        labelPanel.setBorder(BorderFactory.createMatteBorder(30, 0, 20, 0, theme.getColorTheme().getPrimaryColor()));
        labelPanel.setBackground(theme.getColorTheme().getPrimaryColor());
        JLabel textLabel = new JLabel(text);
        textLabel.setForeground(theme.getColorTheme().getAccentColor());
        textLabel.setHorizontalAlignment(SwingConstants.CENTER);
        textLabel.setFont(theme.getFont());
        labelPanel.add(textLabel);
        dialogBox.add("North", labelPanel);

        JPanel okPanel = new JPanel();
        okPanel.setBorder(BorderFactory.createMatteBorder(20, 0, 45, 0, theme.getColorTheme().getPrimaryColor()));
        okPanel.setBackground(theme.getColorTheme().getPrimaryColor());
        RoundedButton okButton = new RoundedButton("Ok");
        okButton.addActionListener(e -> {
            dialogBox.dispose();
        });
        okButton.addActionListener(okListener);
        okButton.setForeground(theme.getColorTheme().getAccentColor());
        okButton.setHorizontalAlignment(SwingConstants.CENTER);
        okButton.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        okButton.setFont(theme.getFont());
        okPanel.add(okButton);
        dialogBox.add("South", okPanel);

        dialogBox.setResizable(false);
        dialogBox.setLocationRelativeTo(null);
        dialogBox.setAlwaysOnTop(isWarning);
        dialogBox.setVisible(true);
    }
    public static void prompt(String title, String text, boolean isWarning) {
        if (!CREATED) {
            try {
                throw new AppNotCreatedException("App not created");
            } catch (AppNotCreatedException e) {
                e.printStackTrace();
            }
        }
        prompt(title, text, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

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
        promptWithButtons(title, text, isWarning, new PromptedButton("Yes", yesListener).closeWindow(), new PromptedButton("No", noListener).closeWindow());
    }
    public static void promptYesOrNo(String title, String text, boolean isWarning, MouseListener yesListener) {
        promptWithButtons(title, text, isWarning, new PromptedButton("Yes", yesListener).closeWindow(), new PromptedButton("No", new MouseAdapter() {
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
        dialogBox.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        dialogBox.setBounds(0,0,400, 200);
        dialogBox.setIconImage(isWarning ? ImageHelper.getImage(theme.getWidgetStyle().getName() + "_warning").getImage() : icon.getImage());

        JPanel labelPanel = new JPanel();
        labelPanel.setBorder(BorderFactory.createMatteBorder(30, 0, 20, 0, theme.getColorTheme().getPrimaryColor()));
        labelPanel.setBackground(theme.getColorTheme().getPrimaryColor());
        JLabel textLabel = new JLabel(text);
        textLabel.setForeground(theme.getColorTheme().getAccentColor());
        textLabel.setHorizontalAlignment(SwingConstants.CENTER);
        textLabel.setFont(theme.getFont());
        labelPanel.add(textLabel);
        dialogBox.add("North", labelPanel);

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setBorder(BorderFactory.createMatteBorder(20, 0, 45, 0, theme.getColorTheme().getPrimaryColor()));
        buttonsPanel.setBackground(theme.getColorTheme().getPrimaryColor());

        for (PromptedButton promptedButton : promptedButtons) {
            RoundedButton button = new RoundedButton(promptedButton.getButtonText());
            if (promptedButton.closeWindow)
                button.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        dialogBox.dispose();
                    }
                });
            button.addMouseListener(promptedButton.getActionListener());
            button.setForeground(theme.getColorTheme().getAccentColor());
            button.setHorizontalAlignment(SwingConstants.CENTER);
            button.setFont(theme.getFont());
            button.setPreferredSize(new Dimension(BUTTON_WIDTH - (BUTTON_WIDTH / 3), BUTTON_HEIGHT));
            buttonsPanel.add(button);
        }

        dialogBox.add("South", buttonsPanel);

        dialogBox.setResizable(false);
        dialogBox.setLocationRelativeTo(null);
        dialogBox.setAlwaysOnTop(isWarning);
        dialogBox.setVisible(true);
    }

    /**
     * Executes simple command
     *
     * @param command Command to execute
     * @return Exit value of command
     */
    public static int systemExecute(String command) {
        try {
            Process process = Runtime.getRuntime().exec(command);

            StringBuilder output = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
            }
            int exitVal = process.waitFor();
            System.out.println(output.toString());
            return exitVal;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Switches theme
     *
     * @param newTheme Theme to change
     */
    public static void switchTheme(Theme newTheme) {
        theme = newTheme;
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
        if (!CREATED) {
            try {
                throw new AppNotCreatedException("App not created");
            } catch (AppNotCreatedException e) {
                e.printStackTrace();
            }
        }
        return version;
    }

    /**
     * @return App title
     */
    public static String getTitle() {
        if (!CREATED) {
            try {
                throw new AppNotCreatedException("App not created");
            } catch (AppNotCreatedException e) {
                e.printStackTrace();
            }
        }
        return title;
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
        void creation(JFrame parentFrame);
    }
}
