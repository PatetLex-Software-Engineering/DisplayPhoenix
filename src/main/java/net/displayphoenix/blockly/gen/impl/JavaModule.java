package net.displayphoenix.blockly.gen.impl;

import net.displayphoenix.blockly.gen.Module;

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
