package com.patetlex.displayphoenix.widgets.impl;

import com.patetlex.displayphoenix.widgets.ProgressWidget;

import java.awt.*;

/**
 * @author TBroski
 */
public class BarProgressWidget extends ProgressWidget {

    private Color loadingColor;
    private Color emptyColor;

    public BarProgressWidget(float maxProgress, Color loadingColor, Color emptyColor) {
        super(maxProgress);
        this.loadingColor = loadingColor;
        this.emptyColor = emptyColor;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        g.setColor(emptyColor);
        g.fillRect(0,0, this.getWidth(), this.getHeight());

        g.setColor(loadingColor);
        float ratio = this.getLoadingProgress() / this.getMaxProgress();

        g.fillRect(0,0, Math.round(this.getWidth() * ratio), this.getHeight());
    }
}
