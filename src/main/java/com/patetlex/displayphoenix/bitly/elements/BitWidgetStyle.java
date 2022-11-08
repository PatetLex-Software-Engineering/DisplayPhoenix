package com.patetlex.displayphoenix.bitly.elements;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.patetlex.displayphoenix.Application;
import com.patetlex.displayphoenix.bitly.ui.BitArgument;
import com.patetlex.displayphoenix.bitly.ui.BitWidget;
import com.patetlex.displayphoenix.blockly.Blockly;
import com.patetlex.displayphoenix.blockly.elements.Block;
import com.patetlex.displayphoenix.blockly.elements.workspace.Field;
import com.patetlex.displayphoenix.blockly.elements.workspace.ImplementedBlock;
import com.patetlex.displayphoenix.blockly.ui.BlocklyDependencyPanel;
import com.patetlex.displayphoenix.blockly.ui.BlocklyPanel;
import com.patetlex.displayphoenix.canvasly.CanvasPanel;
import com.patetlex.displayphoenix.canvasly.ElementPanel;
import com.patetlex.displayphoenix.canvasly.LayerViewPanel;
import com.patetlex.displayphoenix.canvasly.ToolPanel;
import com.patetlex.displayphoenix.canvasly.elements.CanvasSave;
import com.patetlex.displayphoenix.canvasly.elements.Element;
import com.patetlex.displayphoenix.canvasly.elements.StaticElement;
import com.patetlex.displayphoenix.canvasly.elements.impl.FontElement;
import com.patetlex.displayphoenix.canvasly.elements.impl.ImageElement;
import com.patetlex.displayphoenix.canvasly.tools.Tool;
import com.patetlex.displayphoenix.file.Data;
import com.patetlex.displayphoenix.file.DetailedFile;
import com.patetlex.displayphoenix.file.FileDialog;
import com.patetlex.displayphoenix.generation.Module;
import com.patetlex.displayphoenix.lang.Localizer;
import com.patetlex.displayphoenix.ui.widget.*;
import com.patetlex.displayphoenix.ui.widget.TextField;
import com.patetlex.displayphoenix.util.*;
<<<<<<< HEAD
import org.eclipse.jgit.annotations.NonNull;

=======
import com.sun.istack.internal.NotNull;
>>>>>>> 47a47a09d2902902588a944b173e5c8c191c9a2d

import javax.imageio.ImageIO;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;

/**
 * For widget styles
 *
 * @see com.patetlex.displayphoenix.bitly.ui.BitWidget
 *
 */
public abstract class BitWidgetStyle {

    private static transient final Random rand = new Random();
    private static transient final Gson gson = new Gson();

    public static final BitWidgetStyle TOGGLE = new BitWidgetStyle("TOGGLE") {
        @Override
        public Component create(BitWidget widget) {
            Toggle toggle = new Toggle();
            toggle.setPreferredSize(new Dimension(150, 75));
            return toggle;
        }

        @Override
        public Object getValue(Component component) {
            return ((Toggle) component).isToggled();
        }

        @Override
        public void setValue(Component component, BitArgument argument) {
            ((Toggle) component).setToggle(argument.getAsBoolean());
        }
    };
    public static final BitWidgetStyle TEXT = new BitWidgetStyle("TEXT") {
        @Override
        public Component create(BitWidget widget) {
            TextField textField = new TextField();
            textField.setPreferredSize(new Dimension(150, 75));
            ComponentHelper.deriveFont(textField, 25);
            textField.setHorizontalAlignment(SwingConstants.CENTER);
            return textField;
        }

        @Override
        public Object getValue(Component component) {
            return ((TextField) component).getText();
        }

        @Override
        public void setValue(Component component, BitArgument argument) {
            ((TextField) component).setText(argument.getAsString());
        }
    };
    public static final BitWidgetStyle NUMBER = new BitWidgetStyle("NUMBER") {
        @Override
        public Component create(BitWidget widget) {
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
            return numField;
        }

        @Override
        public Object getValue(Component component) {
            return ((TextField) component).getText();
        }

        @Override
        public void setValue(Component component, BitArgument argument) {
            ((TextField) component).setText(argument.getAsString());
        }
    };
    public static final BitWidgetStyle BLOCKLY = new BitWidgetStyle("BLOCKLY") {
        private transient Map<String, Integer> colorCache = new HashMap<>();
        @Override
        public Component create(BitWidget widget) {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByExtension("js");
            Invocable invocable = (Invocable) engine;
            try {
                engine.eval(widget.getScript() != null ? widget.getScript() : "");
            } catch (ScriptException e) {
                e.printStackTrace();
            }
            ProvisionWidget provisionWidget = new ProvisionWidget(widget.provisions, widget.headBlock != null ? widget.headBlock : "event_wrapper");
            engine.put("widget", provisionWidget);
            provisionWidget.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
                        Application.openWindow(JFrame.DO_NOTHING_ON_CLOSE, parentFrame -> {
                            BlocklyPanel blockly = new BlocklyPanel();
                            engine.put("blocklypanel", blockly);
                            if (provisionWidget.getFieldProvisions() != null) {
                                for (String extensionKey : provisionWidget.getFieldProvisions().keySet()) {
                                    blockly.addFieldExtensions(extensionKey, provisionWidget.getFieldProvisions().get(extensionKey));
                                }
                            }
                            BlocklyDependencyPanel dependencyPanel = new BlocklyDependencyPanel(blockly);
                            if (widget.provisions != null) {
                                for (String provision : widget.provisions) {
                                    dependencyPanel.addProvision(provision);
                                }
                            }
                            if (provisionWidget.getXml() != null) {
                                dependencyPanel.getBlocklyPanel().addBlocks(provisionWidget.getXml());
                            } else {
                                Block event = Blockly.getBlockFromType(widget.headBlock != null ? widget.headBlock : "event_wrapper");
                                if (event == null) {
                                    event = Blockly.getBlockFromType("event_wrapper");
                                    event.persist();
                                }
                                dependencyPanel.getBlocklyPanel().addBlocks(new ImplementedBlock(event, 50, 50, false, true));
                            }
                            parentFrame.addWindowListener(new WindowAdapter() {
                                @Override
                                public void windowClosing(WindowEvent e) {
                                    super.windowClosing(e);
                                    if (dependencyPanel.getUnsatisfiedDependencies().isEmpty()) {
                                        List<ImplementedBlock> unsatisfiedBlocks = new ArrayList<>();
                                        BlocklyHelper.forEachBlock(new Consumer<ImplementedBlock>() {
                                            @Override
                                            public void accept(ImplementedBlock implementedBlock) {
                                                for (String value : implementedBlock.getValueBlocks().keySet()) {
                                                    if (implementedBlock.getValueBlocks().get(value).size() == 0) {
                                                        unsatisfiedBlocks.add(implementedBlock);
                                                    }
                                                }
                                                for (Field field : implementedBlock.getFields()) {
                                                    if (field.getValue() == null || field.getValue().isEmpty()) {
                                                        unsatisfiedBlocks.add(implementedBlock);
                                                    }
                                                }
                                            }
                                        }, new Object() {
                                            public ImplementedBlock getHeadBlock() {
                                                for (ImplementedBlock block : dependencyPanel.getBlocklyPanel().getWorkspace()) {
                                                    if (block.getBlock().getType().equalsIgnoreCase(provisionWidget.getHeadBlock())) {
                                                        return block;
                                                    }
                                                }
                                                return null;
                                            }
                                        }.getHeadBlock());
                                        if (unsatisfiedBlocks.size() == 0) {
                                            parentFrame.dispose();
                                            provisionWidget.setXml(dependencyPanel.getBlocklyPanel().getRawWorkspace());
                                            try {
                                                invocable.invokeFunction("onClose");
                                            } catch (ScriptException scriptException) {
                                                scriptException.printStackTrace();
                                            } catch (NoSuchMethodException noSuchMethodException) {
                                            }
                                        } else {
                                            Application.openWindow(JFrame.DISPOSE_ON_CLOSE, parentFrame -> {
                                                JLabel label = new JLabel(Localizer.translate("blockly.unsatisfied_values.text"));
                                                ComponentHelper.themeComponent(label);
                                                ComponentHelper.deriveFont(label, 20);
                                                JPanel list = PanelHelper.join();
                                                for (ImplementedBlock unsatisfiedBlock : unsatisfiedBlocks) {
                                                    String unsatisfiedValue = unsatisfiedBlock.getBlock().getType();
                                                    JLabel unsatisfyLabel = new JLabel(unsatisfiedValue);
                                                    ComponentHelper.themeComponent(unsatisfyLabel);
                                                    ComponentHelper.deriveFont(unsatisfyLabel, 17);
                                                    if (!colorCache.containsKey(unsatisfiedValue))
                                                        colorCache.put(unsatisfiedValue, rand.nextInt(360));
                                                    float hue = colorCache.get(unsatisfiedValue);
                                                    label.setForeground(Color.getHSBColor(hue / 360F, 0.45F, 0.65F));
                                                    JPanel labelPanel = PanelHelper.join(label);
                                                    labelPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
                                                    list = PanelHelper.northAndCenterElements(list, labelPanel);
                                                }
                                                parentFrame.add(PanelHelper.northAndCenterElements(PanelHelper.join(label), list));
                                            }, Math.round(Application.getTheme().getWidth() * 0.3F), Math.round(Application.getTheme().getHeight() * 0.5F));                                        }
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
                            provisionWidget.invoke(blockly);
                            try {
                                invocable.invokeFunction("onOpen");
                            } catch (ScriptException scriptException) {
                                scriptException.printStackTrace();
                            } catch (NoSuchMethodException noSuchMethodException) {
                            }
                            parentFrame.add(PanelHelper.centerAndEastElements(blockly, dependencyPanel));
                            blockly.setPreferredSize(new Dimension(Math.round(parentFrame.getWidth() * 0.85F), parentFrame.getHeight()));
                            dependencyPanel.setPreferredSize(new Dimension(Math.round(parentFrame.getWidth() * 0.15F), parentFrame.getHeight()));
                        });
                    }
                }
            });
            ComponentHelper.deriveFont(provisionWidget, 25);
            provisionWidget.setPreferredSize(new Dimension(150, 75));
            return provisionWidget;
        }

        @Override
        public Object getValue(Component component) {
            return ((ProvisionWidget) component).getXml();
        }

        @Override
        public void setValue(Component component, BitArgument argument) {
            ((ProvisionWidget) component).setXml(argument.getAsString());
        }

        @Override
        public String getCode(Module module, Component component) {
            ProvisionWidget provisionWidget = ((ProvisionWidget) component);
            if (provisionWidget.getXml() == null)
                return null;
            ImplementedBlock[] implementedBlocks = module.fromWorkspaceXml(provisionWidget.getXml());
            for (ImplementedBlock implementedBlock : implementedBlocks) {
                if (implementedBlock.getBlock().getType().equalsIgnoreCase(provisionWidget.getHeadBlock())) {
                    return module.getCodeFromBlock(implementedBlock);
                }
            }
            return "";
        }
    };
    public static final BitWidgetStyle RESOURCE = new BitWidgetStyle("RESOURCE") {
        @Override
        public Component create(BitWidget widget) {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByExtension("js");
            Invocable invocable = (Invocable) engine;
            try {
                engine.eval(widget.getScript() != null ? widget.getScript() : "");
            } catch (ScriptException e) {
                e.printStackTrace();
            }
            ResourceWidget resourceWidget = new ResourceWidget();
            engine.put("widget", resourceWidget);
            resourceWidget.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    FileDialog.openFile(new Consumer<DetailedFile>() {
                        @Override
                        public void accept(DetailedFile detailedFile) {
                            if (widget.width != 0 && widget.height != 0) {
                                if (detailedFile.getFileExtension().equalsIgnoreCase("png")) {
                                    try {
                                        ImageIcon icon = new ImageIcon(Toolkit.getDefaultToolkit().createImage(detailedFile.getFile().toURI().toURL()));
                                        if (icon != null && icon.getIconWidth() != widget.width && icon.getIconHeight() != widget.height) {
                                            return;
                                        }
                                    } catch (MalformedURLException malformedURLException) {
                                        malformedURLException.printStackTrace();
                                    }
                                }
                            }
                            resourceWidget.setFile(detailedFile);
                            try {
                                invocable.invokeFunction("fileSet", detailedFile);
                            } catch (ScriptException scriptException) {
                                scriptException.printStackTrace();
                            } catch (NoSuchMethodException noSuchMethodException) {
                            }
                        }
                    }, widget.options);
                }
            });
            resourceWidget.setPreferredSize(new Dimension(150, 150));
            return resourceWidget;
        }

        @Override
        public Object getValue(Component component) {
            return ((ResourceWidget) component).getFile();
        }

        @Override
        public void setValue(Component component, BitArgument argument) {
            if (argument.get() instanceof DetailedFile) {
                ((ResourceWidget) component).setFile((DetailedFile) argument.get());
            } else if (argument.get() instanceof File) {
                ((ResourceWidget) component).setFile(new DetailedFile((File) argument.get()));
            } else if (argument.get() instanceof LinkedTreeMap) {
                LinkedTreeMap treeMap = (LinkedTreeMap) argument.get();
                ((ResourceWidget) component).setFile(new DetailedFile(new File(gson.toJsonTree(treeMap).getAsJsonObject().get("file").getAsJsonObject().get("path").getAsString())));
            } else {
                ((ResourceWidget) component).setFile(new DetailedFile(new File(argument.getAsString())));
            }
        }
    };
    public static final BitWidgetStyle IMAGE = new BitWidgetStyle("IMAGE") {
        @Override
        public Component create(BitWidget widget) {
            JLabel label1 = new JLabel(ImageHelper.resize(ImageHelper.fromPath(widget.path), widget.width > 0 ? widget.width : 150, widget.height > 0 ? widget.height : 150));
            label1.setPreferredSize(new Dimension(widget.width > 0 ? widget.width : 150, widget.height > 0 ? widget.height : 150));
            return label1;
        }

        @Override
        public Object getValue(Component component) {
            return null;
        }

        @Override
        public void setValue(Component component, BitArgument argument) {

        }
    };
    public static final BitWidgetStyle CANVAS = new BitWidgetStyle("CANVAS") {
        @Override
        public Component create(BitWidget widget) {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByExtension("js");
            Invocable invocable = (Invocable) engine;
            try {
                engine.eval(widget.getScript() != null ? widget.getScript() : "");
            } catch (ScriptException e) {
                e.printStackTrace();
            }
            CanvasWidget canvasWidget = new CanvasWidget();
            engine.put("widget", canvasWidget);
            canvasWidget.setToolTipText(widget.width + " x " + widget.height);
            java.util.List<Tool> canvasTools = new ArrayList<>();
            for (Tool tool : Tool.REGISTERED_TOOLS) {
                for (String toolName : widget.tools) {
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
                            CanvasPanel canvas = new CanvasPanel(widget.width, widget.height);
                            engine.put("canvasPanel", canvas);
                            canvas.setPreferredSize(new Dimension(parentFrame.getWidth() - (widget.canvasElements != null ? 600 : 400), parentFrame.getHeight()));
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
                                    try {
                                        invocable.invokeFunction("onClose");
                                    } catch (ScriptException scriptException) {
                                        scriptException.printStackTrace();
                                    } catch (NoSuchMethodException noSuchMethodException) {
                                    }
                                }
                            });
                            if (widget.canvasElements != null) {
                                List<StaticElement> staticElements = new ArrayList<>();
                                for (BitWidget.CanvasElement canvasElement : widget.canvasElements) {
                                    Element element = null;
                                    if (canvasElement.type.equalsIgnoreCase("image")) {
                                        String[] paths = canvasElement.defaultValue.split("/");
                                        try {
                                            element = new ImageElement(ImageIO.read(Data.find("/bitly/elements/" + paths[paths.length - 1])), paths[paths.length - 1]);
                                        } catch (IOException ioException) {
                                            ioException.printStackTrace();
                                        }
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
                            try {
                                invocable.invokeFunction("onOpen");
                            } catch (ScriptException scriptException) {
                                scriptException.printStackTrace();
                            } catch (NoSuchMethodException noSuchMethodException) {
                                noSuchMethodException.printStackTrace();
                            }
                            parentFrame.add(PanelHelper.westAndCenterElements(PanelHelper.westAndCenterElements(toolkit, canvas), layerView));
                        });
                    }
                }
            });
            canvasWidget.setPreferredSize(new Dimension(150, 150));
            return canvasWidget;
        }

        @Override
        public Object getValue(Component component) {
            return ((CanvasWidget) component).getSave();
        }

        @Override
        public void setValue(Component component, BitArgument argument) {
            Object arg = argument.get();
            if (arg instanceof LinkedTreeMap) {
                arg = CanvasSave.fromSave(gson.toJson(arg));
            }
            ((CanvasWidget) component).setSave((CanvasSave) arg);
        }

        @Override
        public Map<String, byte[]> loadExternalFiles(BitWidget widget, boolean isNative, File relativePath) {
            Map<String, byte[]> files = super.loadExternalFiles(widget, isNative, relativePath);
            for (BitWidget.CanvasElement canvasElement : widget.canvasElements) {
                byte[] bytes = null;
                if (isNative) {
                    bytes = FileHelper.readAllBytesFromStream(ClassLoader.getSystemClassLoader().getResourceAsStream("textures/bitly/" + canvasElement.defaultValue));
                } else {
                    try {
                        bytes = Files.readAllBytes(new File(relativePath.getPath() + "/" + canvasElement.defaultValue).toPath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (canvasElement.defaultValue.contains("/")) {
                    String[] paths = canvasElement.defaultValue.split("/");
                    files.put(paths[paths.length - 1], bytes);
                } else {
                    files.put(canvasElement.defaultValue, bytes);
                }
            }
            return files;
        }
    };
    public static final BitWidgetStyle OPTIONS = new BitWidgetStyle("OPTIONS") {
        @Override
        public Component create(BitWidget widget) {
            JComboBox comboBox = new JComboBox(widget.options);
            comboBox.setPreferredSize(new Dimension(150, 75));
            return comboBox;
        }

        @Override
        public Object getValue(Component component) {
            return ((JComboBox) component).getSelectedItem();
        }

        @Override
        public void setValue(Component component, BitArgument argument) {
            ((JComboBox) component).setSelectedItem(argument.getAsString());
        }
    };

    private final String name;

    public BitWidgetStyle(String name) {
        this.name = name;
    }

    public abstract Component create(BitWidget widget);
    public abstract Object getValue(Component component);
    public abstract void setValue(Component component, BitArgument argument);

<<<<<<< HEAD
    @NonNull
=======
    @NotNull
>>>>>>> 47a47a09d2902902588a944b173e5c8c191c9a2d
    public String getCode(Module module, Component component) {
        Object val = getValue(component);
        if (val == null)
            return "";
        return String.valueOf(val);
    }

    public String getName() {
        return name;
    }

    public Map<String, byte[]> loadExternalFiles(BitWidget widget, boolean isNative, File relativePath) {
        return new HashMap<>();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BitWidgetStyle) {
            return ((BitWidgetStyle) obj).getName().equalsIgnoreCase(this.getName());
        }
        return super.equals(obj);
    }
}
