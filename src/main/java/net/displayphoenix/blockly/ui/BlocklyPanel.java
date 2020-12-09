package net.displayphoenix.blockly.ui;

import net.displayphoenix.Application;
import net.displayphoenix.blockly.Blockly;
import net.displayphoenix.blockly.elements.workspace.Field;
import net.displayphoenix.blockly.gen.BlocklyHtmlGenerator;
import net.displayphoenix.blockly.gen.BlocklyXmlParser;
import net.displayphoenix.blockly.elements.Category;
import net.displayphoenix.blockly.elements.workspace.ImplementedBlock;
import net.displayphoenix.blockly.event.BlocklyEvent;
import net.displayphoenix.blockly.event.IBlocklyListener;
import net.displayphoenix.blockly.event.events.*;
import net.displayphoenix.file.FileDialog;
import net.displayphoenix.util.ColorHelper;
import net.displayphoenix.util.FileHelper;
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
public class BlocklyPanel extends WebPanel implements BlocklyHtmlGenerator {

    private List<Runnable> runOnLoad = new ArrayList<>();
    private Map<String, String[][]> fieldExtensions = new HashMap<>();
    private boolean isLoaded;
    private String futureXml;
    private String currentXml;
    private double scale;

    private List<IBlocklyListener> eventListeners = new ArrayList<>();

    private Category[] categories;

    /**
     * Main Blockly Panel, uses WebView and JavaScript bridging to manipulate elements in java
     *
     * @see BlocklyDependencyPanel
     */
    public BlocklyPanel() {
        load();
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
            ImplementedBlock[] arr = BlocklyXmlParser.fromWorkspaceXml(xml);
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
     * Parses blocks and add xml string
     *
     * @param blocks Blocks to parse
     */
    public void addBlocks(ImplementedBlock... blocks) {
        String xml = BlocklyXmlParser.parseWorkspaceXml(blocks);
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
        if (this.isLoaded) {
            executeScript("Blockly.Xml.domToWorkspace(Blockly.Xml.textToDom('" + xml + "'), workspace)");
            executeScript("workspace.clearUndo()");
        } else {
            String finalXml = xml;
            queueOnLoad(() -> addBlocks(finalXml));
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
        addBlocks(FileHelper.readAllLines(FileDialog.openFile().getFile()));
    }

    /**
     * Queue statement to run when Blockly runs
     *
     * @param runnable Code to run
     */
    public void queueOnLoad(Runnable runnable) {
        this.runOnLoad.add(runnable);
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

    /**
     * @return Categories of BlocklyPanel
     */
    public Category[] getCategories() {
        return categories != null ? categories : Blockly.getBlocklyCategories();
    }

    private void loadBlockly(String html) {
        loadHtml(html.replace("@WIDTH", String.valueOf(Math.round(this.getWidth() * 0.666F))).replace("@HEIGHT", String.valueOf(Math.round(this.getHeight() * 0.676F))));
    }

    /**
     * Reloads the blockly panel, resizes etc.
     */
    public void reload() {
        StringBuilder html = new StringBuilder();
        this.appendTopWrapper(html);
        Blockly.appendCategories(html, getCategories());
        this.appendBottomWrapper(html, Blockly.parseBlocksToJsonArray(this.fieldExtensions));
        if (this.isLoaded) {
            this.isLoaded = false;
        }
        if (getBrowser() != null) {
            this.futureXml = getRawWorkspace();
            loadBlockly(html.toString());
        }
    }

    private void load() {
        this.isLoaded = false;
        setScale(0.8);

        StringBuilder html = new StringBuilder();
        this.appendTopWrapper(html);
        Blockly.appendCategories(html, getCategories());
        this.appendBottomWrapper(html, Blockly.parseBlocksToJsonArray(this.fieldExtensions));
        loadBlockly(html.toString());

        setMember("blocklypanel", this);
        addLoadHandler(new CefLoadHandlerAdapter() {
            @Override
            public void onLoadEnd(CefBrowser browser, CefFrame frame, int httpStatusCode) {
                super.onLoadEnd(browser, frame, httpStatusCode);
                if (!isLoaded) {
                    isLoaded = true;
                    if (futureXml != null) {
                        addBlocks(futureXml);
                        futureXml = null;
                    }
                    String code = " function onBlocklyEvent(event) {" +
                            "if (typeof blocklypanel !== \"undefined\")" +
                            "   blocklypanel.fire(event.type, workspace.getBlockById(event.blockId) !== null ? workspace.getBlockById(event.blockId).type : '', event.element, event.name, event.oldValue, event.newValue, event.newCoordinate != null ? event.newCoordinate.x : 0, event.newCoordinate != null ? event.newCoordinate.y : 0, event.oldCoordinate != null ? event.oldCoordinate.x : 0, event.oldCoordinate != null ? event.oldCoordinate.y : 0);" +
                            "}" +
                            "workspace.addChangeListener(onBlocklyEvent);";
                    browser.executeJavaScript(testForJavaScriptMembers(code) + code, browser.getURL(), 1);
                    for (Runnable runnable : runOnLoad) {
                        runnable.run();
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
        BlocklyEvent event = new BlocklyEvent((String) type, this, Blockly.getBlockFromType((String) blockId));

        if (type.equals("create")) {
            event = new BlocklyCreateEvent((String) type, this, Blockly.getBlockFromType((String) blockId));
        } else if (type.equals("delete")) {
            event = new BlocklyDeleteEvent((String) type, this, Blockly.getBlockFromType((String) blockId));
        } else if (type.equals("change")) {
            event = new BlocklyChangeEvent((String) type, this, Blockly.getBlockFromType((String) blockId), (String) element, (String) name, (String) oldValue, (String) newValue);
        } else if (type.equals("move")) {
            event = new BlocklyMoveEvent((String) type, this, Blockly.getBlockFromType((String) blockId), Math.round((float) (double) oldCoordinateX), Math.round((float) (double) oldCoordinateY), Math.round((float) (double) newCoordinateX), Math.round((float) (double) newCoordinateY));
        } else if (type.equals("ui")) {
            event = new BlocklyUIEvent((String) type, this, Blockly.getBlockFromType((String) blockId), (String) element, (String) oldValue, (String) newValue);
        }

        BlocklyEvent finalEvent = event;
        executeScript("Blockly.Xml.domToText(Blockly.Xml.workspaceToDom(workspace, true))", new Consumer<Object>() {
            @Override
            public void accept(Object o) {
                currentXml = (String) o;
                for (IBlocklyListener listener : eventListeners) {
                    listener.onBlocklyEvent(finalEvent);
                }
            }
        });
    }

    @Override
    public void reshape(int x, int y, int w, int h) {
        boolean flag = x != getX() || y != getY() || w != getWidth() || h != getHeight();
        super.reshape(x, y, w, h);
        if (flag)
            reload();
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
                "#blockly {\n" +
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
}
