package net.displayphoenix.canvasly.elements;

public class Layer {

    private int index;
    private boolean hidden;

    public Layer(int index) {
        this.index = index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public int getIndex() {
        return index;
    }

    public boolean isHidden() {
        return hidden;
    }

}
