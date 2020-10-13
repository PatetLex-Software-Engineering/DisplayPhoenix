package net.displayphoenix.widgets;

import javax.swing.*;

/**
 * @author TBroski
 */
public class ProgressWidget extends JPanel {

    private float loadProgress;
    private float maxProgress;

    public ProgressWidget(float maxProgress) {
        this.maxProgress = maxProgress;
    }

    public void setLoadingProgress(float loadingProgress) {
        this.loadProgress = loadingProgress;
        repaint();
    }

    public float getMaxProgress() {
        return maxProgress;
    }

    public float getLoadingProgress() {
        return loadProgress;
    }
}
