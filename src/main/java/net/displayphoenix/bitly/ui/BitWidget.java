package net.displayphoenix.bitly.ui;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;
import net.displayphoenix.Application;
import net.displayphoenix.bitly.elements.Bit;
import net.displayphoenix.bitly.enums.BitWidgetStyle;
import net.displayphoenix.blockly.Blockly;
import net.displayphoenix.blockly.elements.Block;
import net.displayphoenix.blockly.elements.workspace.ImplementedBlock;
import net.displayphoenix.blockly.ui.BlocklyDependencyPanel;
import net.displayphoenix.blockly.ui.BlocklyPanel;
import net.displayphoenix.canvasly.CanvasPanel;
import net.displayphoenix.canvasly.ElementPanel;
import net.displayphoenix.canvasly.LayerViewPanel;
import net.displayphoenix.canvasly.ToolPanel;
import net.displayphoenix.canvasly.elements.CanvasSave;
import net.displayphoenix.canvasly.elements.Element;
import net.displayphoenix.canvasly.elements.StaticElement;
import net.displayphoenix.canvasly.elements.impl.FontElement;
import net.displayphoenix.canvasly.elements.impl.ImageElement;
import net.displayphoenix.canvasly.tools.Tool;
import net.displayphoenix.file.DetailedFile;
import net.displayphoenix.file.FileDialog;
import net.displayphoenix.lang.Localizer;
import net.displayphoenix.ui.widget.*;
import net.displayphoenix.ui.widget.TextField;
import net.displayphoenix.util.BlocklyHelper;
import net.displayphoenix.util.ComponentHelper;
import net.displayphoenix.util.ImageHelper;
import net.displayphoenix.util.PanelHelper;
import net.displayphoenix.web.Website;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

public class BitWidget {

    private static transient final Random rand = new Random();
    private static transient final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static transient Map<String, Integer> colorCache = new HashMap<>();


    private BitWidgetStyle style;
    private String flag;
    private String helpUrl;
    private String[] provisions;
    private String[] extensions;
    private String path;
    private String headBlock;
    private int width;
    private int height;
    private String[] tools;
    private List<CanvasElement> canvasElements;

    /**
     * Main component of Bitly, used as widgets for main panel
     *
     * @param style Style of widget
     * @param flag  Flag for code
     * @see BitWidgetStyle
     */
    public BitWidget(BitWidgetStyle style, String flag) {
        this.style = style;
        this.flag = flag;
    }

    /**
     * Returns style of widget
     *
     * @return Style of widget
     * @see BitWidgetStyle
     */
    public BitWidgetStyle getStyle() {
        return style;
    }

    /**
     * Returns code flag of widget
     *
     * @return Flag of bit
     */
    public String getFlag() {
        return flag;
    }

    /**
     * Creates the main component and panel component
     *
     * @return Component array of both panel and component
     */
    public Component[] create() {
        // Widget comment
        JLabel label = new JLabel(Localizer.translate("bitly.widget." + flag.toLowerCase() + ".text"));

        // Creates help website if applicable
        if (this.helpUrl != null) {
            label.setCursor(new Cursor(Cursor.HAND_CURSOR));
            label.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    getHelpWebsite().open();
                }
            });
        }

        // Themes label
        ComponentHelper.themeComponent(label);
        ComponentHelper.deriveFont(label, 25F);

        // Checks for each individual style
        switch (style) {
            case TOGGLE:
                Toggle toggle = new Toggle();
                toggle.setPreferredSize(new Dimension(150, 75));
                return new Component[]{PanelHelper.northAndCenterElements(PanelHelper.join(label), PanelHelper.join(toggle)), toggle};
            case TEXT:
                TextField textField = new TextField();
                textField.setPreferredSize(new Dimension(150, 75));
                ComponentHelper.deriveFont(textField, 25);
                textField.setHorizontalAlignment(SwingConstants.CENTER);
                return new Component[]{PanelHelper.northAndCenterElements(PanelHelper.join(label), PanelHelper.join(textField)), textField};
            case NUMBER:
                TextField numField = new TextField();
                numField.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyTyped(KeyEvent e) {
                        char c = e.getKeyChar();
                        boolean flag = false;
                        try {
                            Double.parseDouble(Character.toString(c));
                        } catch (NumberFormatException ex) {
                            flag = true;
                        }
                        if (flag)
                            e.consume();
                    }
                });
                numField.setPreferredSize(new Dimension(150, 75));
                ComponentHelper.deriveFont(numField, 25);
                numField.setHorizontalAlignment(SwingConstants.CENTER);
                return new Component[]{PanelHelper.northAndCenterElements(PanelHelper.join(label), PanelHelper.join(numField)), numField};
            case BLOCKLY:
                ProvisionWidget provisionWidget = new ProvisionWidget(this.provisions, headBlock != null ? headBlock : "event_wrapper");
                provisionWidget.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
                            Application.openWindow(JFrame.DO_NOTHING_ON_CLOSE, parentFrame -> {
                                BlocklyPanel blockly = new BlocklyPanel();
                                if (provisionWidget.getFieldProvisions() != null) {
                                    for (String extensionKey : provisionWidget.getFieldProvisions().keySet()) {
                                        blockly.addFieldExtensions(extensionKey, provisionWidget.getFieldProvisions().get(extensionKey));
                                    }
                                }
                                BlocklyDependencyPanel dependencyPanel = new BlocklyDependencyPanel(blockly);
                                if (provisions != null) {
                                    for (String provision : provisions) {
                                        dependencyPanel.addProvision(provision);
                                    }
                                }
                                if (provisionWidget.getXml() != null) {
                                    dependencyPanel.getBlocklyPanel().addBlocks(provisionWidget.getXml());
                                } else {
                                    Block event = Blockly.getBlockFromType(headBlock != null ? headBlock : "event_wrapper");
                                    if (event == null) {
                                        Blockly.registerCategory("flow_control", BlocklyHelper.getCategoryJson("flow_control"));
                                        Blockly.registerBlock("event_wrapper", BlocklyHelper.getBlockJson("event_wrapper"));
                                        event = Blockly.getBlockFromType("event_wrapper");
                                        event.persist();
                                        event.hide();
                                    }
                                    dependencyPanel.getBlocklyPanel().addBlocks(new ImplementedBlock(event, 50, 50, false, true));
                                }
                                parentFrame.addWindowListener(new WindowAdapter() {
                                    @Override
                                    public void windowClosing(WindowEvent e) {
                                        dependencyPanel.getBlocklyPanel().getRawWorkspace(new Consumer<String>() {
                                            @Override
                                            public void accept(String s) {
                                                provisionWidget.setXml(s);
                                            }
                                        });
                                    }
                                });
                                parentFrame.addWindowListener(new WindowAdapter() {
                                    @Override
                                    public void windowClosing(WindowEvent e) {
                                        super.windowClosing(e);
                                        if (dependencyPanel.getUnsatisfiedDependencies().isEmpty()) {
                                            parentFrame.dispose();
                                        } else {
                                            Application.openWindow(JFrame.DISPOSE_ON_CLOSE, parentFrame -> {
                                                JLabel label = new JLabel(Localizer.translate("blockly.unsatisfied_dependencies.text"));
                                                ComponentHelper.themeComponent(label);
                                                ComponentHelper.deriveFont(label, 20);
                                                JPanel list = PanelHelper.join();
                                                for (String unsatisfiedDependency : dependencyPanel.getUnsatisfiedDependencies()) {
                                                    JLabel unsatisfyLabel = new JLabel(unsatisfiedDependency);
                                                    ComponentHelper.themeComponent(unsatisfyLabel);
                                                    ComponentHelper.deriveFont(unsatisfyLabel, 17);
                                                    if (!colorCache.containsKey(unsatisfiedDependency))
                                                        colorCache.put(unsatisfiedDependency, rand.nextInt(360));
                                                    float hue = colorCache.get(unsatisfiedDependency);
                                                    label.setForeground(Color.getHSBColor(hue / 360F, 0.45F, 0.65F));
                                                    JPanel labelPanel = PanelHelper.join(label);
                                                    labelPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
                                                    list = PanelHelper.northAndCenterElements(list, labelPanel);
                                                }
                                                parentFrame.add(PanelHelper.northAndCenterElements(PanelHelper.join(label), list));
                                            }, Math.round(Application.getTheme().getWidth() * 0.3F), Math.round(Application.getTheme().getHeight() * 0.5F));
                                        }
                                    }
                                });
                                parentFrame.add(PanelHelper.westAndCenterElements(blockly, dependencyPanel));
                                blockly.setPreferredSize(new Dimension(Math.round(parentFrame.getWidth() * 0.8F), parentFrame.getHeight()));
                                dependencyPanel.setPreferredSize(new Dimension(Math.round(parentFrame.getWidth() * 0.2F), parentFrame.getHeight()));
                            });
                        }
                    }
                });
                ComponentHelper.deriveFont(provisionWidget, 25);
                provisionWidget.setPreferredSize(new Dimension(150, 75));
                return new Component[]{PanelHelper.northAndCenterElements(PanelHelper.join(label), PanelHelper.join(provisionWidget)), provisionWidget};
            case RESOURCE:
                ResourceWidget resourceWidget = new ResourceWidget();
                resourceWidget.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        resourceWidget.setFile(FileDialog.openFile(extensions));
                    }
                });
                resourceWidget.setPreferredSize(new Dimension(150, 150));
                return new Component[]{PanelHelper.northAndCenterElements(PanelHelper.join(label), PanelHelper.join(resourceWidget)), resourceWidget};
            case IMAGE:
                JLabel label1 = new JLabel(ImageHelper.resize(ImageHelper.fromPath(this.path), this.width > 0 ? this.width : 150, this.height > 0 ? this.height : 150));
                label1.setPreferredSize(new Dimension(this.width > 0 ? this.width : 150, this.height > 0 ? this.height : 150));
                return new Component[]{PanelHelper.northAndCenterElements(PanelHelper.join(label), PanelHelper.join(label1)), label1};
            case CANVAS:
                CanvasWidget canvasWidget = new CanvasWidget();
                canvasWidget.setToolTipText(this.width + " x " + this.height);
                List<Tool> canvasTools = new ArrayList<>();
                for (Tool tool : Tool.REGISTERED_TOOLS) {
                    for (String toolName : this.tools) {
                        if (tool.getName().equalsIgnoreCase(toolName)) {
                            canvasTools.add(tool);
                            break;
                        }
                    }
                }
                canvasWidget.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        super.mouseClicked(e);
                        if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
                            Application.openWindow(JFrame.DISPOSE_ON_CLOSE, parentFrame -> {
                                CanvasPanel canvas = new CanvasPanel(width, height);
                                canvas.setPreferredSize(new Dimension(parentFrame.getWidth() - (canvasElements != null ? 600 : 400), parentFrame.getHeight()));
                                ToolPanel toolkit = new ToolPanel(canvas, canvasTools.toArray(new Tool[canvasTools.size()]));
                                toolkit.setPreferredSize(new Dimension(200, parentFrame.getHeight()));
                                LayerViewPanel layerView = new LayerViewPanel(canvas);
                                layerView.setPreferredSize(new Dimension(200, parentFrame.getHeight()));
                                if (canvasWidget.getSave() != null) {
                                    canvas.setCanvas(canvasWidget.getSave().getPixels(), canvasWidget.getSave().getStaticElements());
                                }
                                parentFrame.addWindowListener(new WindowAdapter() {
                                    @Override
                                    public void windowClosing(WindowEvent e) {
                                        super.windowClosing(e);
                                        canvasWidget.setSave(canvas.getSave());
                                    }
                                });
                                if (canvasElements != null) {
                                    List<StaticElement> staticElements = new ArrayList<>();
                                    for (CanvasElement canvasElement : canvasElements) {
                                        Element element = null;
                                        if (canvasElement.type.equalsIgnoreCase("image")) {
                                            element = new ImageElement(ImageHelper.getImage(canvasElement.defaultValue).getImage(), ClassLoader.getSystemClassLoader().getResource("textures/" + (canvasElement.defaultValue.endsWith(".png") ? canvasElement.defaultValue : canvasElement.defaultValue + ".png")).getFile());
                                        } else if (canvasElement.type.equalsIgnoreCase("text")) {
                                            element = new FontElement(canvasElement.defaultValue, new Color(canvasElement.r, canvasElement.g, canvasElement.b, canvasElement.a), 1);
                                        }
                                        element.setScaleFactor(canvasElement.scale);
                                        StaticElement.Properties properties = new StaticElement.Properties();
                                        if (canvasElement.parse) {
                                            properties.setParse();
                                        }
                                        if (canvasElement.overlay) {
                                            properties.setOverlay();
                                        }
                                        staticElements.add(new StaticElement(element, 0, 0, properties));
                                    }
                                    ElementPanel elementPanel = new ElementPanel(canvas, staticElements.toArray(new StaticElement[staticElements.size()]));
                                    elementPanel.setPreferredSize(new Dimension(200, parentFrame.getHeight()));
                                    parentFrame.add(PanelHelper.westAndCenterElements(PanelHelper.westAndCenterElements(elementPanel, toolkit), PanelHelper.westAndCenterElements(canvas, layerView)));
                                    return;
                                }
                                parentFrame.add(PanelHelper.westAndCenterElements(PanelHelper.westAndCenterElements(toolkit, canvas), layerView));
                            });
                        }
                    }
                });
                canvasWidget.setPreferredSize(new Dimension(150, 150));
                return new Component[]{PanelHelper.northAndCenterElements(PanelHelper.join(label), PanelHelper.join(canvasWidget)), canvasWidget};
        }
        return null;
    }

    /**
     * Sets the value of a widget to argument
     *
     * @param component Component to set
     * @param argument  Argument to set to
     * @see Bit#get(BitArgument...)
     */
    public void setValue(Component component, BitArgument argument) {

        // Switch for each style
        switch (style) {
            case TOGGLE:
                ((Toggle) component).setToggle(argument.getAsBoolean());
                break;
            case TEXT:
            case NUMBER:
                ((TextField) component).setText(argument.getAsString());
                break;
            case BLOCKLY:
                ((ProvisionWidget) component).setXml(argument.getAsString());
                break;
            case RESOURCE:
                ((ResourceWidget) component).setFile(new DetailedFile(new File(argument.getAsString())));
                break;
            case CANVAS:
                Object arg = argument.get();
                if (arg instanceof LinkedTreeMap) {
                    arg = gson.fromJson(gson.toJson(arg), CanvasSave.class);
                }
                ((CanvasWidget) component).setSave((CanvasSave) arg);
                break;
        }
        component.repaint();
    }

    /**
     * Returns the value of a component
     *
     * @param component Component to check
     */
    public Object getValue(Component component) {

        // Switch for each style
        switch (style) {
            case TOGGLE:
                return ((Toggle) component).isToggled();
            case TEXT:
            case NUMBER:
                return ((TextField) component).getText();
            case BLOCKLY:
                return ((ProvisionWidget) component).getXml();
            case RESOURCE:
                return ((ResourceWidget) component).getFile().getFile().getPath();
            case CANVAS:
                return ((CanvasWidget) component).getSave();
        }
        return null;
    }

    /**
     * Returns website of <code>helpUrl</code>
     *
     * @return Website of help url
     * @see BitWidget#create()
     */
    public Website getHelpWebsite() {
        return this.helpUrl != null ? new Website(this.helpUrl) : null;
    }

    private static class CanvasElement {

        public String type;
        public String defaultValue;
        public int scale = 1;
        public int r;
        public int g;
        public int b;
        public int a = 255;
        public boolean parse;
        public boolean overlay;

    }
}
