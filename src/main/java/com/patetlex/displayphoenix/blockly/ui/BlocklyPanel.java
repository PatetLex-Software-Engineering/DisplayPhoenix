package com.patetlex.displayphoenix.blockly.ui;

import com.patetlex.displayphoenix.Application;
import com.patetlex.displayphoenix.blockly.Blockly;
import com.patetlex.displayphoenix.blockly.elements.Block;
import com.patetlex.displayphoenix.blockly.elements.workspace.Field;
import com.patetlex.displayphoenix.blockly.gen.BlocklyHtmlGenerator;
import com.patetlex.displayphoenix.blockly.gen.BlocklyXmlParser;
import com.patetlex.displayphoenix.blockly.elements.Category;
import com.patetlex.displayphoenix.blockly.elements.workspace.ImplementedBlock;
import com.patetlex.displayphoenix.blockly.event.BlocklyEvent;
import com.patetlex.displayphoenix.blockly.event.IBlocklyListener;
import com.patetlex.displayphoenix.blockly.event.events.*;
import com.patetlex.displayphoenix.file.DetailedFile;
import com.patetlex.displayphoenix.file.indexly.Indexly;
import com.patetlex.displayphoenix.util.ColorHelper;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.handler.CefLoadHandlerAdapter;
import org.cef.ui.WebPanel;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author TBroski
 */
public class BlocklyPanel extends WebPanel implements BlocklyHtmlGenerator, BlocklyXmlParser {

    private static List<Consumer<BlocklyPanel>> runEveryPanel = new ArrayList<>();

    private List<Runnable> runOnLoad = new ArrayList<>();
    private List<Runnable> runOnNextLoad = new ArrayList<>();
    private Map<String, String[][]> fieldExtensions = new HashMap<>();
    private boolean isLoaded;
    private String futureXml;
    private String currentXml;
    private double scale;
    private boolean hideCategories;

    private List<IBlocklyListener> eventListeners = new ArrayList<>();

    private Category[] categories;
    private List<Block> loadedBlocks = new ArrayList<>();

    /**
     * Main Blockly Panel, uses WebView and JavaScript bridging to manipulate elements in java
     *
     * @see BlocklyDependencyPanel
     */
    public BlocklyPanel() {
        load();
    }

    public static void runOnEveryPanel(Consumer<BlocklyPanel> runnable) {
        runEveryPanel.add(runnable);
    }

    /**
     * Allows to get the workspace of the panel in java code
     *
     * @return Array of blocks in workspace
     * @see BlocklyXmlParser#fromWorkspaceXml(String)
     * @see BlocklyPanel#getRawWorkspace()
     * @see BlocklyPanel#addBlocks(String)
     */
    public ImplementedBlock[] getWorkspace() {
        String xml = getRawWorkspace();
        if (xml != null && !xml.equalsIgnoreCase("no-output")) {
            ImplementedBlock[] arr = fromWorkspaceXml(xml);
            return arr;
        }
        return null;
    }

    /**
     * @return Raw xml of BlocklyPanel
     */
    public String getRawWorkspace() {
        return this.currentXml;
    }

    /**
     * Parses blocks and adds xml string
     *
     * @param blocks Blocks to parse
     */
    public void addBlocks(ImplementedBlock... blocks) {
        String xml = parseWorkspaceXml(blocks);
        addBlocks(xml);
    }

    /**
     * Adds blocks from raw xml string
     *
     * @param xml Xml to set
     * @see BlocklyPanel#addBlocks(ImplementedBlock...)
     */
    public void addBlocks(String xml) {
        xml = xml.replaceAll("\\\\'", "'").replaceAll("'", "\\\\'").replace("\n", "\\n").replace("\r", "\\r");
        if (this.isLoaded()) {
            executeScript("Blockly.Xml.domToWorkspace(Blockly.Xml.textToDom('" + xml + "'), workspace)");
            executeScript("workspace.clearUndo()");
        } else {
            String finalXml = xml;
            queueOnNextLoad(() -> addBlocks(finalXml));
        }
    }

    /**
     * Parses blocks and sets xml string
     *
     * @param blocks Blocks to parse
     */
    public void setWorkspace(ImplementedBlock... blocks) {
        String xml = parseWorkspaceXml(blocks);
        setWorkspace(xml);
    }

    /**
     * Sets blocks from raw xml string
     *
     * @param xml  Xml to set
     * @see BlocklyPanel#setWorkspace(ImplementedBlock...)
     */
    public void setWorkspace(String xml) {
        xml = xml.replaceAll("\\\\'", "'").replaceAll("'", "\\\\'").replace("\n", "\\n").replace("\r", "\\r");
        if (this.isLoaded) {
            executeScript("Blockly.Xml.domToWorkspace(Blockly.Xml.textToDom('" + xml + "'), workspace)");
        } else {
            String finalXml = xml;
            queueOnNextLoad(() -> setWorkspace(finalXml));
        }
    }

    /**
     * Clears workspace
     */
    public void clear() {
        executeScript("workspace.clear()");
    }

    /**
     * Prompts and imports xml file to workspace
     */
    public void importXmlFileToWorkspace() {
        Indexly.openFile(new Consumer<DetailedFile>() {
            @Override
            public void accept(DetailedFile detailedFile) {
                addBlocks(detailedFile.read());
            }
        });
    }

    /**
     * Queue statement to run when Blockly loads, continuous
     *
     * @param runnable Code to run
     */
    public void queueOnLoad(Runnable runnable) {
        this.runOnLoad.add(runnable);
    }

    /**
     * Queue statement to run when Blockly loads next time, once
     *
     * @param runnable Code to run
     */
    public void queueOnNextLoad(Runnable runnable) {
        this.runOnNextLoad.add(runnable);
    }

    /**
     * Whitelist categories to show in BlocklyPanel
     * <p>
     * if categories is null, no categories will show
     *
     * @param categories Categories to show
     * @return
     */
    public BlocklyPanel whitelist(Category... categories) {
        if (categories == null) {
            hideCategories();
            return this;
        }
        this.categories = categories;
        reload();
        return this;
    }

    /**
     * Blacklist categories to show in BlocklyPanel
     *
     * @param categories Categories to blacklist
     * @return
     * @throws NullPointerException if categories is null
     */
    public BlocklyPanel blacklist(Category... categories) {
        Category[] registeredCategories = Blockly.getBlocklyCategories();
        Category[] categoriesToAdd = new Category[registeredCategories.length - categories.length];
        int i = 0;
        for (Category registeredCategory : registeredCategories) {
            for (Category blacklistedCategory : categories) {
                if (registeredCategory.equals(blacklistedCategory)) {
                    continue;
                }
            }
            categoriesToAdd[i] = registeredCategory;
            i++;
        }
        this.categories = categoriesToAdd;
        reload();
        return this;
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    /**
     * Hides category panel
     */
    @Override
    public void hideCategories() {
        this.hideCategories = true;
        reload();
    }

    /**
     * @return  Does BlocklyPanel have category panel
     */
    @Override
    public boolean hasCategories() {
        return !this.hideCategories;
    }

    /**
     * @return Categories of BlocklyPanel
     */
    public Category[] getCategories() {
        return categories != null ? categories : Blockly.getBlocklyCategories();
    }

    /**
     * Register block with panel
     *
     * @param block  Block to register
     */
    public void loadBlock(Block block) {
        this.loadedBlocks.remove(block);
        this.loadedBlocks.add(block);
        reload();
    }

    /**
     * Reloads the blockly panel, resizes etc.
     */
    public void reload() {
        if (!cachedVars.isEmpty())
            cachedVars.pop();
        StringBuilder html = new StringBuilder();
        this.appendTopWrapper(html);
        Blockly.appendCategories(html, getCategories());
        this.appendBottomWrapper(html, Blockly.parseBlocksToJsonArray(this.fieldExtensions, this.loadedBlocks != null ? this.loadedBlocks.toArray(new Block[this.loadedBlocks.size()]) : new Block[0]));
        if (this.isLoaded) {
            this.isLoaded = false;
        }
        if (getBrowser() != null) {
            this.futureXml = getRawWorkspace();
            loadHtml(html.toString());
        }
    }

    private void load() {
        this.isLoaded = false;
        setScale(0.8);
        for (Consumer<BlocklyPanel> runnable : runEveryPanel) {
            runnable.accept(this);
        }

        StringBuilder html = new StringBuilder();
        this.appendTopWrapper(html);
        Blockly.appendCategories(html, getCategories());
        this.appendBottomWrapper(html, Blockly.parseBlocksToJsonArray(this.fieldExtensions, this.loadedBlocks.toArray(new Block[this.loadedBlocks.size()])));
        loadHtml(html.toString());

        setMember("blocklypanel", this);
        addLoadHandler(new CefLoadHandlerAdapter() {
            @Override
            public void onLoadEnd(CefBrowser browser, CefFrame frame, int httpStatusCode) {
                super.onLoadEnd(browser, frame, httpStatusCode);
                if (!BlocklyPanel.this.isLoaded) {
                    BlocklyPanel.this.isLoaded = true;
                    if (BlocklyPanel.this.futureXml != null) {
                        setWorkspace(BlocklyPanel.this.futureXml);
                        BlocklyPanel.this.futureXml = null;
                    }
                    String code = " function onBlocklyEvent(event) {" +
                            "if (blocklypanel !== null && typeof blocklypanel !== \"undefined\" && event != null)" +
                            "blocklypanel.fire((event.type !== null && typeof event.type !== \"undefined\") ? event.type.toString() : '', (workspace.getBlockById(event.blockId) !== null && typeof workspace.getBlockById(event.blockId) !== \"undefined\") ? workspace.getBlockById(event.blockId).type.toString() : '', (event.element !== null && typeof event.element !== \"undefined\") ? event.element.toString() : '', (event.name !== null && typeof event.name !== \"undefined\") ? event.name.toString() : '', (event.oldValue !== null && typeof event.oldValue !== \"undefined\") ? event.oldValue.toString() : '', (event.newValue !== null && typeof event.newValue !== \"undefined\") ? event.newValue.toString() : '', (event.newCoordinate !== null && typeof event.newCoordinate !== \"undefined\") ? event.newCoordinate.x : 0, (event.newCoordinate !== null && typeof event.newCoordinate !== \"undefined\") ? event.newCoordinate.y : 0, (event.oldCoordinate !== null && typeof event.oldCoordinate !== \"undefined\") ? event.oldCoordinate.x : 0, (event.oldCoordinate !== null && typeof event.oldCoordinate !== \"undefined\") ? event.oldCoordinate.y : 0);" +
                            "}" +
                            "workspace.addChangeListener(onBlocklyEvent);";
                    browser.executeJavaScript(generateMembers() + code, browser.getURL(), 1);
                    for (Category category : Blockly.getBlocklyCategories()) {
                        for (Block block : Blockly.getBlocksFromCategory(category)) {
                            if (block.getBlocklyJson() != null && block.getBlocklyJson().get("script") != null) {
                                executeScript(block.getBlocklyJson().get("script").getAsString());
                            }
                        }
                    }
                    for (Runnable runnable : BlocklyPanel.this.runOnLoad) {
                        runnable.run();
                    }
                    List<Runnable> runnablesToDestroy = new ArrayList<>();
                    for (Runnable runnable : BlocklyPanel.this.runOnNextLoad) {
                        runnable.run();
                        runnablesToDestroy.add(runnable);
                    }
                    for (Runnable runnable : runnablesToDestroy) {
                        BlocklyPanel.this.runOnNextLoad.remove(runnable);
                    }
                }
            }
        });
    }

    /**
     * Adds options to blockly inputs, specifically the field_dropdown type
     * <p>
     * For example, if a block input has the type "field_dropdown" and the identifier
     * <code>"extend": "places"</code>
     * The parameter extensionKey is a String value of "places"
     * The parameter optionsToAdd is a Field array (key, value) of
     * {("Chicago", "CHICAGO"), ("New York", "NEW_YORK")}
     * Then the CHICAGO and NEW_YORK option will be added to the block field_dropdown input
     *
     * @param extensionKey The key identifier for blocks
     * @param optionsToAdd All the options to add to the block input options
     */
    public void addFieldExtensions(String extensionKey, Field... optionsToAdd) {
        if (!this.fieldExtensions.containsKey(extensionKey)) {
            String[][] options = new String[optionsToAdd.length][2];
            for (int i = 0; i < optionsToAdd.length; i++) {
                options[i] = new String[]{optionsToAdd[i].getKey(), optionsToAdd[i].getValue()};
            }
            this.fieldExtensions.put(extensionKey, options);
            reload();
        }
    }

    /**
     * Attach event listener to Blockly panel
     * <p>
     * https://developers.google.com/blockly/guides/configure/web/events
     *
     * @param listener
     */
    public void addBlocklyEventListener(IBlocklyListener listener) {
        this.eventListeners.add(listener);
    }

    /**
     * Fires an event, called from JavaScript
     */
    @SuppressWarnings("unused")
    public void fire(Object type, Object blockId, Object element, Object name, Object oldValue, Object newValue, Object newCoordinateX, Object newCoordinateY, Object oldCoordinateX, Object oldCoordinateY) {
        BlocklyEvent event = new BlocklyEvent((String) type, this, getBlockFromType((String) blockId));

        if (type.equals("create")) {
            event = new BlocklyCreateEvent((String) type, this, getBlockFromType((String) blockId));
        } else if (type.equals("delete")) {
            event = new BlocklyDeleteEvent((String) type, this, getBlockFromType((String) blockId));
        } else if (type.equals("change")) {
            event = new BlocklyChangeEvent((String) type, this, getBlockFromType((String) blockId), (String) element, (String) name, (String) oldValue, (String) newValue);
        } else if (type.equals("move")) {
            event = new BlocklyMoveEvent((String) type, this, getBlockFromType((String) blockId), Math.round((float) (double) oldCoordinateX), Math.round((float) (double) oldCoordinateY), Math.round((float) (double) newCoordinateX), Math.round((float) (double) newCoordinateY));
        } else if (type.equals("ui")) {
            event = new BlocklyUIEvent((String) type, this, getBlockFromType((String) blockId), (String) element, (String) oldValue, (String) newValue);
        }

        BlocklyEvent finalEvent = event;
        executeScript("return Blockly.Xml.domToText(Blockly.Xml.workspaceToDom(workspace, true))", new Consumer<Object>() {
            @Override
            public void accept(Object o) {
                BlocklyPanel.this.currentXml = (String) o;
                for (IBlocklyListener listener : BlocklyPanel.this.eventListeners) {
                    listener.onBlocklyEvent(finalEvent);
                }
            }
        });
    }

    @Override
    public void setForeground(Color fg) {
        super.setForeground(fg);
        reload();
    }

    @Override
    public void setBackground(Color bg) {
        super.setBackground(bg);
        reload();
    }

    @Override
    public String getCss() {
        return "* {\n" +
                "    font-family: " + getFont().getFontName().toLowerCase() + ";\n" +
                "}\n" +
                "\n" +
                "::-webkit-scrollbar {\n" +
                "    width: 7px;\n" +
                "}\n" +
                "\n" +
                "body {\n" +
                "    margin: 0;\n" +
                "    padding: 0;\n" +
                "    background: transparent;\n" +
                "    overflow: hidden;\n" +
                "}\n" +
                "\n" +
                "#toolbox {\n" +
                "    display: none;\n" +
                "}\n" +
                "\n" +
                "#area {\n" +
                "    width: 100vw;\n" +
                "    height: 100vh;\n" +
                "    background: transparent;\n" +
                "}\n" +
                "\n" +
                "#blocklyDiv {\n" +
                "    position: absolute;\n" +
                "    background: transparent;\n" +
                "    top: 0;\n" +
                "    left: 0;\n" +
                "    width: 100%;\n" +
                "    height: 100%;\n" +
                "}\n" +
                "\n" +
                ".blocklySvg {\n" +
                "    background-color: transparent !important;\n" +
                "}\n" +
                "\n" +
                ".blocklyTreeLabel {\n" +
                "    font-family: " + getFont().getFontName().toLowerCase() + ";\n" +
                "    font-size: 11px;\n" +
                "}\n" +
                "\n" +
                ".blocklyText {\n" +
                "    font-family: " + getFont().getFontName().toLowerCase() + ";\n" +
                "}\n" +
                "\n" +
                ".blocklyMainBackground {\n" +
                "    stroke-width: 0;\n" +
                "}\n" +
                "\n" +
                ".blocklyFlyoutBackground {\n" +
                "    fill-opacity: 0;\n" +
                "}\n" +
                "\n" +
                ".blocklyScrollbarVertical .blocklyScrollbarHandle {\n" +
                "    rx: 0;\n" +
                "    ry: 0;\n" +
                "    width: 7px;\n" +
                "}\n" +
                "\n" +
                ".blocklyScrollbarHorizontal .blocklyScrollbarHandle {\n" +
                "    rx: 0;\n" +
                "    ry: 0;\n" +
                "    height: 7px;\n" +
                "}\n" +
                "\n" +
                "\n" +
                ".whlab > .blocklyFlyoutLabelText {\n" +
                "    font-family: sans-serif;\n" +
                "    font-size: 14px;\n" +
                "}\n" +
                "\n" +
                ".small-text {\n" +
                "    font-size: 12px;\n" +
                "}\n" +
                "\n" +
                ".blocklyWidgetDiv .goog-menu {\n" +
                "    border-radius: 0;\n" +
                "}\n" +
                "\n" +
                ".condition-label {\n" +
                "    font-size: 13px !important;\n" +
                "}\n" +
                "\n" +
                ".blocklyDropDownContent {\n" +
                "    position: relative;\n" +
                "}\n" + // Theming
                "::-webkit-scrollbar-track {\n" +
                "    background: " + ColorHelper.convertColorToHexadeimal(getBackground()) + ";\n" +
                "}\n" +
                "\n" +
                "::-webkit-scrollbar-thumb {\n" +
                "    background: " + ColorHelper.convertColorToHexadeimal(getBackground().brighter()) + ";\n" +
                "}\n" +
                "\n" +
                "::-webkit-scrollbar-thumb:hover {\n" +
                "    background: " + ColorHelper.convertColorToHexadeimal(getBackground().brighter().brighter()) + ";\n" +
                "}\n" +
                "\n" +
                ".blocklyMainBackground {\n" +
                "    fill: rgba(" + getBackground().getRed() + ", " + getBackground().getGreen() + ", " + getBackground().getBlue() + ", 0.66) !important;\n" +
                "}\n" +
                "\n" +
                ".blocklyToolboxDiv {\n" +
                "    background-color: " + ColorHelper.convertColorToHexadeimal(getForeground()) + ";\n" +
                "    color: " + ColorHelper.convertColorToHexadeimal(Application.getTheme().getColorTheme().getTextColor()) + ";\n" +
                "}\n" +
                "\n" +
                ".blocklyTreeRow:not(.blocklyTreeSelected):hover {\n" +
                "    background-color: " + ColorHelper.convertColorToHexadeimal(Application.getTheme().getColorTheme().getAccentColor()) + " !important;\n" +
                "    color: " + ColorHelper.convertColorToHexadeimal(Application.getTheme().getColorTheme().getTextColor()) + " !important;\n" +
                "}\n" +
                "\n" +
                ".blocklyTreeSeparator {\n" +
                "    border-bottom: solid " + ColorHelper.convertColorToHexadeimal(getForeground()) + " 1px;\n" +
                "}\n" +
                "\n" +
                ".blocklyFlyout {\n" +
                "    background-color: " + ColorHelper.convertColorToHexadeimal(getForeground()) + "c2;\n" +
                "}\n" +
                "\n" +
                ".blocklyScrollbarVertical .blocklyScrollbarHandle {\n" +
                "    fill: " + ColorHelper.convertColorToHexadeimal(getBackground()) + ";\n" +
                "}\n" +
                "\n" +
                ".blocklyScrollbarVertical:hover .blocklyScrollbarHandle {\n" +
                "    fill: " + ColorHelper.convertColorToHexadeimal(getBackground().brighter()) + ";\n" +
                "}\n" +
                "\n" +
                ".blocklyScrollbarHorizontal .blocklyScrollbarHandle {\n" +
                "    fill: " + ColorHelper.convertColorToHexadeimal(getBackground()) + ";\n" +
                "}\n" +
                "\n" +
                ".blocklyScrollbarHorizontal:hover .blocklyScrollbarHandle {\n" +
                "    fill: " + ColorHelper.convertColorToHexadeimal(getBackground().brighter()) + ";\n" +
                "}\n" +
                "\n" +
                ".whlab > .blocklyFlyoutLabelText {\n" +
                "    fill: " + ColorHelper.convertColorToHexadeimal(Application.getTheme().getColorTheme().getTextColor()) + ";\n" +
                "}\n" +
                "\n" +
                ".blocklyDropDownDiv {\n" +
                "    background: " + ColorHelper.convertColorToHexadeimal(getBackground()) + " !important;\n" +
                "    border-color: " + ColorHelper.convertColorToHexadeimal(Application.getTheme().getColorTheme().getAccentColor()) + " !important;\n" +
                "}\n" +
                "\n" +
                ".blocklyDropDownDiv .goog-menuitem-content {\n" +
                "    color: " + ColorHelper.convertColorToHexadeimal(Application.getTheme().getColorTheme().getTextColor()) + " !important;\n" +
                "}\n" +
                "\n" +
                ".blocklyTooltipDiv {\n" +
                "    background-color: " + ColorHelper.convertColorToHexadeimal(getForeground()) + ";\n" +
                "    color: " + ColorHelper.convertColorToHexadeimal(Application.getTheme().getColorTheme().getTextColor()) + ";\n" +
                "}\n" +
                "\n" +
                ".blocklyWidgetDiv .goog-menu {\n" +
                "    background: " + ColorHelper.convertColorToHexadeimal(getForeground()) + " !important;\n" +
                "    border-color: " + ColorHelper.convertColorToHexadeimal(getBackground()) + " !important;\n" +
                "}\n" +
                "\n" +
                ".blocklyWidgetDiv .goog-menuitem-content {\n" +
                "    color: " + ColorHelper.convertColorToHexadeimal(Application.getTheme().getColorTheme().getTextColor()) + ";\n" +
                "}\n" +
                "\n" +
                ".blocklyCommentTextarea {\n" +
                "    background: " + ColorHelper.convertColorToHexadeimal(getForeground()) + ";\n" +
                "    color: " + ColorHelper.convertColorToHexadeimal(Application.getTheme().getColorTheme().getTextColor()) + ";\n" +
                "}\n" +
                "\n" +
                ".blocklyMutatorBackground {\n" +
                "    fill: " + ColorHelper.convertColorToHexadeimal(getForeground()) + ";\n" +
                "    stroke-width: 0;\n" +
                "}\n" +
                "\n" +
                ".blocklySelected > .blocklyPath {\n" +
                "    stroke: " + ColorHelper.convertColorToHexadeimal(Application.getTheme().getColorTheme().getAccentColor()) + " !important;\n" +
                "    stroke-width: 1px !important;\n" +
                "}\n" +
                "\n" +
                ".blocklyHtmlTextAreaInput {\n" +
                "    background: " + ColorHelper.convertColorToHexadeimal(getForeground()) + ";\n" +
                "    color: white;\n" +
                "}";
    }

    @Override
    public void setScale(double scale) {
        this.scale = scale;
    }

    @Override
    public double getScale() {
        return this.scale;
    }

    @Override
    public Block getBlockFromType(String type) {
        for (Block block : this.loadedBlocks) {
            if (block.getType().equalsIgnoreCase(type)) {
                return block;
            }
        }
        return Blockly.getBlockFromType(type);
    }
}
