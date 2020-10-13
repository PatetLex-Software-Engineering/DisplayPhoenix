package net.displayphoenix.lang;

/**
 * @author TBroski
 */
public enum Local {
    EN_US("en_us"), FR_FR("fr_fr");

    private String tag;

    Local(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }
}
