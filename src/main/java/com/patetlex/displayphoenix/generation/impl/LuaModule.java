package com.patetlex.displayphoenix.generation.impl;

import com.patetlex.displayphoenix.blockly.elements.workspace.ImplementedBlock;
import com.patetlex.displayphoenix.generation.Module;

public class LuaModule extends Module {
    public LuaModule() {
        super("lua");
    }

    @Override
    public String getCodeFromBlock(ImplementedBlock block) {
        if (block.getBlock().getType().equalsIgnoreCase("controls_if")) {
            return super.getCodeFromBlock(block) + "\nend";
        }
        return super.getCodeFromBlock(block);
    }
}
