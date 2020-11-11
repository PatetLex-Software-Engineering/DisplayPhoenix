package net.displayphoenix.canvasly.elements;

import java.awt.event.MouseListener;

public class StaticElement {

    private Element element;
    private int x;
    private int y;
    private Properties properties;

    public StaticElement(Element element, int x, int y, Properties properties) {
        this.element = element;
        this.x = x;
        this.y = y;
        this.properties = properties;
    }

    public Element getElement() {
        return element;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Properties getProperties() {
        return properties;
    }

    public static class Properties {

        private boolean shouldParse;
        private boolean isOverlay;
        private MouseListener mouseListener;

        public Properties setParse() {
            this.shouldParse = true;
            return this;
        }

        public Properties setOverlay() {
            this.isOverlay = true;
            return this;
        }

        public Properties setMouseListener(MouseListener mouseListener) {
            this.mouseListener = mouseListener;
            return this;
        }

        public boolean shouldParse() {
            return shouldParse;
        }

        public boolean isOverlay() {
            return isOverlay;
        }

        public MouseListener getMouseListener() {
            return mouseListener;
        }
    }
}
