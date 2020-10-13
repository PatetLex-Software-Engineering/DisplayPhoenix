package net.displayphoenix.enums;

/**
 * @author TBroski
 */
public enum WidgetStyle {

    POPPING("popping"), BLUNT("blunt");

    private String name;

    WidgetStyle(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
