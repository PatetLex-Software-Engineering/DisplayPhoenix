package com.patetlex.displayphoenix.generation;

import com.patetlex.displayphoenix.bitly.elements.Bit;
import com.patetlex.displayphoenix.bitly.elements.workspace.ImplementedBit;
import com.patetlex.displayphoenix.bitly.ui.BitWidget;
import com.patetlex.displayphoenix.blockly.elements.Block;
import com.patetlex.displayphoenix.blockly.elements.IMutator;
import com.patetlex.displayphoenix.blockly.elements.workspace.Field;
import com.patetlex.displayphoenix.blockly.elements.workspace.ImplementedBlock;
import com.patetlex.displayphoenix.blockly.elements.workspace.Mutation;
import com.patetlex.displayphoenix.blockly.gen.BlocklyXmlParser;
import com.patetlex.displayphoenix.generation.impl.JavaModule;
import com.patetlex.displayphoenix.generation.impl.JavaScriptModule;
import com.patetlex.displayphoenix.generation.impl.LuaModule;
import com.patetlex.displayphoenix.ui.widget.ProvisionWidget;
import com.patetlex.displayphoenix.ui.widget.ResourceWidget;
import com.patetlex.displayphoenix.ui.widget.TextField;
import com.patetlex.displayphoenix.ui.widget.Toggle;
import com.patetlex.displayphoenix.util.StringHelper;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.*;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author TBroski
 */
public class Module implements BlocklyXmlParser {

    public static final Module JAVA = new JavaModule();
    public static final Module JAVASCRIPT = new JavaScriptModule();
    public static final Module LUA = new LuaModule();

    private static final List<Module> REGISTERED_MODULES = new ArrayList<>();

    private final Map<Bit, String> bitCode = new HashMap<>();

    private final Map<Block, String> blockCode = new HashMap<>();
    private final Map<Block, IFieldManipulator> fieldManipulator = new HashMap<>();
    private final Map<Block, IValueManipulator> valueManipulator = new HashMap<>();
    private final Map<Block, IMutator> blockMutators = new HashMap<>();
    private final Map<Block, IMarker> markerListener = new HashMap<>();
    private final List<Block> blockSyntax = new ArrayList<>();
    private String name;

    private int index;

    public Module(String name) {
        this.name = name;
    }

    public static Module getModuleFromName(String name) {
        for (Module module : REGISTERED_MODULES) {
            if (module.getName().equalsIgnoreCase(name)) {
                return module;
            }
        }
        return null;
    }

    public static void registerModule(Module module) {
        if (!REGISTERED_MODULES.contains(module)) {
            REGISTERED_MODULES.add(module);
        }
    }

    public String[] getFlags(String key) {
        return new String[] {"$[" + key + "%", "]"};
    }

    public String addSyntax(String input) {
        return input;
    }

    public void registerBitCode(Bit bit, String codeInput) {
        if (this.bitCode.containsKey(bit)) {
            this.bitCode.remove(bit);
        }
        this.bitCode.put(bit, codeInput);
    }

    public void registerBlockCode(Block block, String codeInput) {
        if (this.blockCode.containsKey(block)) {
            this.blockCode.remove(block);
        }
        this.blockCode.put(block, codeInput);
    }

    public void escapeSyntax(Block block) {
        if (!blockSyntax.contains(block)) {
            blockSyntax.add(block);
        }
    }

    public void manipulateField(Block block, IFieldManipulator manipulator) {
        if (fieldManipulator.containsKey(block)) {
            fieldManipulator.remove(block);
        }
        fieldManipulator.put(block, manipulator);
    }
    public void manipulateValue(Block block, IValueManipulator manipulator) {
        if (valueManipulator.containsKey(block)) {
            valueManipulator.remove(block);
        }
        valueManipulator.put(block, manipulator);
    }
    public void addMarkerListener(Block block, IMarker listener) {
        if (markerListener.containsKey(block)) {
            markerListener.remove(block);
        }
        markerListener.put(block, listener);
    }

    public void attachMutator(Block block, IMutator mutator) {
        if (this.blockMutators.containsKey(block)) {
            this.blockMutators.remove(block);
        }
        this.blockMutators.put(block, mutator);
    }

    public String getCodeFromBit(Bit bit) {
        return this.bitCode.get(bit);
    }

    public String getCodeFromBlock(Block block) {
        return this.blockCode.get(block);
    }

    public String getCodeFromBit(ImplementedBit bit) {
        String code = null;
        for (Bit bitKey : this.bitCode.keySet()) {
            if (bitKey.getType().equalsIgnoreCase(bit.getBit().getType())) {
                code = this.bitCode.get(bitKey);
                break;
            }
        }
        String[] plugins = StringHelper.substringsBetween(code, getFlags("plugin")[0], getFlags("plugin")[1]);
        String[] bits = StringHelper.substringsBetween(code, getFlags("bit")[0], getFlags("bit")[1]);
        if (bits != null) {
            for (BitWidget[] page : bit.getBit().getBits()) {
                for (BitWidget widget : page) {
                    for (String input : bits) {
                        if (input.equalsIgnoreCase(widget.getFlag())) {
                            ScriptEngineManager manager = new ScriptEngineManager();
                            ScriptEngine engine = manager.getEngineByExtension("js");
                            Invocable invocable = (Invocable) engine;
                            try {
                                engine.eval(bit.getBit().getScript() != null ? bit.getBit().getScript() : "");
                            } catch (ScriptException e) {
                                e.printStackTrace();
                            }
                            String val = getValueOfWidget(bit, widget);
                            if (val == null)
                                val = "";
                            try {
                                Object newVal = invocable.invokeFunction("widgetCodeRetrieved", widget.getFlag(), val);
                                val = String.valueOf(newVal);
                            } catch (NoSuchMethodException ex) {

                            } catch (ScriptException ex) {
                                ex.printStackTrace();
                            }
                            code = code.replace(getFlags("bit")[0] + input + getFlags("bit")[1], val);
                            break;
                        }
                    }
                }
            }
        }
        if (plugins != null) {
            for (String input : plugins) {
                String val = " ";
                for (ImplementedBit pluginBit : bit.getImplementedPlugins()) {
                    if (input.equalsIgnoreCase(pluginBit.getBit().getPluginFlag())) {
                        String bitVal = getCodeFromBit(pluginBit);
                        if (bitVal == null)
                            bitVal = "";
                        val += bitVal + "\n";
                    }
                }
                code = code.replace(getFlags("plugin")[0] + input + getFlags("plugin")[1], val);
            }
        }
        return code;
    }

    public String getCodeFromBlock(ImplementedBlock block) {
        String code = getRawCode(block.getBlock());
        for (Mutation mutation : block.getMutations()) {
            for (int im = 0; im < mutation.getAmount(); im++) {
                code += this.blockMutators.get(block.getBlock()).getCodeFromMutation(mutation.getKey(), im + 1);
            }
        }
        // Change markers
        String[] markers = StringHelper.substringsBetween(code, getFlags("marker")[0], getFlags("marker")[1]);
        if (markers != null) {
            for (String input : markers) {
                String marker = this.markerListener.get(block.getBlock()).getCode(input, block);
                code = code.replace(getFlags("marker")[0] + input + getFlags("marker")[1], marker != null ? marker : "");
            }
        }
        String[] increments = StringHelper.substringsBetween(code, getFlags("increment")[0], getFlags("increment")[1]);
        if (increments != null && increments.length > 0) {
            index++;
            for (String input : increments) {
                code = code.replace(getFlags("increment")[0] + input + getFlags("increment")[1], input + index);
            }
        }
        String[] fieldInputs = StringHelper.substringsBetween(code, getFlags("field")[0], getFlags("field")[1]);
        String[] statementInputs = StringHelper.substringsBetween(code, getFlags("statement")[0], getFlags("statement")[1]);
        String[] valueInputs = StringHelper.substringsBetween(code, getFlags("value")[0], getFlags("value")[1]);

        if (!doesBlockContainFields(block)) {
            throw new InvalidParameterException();
        }

        // Change fields
        if (fieldInputs != null) {
            for (String input : fieldInputs) {
                for (Field field : block.getFields()) {
                    if (input.equalsIgnoreCase(field.getKey())) {
                        code = code.replace(getFlags("field")[0] + input + getFlags("field")[1], fieldManipulator.containsKey(block.getBlock()) ? fieldManipulator.get(block.getBlock()).getFieldCode(field) : field.getValue());
                        break;
                    }
                }
            }
        }

        // Add statements
        if (statementInputs != null) {
            for (String input : statementInputs) {
                for (String statementKey : block.getStatementBlocks().keySet()) {
                    if (input.equalsIgnoreCase(statementKey)) {
                        String codeAddition = "";
                        for (ImplementedBlock statementBlock : block.getStatementBlocks().get(statementKey)) {
                            codeAddition += getCodeFromBlock(statementBlock) + "\n";
                        }
                        code = code.replace(getFlags("statement")[0] + input + getFlags("statement")[1], codeAddition);
                        break;
                    }
                }
            }
        }

        // Change values
        if (valueInputs != null) {
            for (String input : valueInputs) {
                for (String valueKey : block.getValueBlocks().keySet()) {
                    if (input.equalsIgnoreCase(valueKey)) {
                        String codeAddition = "";
                        for (ImplementedBlock valueBlock : block.getValueBlocks().get(valueKey)) {
                            codeAddition += this.valueManipulator.containsKey(block.getBlock()) ? this.valueManipulator.get(block.getBlock()).getValueCode(valueKey, getCodeFromBlock(valueBlock)) : getCodeFromBlock(valueBlock);
                        }
                        code = code.replace(getFlags("value")[0] + input + getFlags("value")[1], codeAddition);
                        break;
                    }
                }
            }
        }
        return blockSyntax.contains(block.getBlock()) ? code : addSyntax(code);
    }

    protected String getValueOfWidget(ImplementedBit bit, BitWidget widget) {
        return widget.getStyle().getCode(this, bit.getRawComponent(widget));
    }

    public String getRawCode(Block block) {
        return this.blockCode.get(block);
    }

    public String getName() {
        return name;
    }

    public boolean doesBlockContainFields(ImplementedBlock block) {
        String code = getRawCode(block.getBlock());
        for (Mutation mutation : block.getMutations()) {
            for (int im = mutation.getAmount(); im > 0; im--) {
                code += this.blockMutators.get(block.getBlock()).getCodeFromMutation(mutation.getKey(), im + 1);
            }
        }
        // Change markers
        String[] markers = StringHelper.substringsBetween(code, getFlags("marker")[0], getFlags("marker")[1]);
        if (markers != null) {
            for (String input : markers) {
                code = code.replace(getFlags("marker")[0] + input + getFlags("marker")[1], this.markerListener.get(block.getBlock()).getCode(input, block));
            }
        }
        String[] fieldInputs = StringHelper.substringsBetween(code, getFlags("field")[0], getFlags("field")[1]);
        String[] statementInputs = StringHelper.substringsBetween(code, getFlags("statement")[0], getFlags("statement")[1]);
        String[] valueInputs = StringHelper.substringsBetween(code, getFlags("value")[0], getFlags("value")[1]);

        int nonDeletableBlocks = 0;
        for (String valueKey : block.getValueBlocks().keySet()) {
            for (ImplementedBlock valueBlock : block.getValueBlocks().get(valueKey)) {
                if (!valueBlock.isDeletable()) {
                    nonDeletableBlocks++;
                }
            }
        }

        if (valueInputs != null && valueInputs.length < block.getValueBlocks().size() && valueInputs.length + nonDeletableBlocks < block.getValueBlocks().size()) {
            return false;
        }
        if (statementInputs != null && statementInputs.length < block.getStatementBlocks().size()) {
            return false;
        }
        if (fieldInputs != null && fieldInputs.length < block.getFields().length) {
            return false;
        }
        return true;
    }

    public interface IFieldManipulator {
        String getFieldCode(Field field);
    }
    public interface IValueManipulator {
        String getValueCode(String valueKey, String value);
    }
    public interface IMarker {
        String getCode(String markerKey, ImplementedBlock block);
    }
}
