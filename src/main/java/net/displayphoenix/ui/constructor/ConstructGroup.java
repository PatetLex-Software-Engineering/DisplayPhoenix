package net.displayphoenix.ui.constructor;

import java.awt.*;

public class ConstructGroup extends ConstructElement{

    private ConstructCategory[] categories;

    public ConstructGroup(String name, Color color, ConstructCategory... categories) {
        super(name, color);
        this.categories = categories;
    }

    public ConstructCategory[] getCategories() {
        return categories;
    }
}
