package net.displayphoenix.blockly.ui;

import com.sun.javafx.webkit.Accessor;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Worker;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import net.displayphoenix.Application;
import net.displayphoenix.blockly.Blockly;
import net.displayphoenix.blockly.gen.BlocklyHtmlGenerator;
import net.displayphoenix.blockly.gen.BlocklyXmlParser;
import net.displayphoenix.blockly.elements.Category;
import net.displayphoenix.blockly.elements.workspace.ImplementedBlock;
import net.displayphoenix.blockly.event.BlocklyEvent;
import net.displayphoenix.blockly.event.IBlocklyListener;
import net.displayphoenix.blockly.event.events.*;
import net.displayphoenix.file.FileDialog;
import net.displayphoenix.ui.ColorTheme;
import net.displayphoenix.util.ColorHelper;
import net.displayphoenix.util.FileHelper;
import net.displayphoenix.util.ThreadHelper;
import netscape.javascript.JSObject;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * @author TBroski
 */
public class BlocklyPanel extends JFXPanel {

    private List<Runnable> runOnLoad = new ArrayList<>();
    private boolean isLoaded = false;
    private WebEngine engine;

    private List<IBlocklyListener> eventListeners = new ArrayList<>();

    private String prevXml;

    /**
     * Main Blockly Panel, uses WebView and JavaScript bridging to manipulate elements in java
     *
     * @see BlocklyDependencyPanel
     */
    public BlocklyPanel() {
        load();
    }

    /**
     * Allows to specify blockly panel size
     *
     * @param width  Width of panel
     * @param height  Height of panel
     */
    public BlocklyPanel(int width, int height) {
        this.setBounds(0, 0, width, height);
        this.setPreferredSize(new Dimension(width, height));
        load();
    }

    /**
     * Allows to get the workspace of the panel in java code
     *
     * @see BlocklyXmlParser#fromWorkspaceXml(String)
     * @see BlocklyPanel#getRawWorkspace()
     * @see BlocklyPanel#addBlocks(String)
     *
     * @return Array of blocks in workspace
     */
    public ImplementedBlock[] getWorkspace() {
        return BlocklyXmlParser.fromWorkspaceXml(getRawWorkspace());
    }

    /**
     * @return Raw xml of BlocklyPanel
     */
    public String getRawWorkspace() {
        return (String) executeJavaScriptSynchronously("Blockly.Xml.domToText(Blockly.Xml.workspaceToDom(workspace, true))");
    }

    /**
     * Parses blocks and add xml string
     *
     * @param blocks  Blocks to parse
     */
    public void addBlocks(ImplementedBlock... blocks) {
        String xml = BlocklyXmlParser.parseWorkspaceXml(blocks);
        addBlocks(xml);
    }

    /**
     * Adds blocks from raw xml string
     *
     * @see BlocklyPanel#addBlocks(ImplementedBlock...)
     * 
     * @param xml  Xml to set
     */
    public void addBlocks(String xml) {
        xml = xml.replace("'", "\\'").replace("\n", "\\n").replace("\r", "\\r");
        if (this.isLoaded) {
            executeJavaScriptSynchronously("Blockly.Xml.domToWorkspace(Blockly.Xml.textToDom('" + xml + "'), workspace)");
            executeJavaScriptSynchronously("workspace.clearUndo()");
        }
        else {
            System.out.println("Can't set workspace before loading. Loading to queue.");
            String finalXml = xml;
            queueOnLoad(() -> addBlocks(finalXml));
        }
    }

    /**
     * Clears workspace
     */
    public void clear() {
        executeJavaScriptSynchronously("workspace.clear()");
    }

    /**
     * Prompts and imports xml file to workspace
     *
     * @param window  Can be null, parent window for FileDialog
     */
    public void importXmlFileToWorkspace(Window window) {
        addBlocks(FileHelper.readAllLines(FileDialog.openFile(window).getFile()));
    }

    /**
     * Queue statement to run when Blockly runs
     *
     * @param runnable  Code to run
     */
    public void queueOnLoad(Runnable runnable) {
        this.runOnLoad.add(runnable);
    }

    /**
     * Whitelist categories to show in BlocklyPanel
     *
     * @param categories  Categories to show
     * @return
     */
    public BlocklyPanel whitelist(Category... categories) {
        ThreadHelper.runOnFxThread(() -> {
            StringBuilder html = new StringBuilder();
            BlocklyHtmlGenerator.appendTopWrapper(html);
            Blockly.appendCategories(html, categories);
            BlocklyHtmlGenerator.appendBottomWrapper(html, Blockly.parseBlocks());
            loadBlockly(html.toString());
        });
        return this;
    }

    private void loadBlockly(String html) {
        this.engine.loadContent(html);
    }

    protected void load() {
        setOpaque(false);
        ThreadHelper.runOnFxThread(() -> {
            WebView blockly = new WebView();
            Scene scene = new Scene(blockly);

            scene.setFill(javafx.scene.paint.Color.rgb(Application.getTheme().getColorTheme().getPrimaryColor().getRed(), Application.getTheme().getColorTheme().getPrimaryColor().getGreen(), Application.getTheme().getColorTheme().getPrimaryColor().getBlue()));
            setScene(scene);

            this.engine = blockly.getEngine();

            StringBuilder html = new StringBuilder();
            BlocklyHtmlGenerator.appendTopWrapper(html);
            Blockly.appendCategories(html, Blockly.getBlocklyCategories());
            BlocklyHtmlGenerator.appendBottomWrapper(html, Blockly.parseBlocks());
            loadBlockly(html.toString().replace("@WIDTH", String.valueOf(this.getWidth() - 10)).replace("@HEIGHT", String.valueOf(this.getHeight() - 20)));

            //Changing colors
            ColorTheme theme = Application.getTheme().getColorTheme();
            Color primary = theme.getPrimaryColor().darker();
            blockly.getChildrenUnmodifiable().addListener(
                    (ListChangeListener<Node>) change -> blockly.lookupAll(".scroll-bar")
                            .forEach(bar -> bar.setVisible(false)));
            ColorTheme finalTheme = theme;
            this.engine.getLoadWorker().stateProperty().addListener((ov, oldState, newState) -> {
                if (!this.isLoaded && newState == Worker.State.SUCCEEDED && this.engine.getDocument() != null) {
                    this.isLoaded = true;
                    Element styleNode = this.engine.getDocument().createElement("style");
                    String css = "body {\n" +
                            "    margin: 0;\n" +
                            "    padding: 0;\n" +
                            "    background: transparent;\n" +
                            "    overflow: hidden;\n" +
                            "}\n" +
                            ".blocklyText {\n" +
                            "    font-family: " + Application.getTheme().getFont().getName().toLowerCase() + ";\n" +
                            "}\n" +
                            "#blockly {\n" +
                            "    position: absolute;\n" +
                            "    background: transparent;\n" +
                            "    top: 0;\n" +
                            "    left: 0;\n" +
                            "    width: 100%;\n" +
                            "    height: 100%;\n" +
                            "}\n" +
                            ".blocklyMainBackground {\n" +
                            "    fill: rgb(" + primary.getRed() + ", " + primary.getGreen() + ", " + primary.getBlue() + ") !important;\n" +
                            "    stroke-width: 0;\n" +
                            "}\n" +
                            "\n" +
                            ".blocklyFlyoutBackground {\n" +
                            "    fill-opacity: 0;\n" +
                            "}\n" +
                            "\n" +
                            ".blocklyFlyout {\n" +
                            "    background-color: " + ColorHelper.convertColorToHexadeimal(finalTheme.getSecondaryColor().darker()) + ";\n" +
                            "}\n\n" +
                            ".blocklyToolboxDiv {\n" +
                            "    background-color: " + ColorHelper.convertColorToHexadeimal(finalTheme.getSecondaryColor()) + ";\n" +
                            "    color: white;\n" +
                            "}" +
                            ".blocklyScrollbarVertical .blocklyScrollbarHandle {\n" +
                            "    fill: " + ColorHelper.convertColorToHexadeimal(primary.brighter()) + ";\n" +
                            "    rx: 0;\n" +
                            "    ry: 0;\n" +
                            "    width: 10px;\n" +
                            "}\n" +
                            "\n" +
                            ".blocklyScrollbarVertical:hover .blocklyScrollbarHandle {\n" +
                            "    fill: " + ColorHelper.convertColorToHexadeimal(finalTheme.getAccentColor()) + ";\n" +
                            "}\n" +
                            "\n" +
                            ".blocklyScrollbarHorizontal .blocklyScrollbarHandle {\n" +
                            "    fill: " + ColorHelper.convertColorToHexadeimal(primary.brighter()) + ";\n" +
                            "    rx: 0;\n" +
                            "    ry: 0;\n" +
                            "    height: 10px;\n" +
                            "}\n" +
                            "\n" +
                            ".blocklyScrollbarHorizontal:hover .blocklyScrollbarHandle {\n" +
                            "    fill: " + ColorHelper.convertColorToHexadeimal(finalTheme.getAccentColor()) + ";\n" +
                            "}";

                    Text styleContent = this.engine.getDocument().createTextNode(css);
                    styleNode.appendChild(styleContent);
                    this.engine.getDocument().getDocumentElement().getElementsByTagName("head").item(0).appendChild(styleNode);

                    Accessor.getPageFor(this.engine).setBackgroundColor(0);

                    executeJavaScriptSynchronously("function onBlocklyEvent(event) {\n" +
                            "if (typeof blocklypanel !== \"undefined\")\n" +
                            "   blocklypanel.fire(event.type, workspace.getBlockById(event.blockId) != null ? workspace.getBlockById(event.blockId).type : '', event.element, event.name, event.oldValue, event.newValue, event.newCoordinate != null ? event.newCoordinate.x : 0, event.newCoordinate != null ? event.newCoordinate.y : 0, event.oldCoordinate != null ? event.oldCoordinate.x : 0, event.oldCoordinate != null ? event.oldCoordinate.y : 0); \n" +
                            "}\n" +
                            "workspace.addChangeListener(onBlocklyEvent);");
                    for (Runnable runnable : this.runOnLoad) {
                        ThreadHelper.runOnFxThread(runnable);
                    }
                    JSObject window = (JSObject) this.engine.executeScript("window");
                    window.setMember("blocklypanel", this);
                    this.prevXml = getRawWorkspace();
                }
            });
        });
    }

    /**
     * Attach event listener to Blockly panel
     *
     * https://developers.google.com/blockly/guides/configure/web/events
     *
     * @param listener
     */
    public void addBlocklyEventListener(IBlocklyListener listener) {
        this.eventListeners.add(listener);
    }

    /**
     * Fires an event, called from JavaScript
     *
     * @param type
     * @param blockId
     * @param element
     * @param name
     * @param oldValue
     * @param newValue
     * @param newCoordinateX
     * @param newCoordinateY
     * @param oldCoordinateX
     * @param oldCoordinateY
     */
    @SuppressWarnings("unused")
    public void fire(String type, String blockId, String element, String name, String oldValue, String newValue, String newCoordinateX, String newCoordinateY, String oldCoordinateX, String oldCoordinateY) {

        BlocklyEvent event = new BlocklyEvent(type, this, Blockly.getBlockFromType(blockId));
        List<ImplementedBlock> prevBlocks = Arrays.asList(BlocklyXmlParser.fromWorkspaceXml(this.prevXml));
        List<ImplementedBlock> nowBlocks = Arrays.asList(getWorkspace());
        List<ImplementedBlock> involvedBlocks = new ArrayList<>();

        for (ImplementedBlock nowBlock : nowBlocks) {
            boolean contains = false;
            for (ImplementedBlock prevBlock : prevBlocks) {
                if (nowBlock.equals(prevBlock)) {
                    contains = true;
                    break;
                }
            }
            if (!contains) {
                involvedBlocks.add(nowBlock);
            }
        }
        for (ImplementedBlock prevBlock : prevBlocks) {
            boolean contains = false;
            for (ImplementedBlock nowBlock : nowBlocks) {
                if (prevBlock.equals(nowBlock)) {
                    contains = true;
                    break;
                }
            }
            if (!contains) {
                involvedBlocks.add(prevBlock);
            }
        }

        ImplementedBlock[] involvedBlocksArray = new ImplementedBlock[involvedBlocks.size()];
        involvedBlocksArray = involvedBlocks.toArray(involvedBlocksArray);

        if (type.equalsIgnoreCase("create")) {
            event = new BlocklyCreateEvent(type, this, Blockly.getBlockFromType(blockId), involvedBlocksArray);
            this.prevXml = getRawWorkspace();
        }
        else if (type.equalsIgnoreCase("delete")) {
            event = new BlocklyDeleteEvent(type, this, Blockly.getBlockFromType(blockId), involvedBlocksArray);
            this.prevXml = getRawWorkspace();
        }
        else if (type.equalsIgnoreCase("change")) {
            event = new BlocklyChangeEvent(type, this, Blockly.getBlockFromType(blockId), element, name, oldValue, newValue);
            this.prevXml = getRawWorkspace();
        }
        else if (type.equalsIgnoreCase("move")) {
            event = new BlocklyMoveEvent(type, this, Blockly.getBlockFromType(blockId), Integer.parseInt(oldCoordinateX), Integer.parseInt(oldCoordinateY), Integer.parseInt(newCoordinateX), Integer.parseInt(newCoordinateY));
            this.prevXml = getRawWorkspace();
        }
        else if (type.equalsIgnoreCase("ui")) {
            event = new BlocklyUIEvent(type, this, Blockly.getBlockFromType(blockId), element, oldValue, newValue);
        }

        for (IBlocklyListener listener : this.eventListeners) {
            listener.onBlocklyEvent(event);
        }
    }

    private Object executeJavaScriptSynchronously(String javaScript) {
        try {
            if (isLoaded) {
                FutureTask<Object> query = new FutureTask<>(() -> this.engine.executeScript(javaScript));
                ThreadHelper.runOnFxThread(query);
                return query.get();
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }
}
