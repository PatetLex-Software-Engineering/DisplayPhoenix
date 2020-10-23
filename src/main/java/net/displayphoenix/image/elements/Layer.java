package net.displayphoenix.image.elements;

public class Layer {

    private boolean hidden;
    private boolean lock;

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public void setLocked(boolean locked) {
        this.lock = locked;
    }

    public boolean isHidden() {
        return hidden;
    }

    public boolean isLocked() {
        return lock;
    }
}
