package net.displayphoenix.image.elements;

public class StaticElement {

    private Element element;
    private int x;
    private int y;

    public StaticElement(Element element, int x, int y) {
        this.element = element;
        this.x = x;
        this.y = y;
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
}
