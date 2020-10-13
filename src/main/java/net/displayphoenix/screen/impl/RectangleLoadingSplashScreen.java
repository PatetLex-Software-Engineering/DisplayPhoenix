package net.displayphoenix.screen.impl;

import net.displayphoenix.init.ColorInit;
import net.displayphoenix.screen.SplashScreen;
import net.displayphoenix.widgets.impl.BarProgressWidget;

import java.awt.*;

public class RectangleLoadingSplashScreen extends SplashScreen {


    public RectangleLoadingSplashScreen(Image panelImage, int width, int height, Color loadingColor, Color emptyColor, int barWidth, int barHeight, int barY) {
        super(new BarProgressWidget(100, loadingColor, emptyColor), panelImage, width, height);
        this.progressWidget.setBounds((width / 2) - (barWidth / 2), barY, barWidth, barHeight); //width / 2
        this.progressWidget.setBackground(ColorInit.TRANSPARENT);

        float ratio = this.progressWidget.getLoadingProgress() / this.progressWidget.getMaxProgress();
    }
}
