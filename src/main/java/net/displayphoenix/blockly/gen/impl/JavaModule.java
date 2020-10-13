package net.displayphoenix.blockly.gen.impl;

import net.displayphoenix.blockly.gen.Module;

/**
 * @author TBroski
 */
public class JavaModule extends Module {

    public JavaModule() {
        super("java");
    }

    @Override
    public String addSyntax(String input) {
        input += ";";
        return input;
    }
}
