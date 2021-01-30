package com.patetlex.displayphoenix.screen.impl;

import com.patetlex.displayphoenix.init.ColorInit;
import com.patetlex.displayphoenix.screen.SplashScreen;
import com.patetlex.displayphoenix.widgets.impl.BarProgressWidget;

import java.awt.*;

/**
 * @author TBroski
 */
public class RectangleLoadingSplashScreen extends SplashScreen {

    public RectangleLoadingSplashScreen(Image panelImage, int width, int height, Color loadingColor, Color emptyColor, int barWidth, int barHeight, int barY) {
        super(new BarProgressWidget(100, loadingColor, emptyColor), panelImage, width, height);
        this.progressWidget.setBounds((width / 2) - (barWidth / 2), barY, barWidth, barHeight); //width / 2
        this.progressWidget.setBackground(ColorInit.TRANSPARENT);

        //float ratio = this.progressWidget.getLoadingProgress() / this.progressWidget.getMaxProgress();
    }
}
