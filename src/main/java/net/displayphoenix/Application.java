package net.displayphoenix;

import net.displayphoenix.exception.AppNotCreatedException;
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
    private static Local selected_local = Local.EN_US;

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

        Localizer.create();
        CREATED = true;
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
        }, 600, 600);
    }

    /**
     * Opens a window within themed wrapper
     *
     * @param title Title of window
     * @param closeAction Close action of window
     * @param windowCreation Init components
     * @param width Width of window
     * @param height Height of window
     * @return Returns the frame for additional manipulation
     */
    public static JFrame openWindow(String title, int closeAction, IOpenWindow windowCreation, int width, int height) {
        JFrame frame = new JFrame(title);
        frame.setIconImage(icon.getImage());
        frame.setDefaultCloseOperation(closeAction);
        frame.setBounds(0,0, width, height);
        frame.getContentPane().setBackground(theme.getColorTheme().getPrimaryColor());

        windowCreation.creation(frame);

        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        return frame;
    }
    public static JFrame openWindow(int closeAction, IOpenWindow windowCreation) {
        return openWindow(title, closeAction, windowCreation, theme.getWidth(), theme.getHeight());
    }
    public static JFrame openWindow(String title, IOpenWindow windowCreation) {
        return openWindow(title, JFrame.EXIT_ON_CLOSE, windowCreation, theme.getWidth(), theme.getHeight());
    }
    public static JFrame openWindow(String title, int closeAction, IOpenWindow windowCreation) {
        return openWindow(title, closeAction, windowCreation, theme.getWidth(), theme.getHeight());
    }
    public static JFrame openWindow(int closeAction, IOpenWindow windowCreation, int width, int height) {
        return openWindow(title, closeAction, windowCreation, width, height);
    }
    public static JFrame openWindow(IOpenWindow windowCreation) {
        return openWindow(JFrame.EXIT_ON_CLOSE, windowCreation);
    }

    /**
     * Opens localization window, containing representing flags of languages
     *
     * @param title Title of window
     * @return Returns opened window
     */
    public static JFrame promptLocalChange(String title) {
        return Application.openWindow(title, JFrame.DISPOSE_ON_CLOSE, parentFrame -> {
            JPanel enUsFrFr = PanelHelper.join(FlowLayout.CENTER, 30, 0,getLocalWidget(Local.EN_US, parentFrame), getLocalWidget(Local.FR_FR, parentFrame));
            enUsFrFr.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
            parentFrame.add(enUsFrFr);
        }, 400, 600);
    }
    public static JFrame promptLocalChange() {
        return Application.openWindow(title, JFrame.DISPOSE_ON_CLOSE, parentFrame -> {
            JPanel enUsFrFr = PanelHelper.join(FlowLayout.CENTER, 30, 0,getLocalWidget(Local.EN_US, parentFrame), getLocalWidget(Local.FR_FR, parentFrame));
            enUsFrFr.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
            parentFrame.add(enUsFrFr);
        }, 400, 600);
    }

    private static OverlayOnHoverWidget getLocalWidget(Local local, Window parentFrame) {
        OverlayOnHoverWidget localOverlay = new OverlayOnHoverWidget(ImageHelper.getImage("lang/" + local.getTag()), theme.getColorTheme().getSecondaryColor(), 0.5F, 0.005F);
        localOverlay.setPreferredSize(new Dimension(100, 50));
        localOverlay.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selected_local = local;
                parentFrame.dispose();
                for (WindowListener listener : parentFrame.getWindowListeners()) {
                    listener.windowClosing(new WindowEvent(parentFrame, 0));
                }
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
        return selected_local;
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
