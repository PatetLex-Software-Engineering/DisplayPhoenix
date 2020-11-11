package net.displayphoenix.canvasly.elements;

public class Layer {

    private int index;
    private boolean hidden;
    private boolean lock;

    public Layer(int index) {
        this.index = index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public void setLocked(boolean locked) {
        this.lock = locked;
    }

    public int getIndex() {
        return index;
    }

    public boolean isHidden() {
        return hidden;
    }

    public boolean isLocked() {
        return lock;
    }
}
