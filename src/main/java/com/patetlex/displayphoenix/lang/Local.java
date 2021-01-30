package com.patetlex.displayphoenix.lang;

/**
 * @author TBroski
 */
public enum Local {
    EN_US("en_us"), FR_FR("fr_fr"), ES_ES("es_es"), RU("ru"), IT("it");

    private String tag;

    Local(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }
}
