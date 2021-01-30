package com.patetlex.displayphoenix.generation.impl;

import com.patetlex.displayphoenix.generation.Module;

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
