package net.displayphoenix.ui.constructor;

import java.util.ArrayList;
import java.util.List;

public class Construct {

    private List<ConstructBlock> blocks = new ArrayList<>();

    public void add(ConstructBlock block) {
        this.blocks.add(block);
    }

    public void add(List<ConstructBlock> blocks) {
        for (ConstructBlock block : blocks) {
            blocks.add(block);
        }
    }

    public void set(ConstructBlock... blocks) {
        List<ConstructBlock> converts = new ArrayList<>();
        for (ConstructBlock block : blocks) {
            converts.add(block);
        }
        this.blocks = converts;
    }

    public void set(List<ConstructBlock> blocks) {
        this.blocks = blocks;
    }
}
