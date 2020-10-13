package net.displayphoenix.widgets.impl;

import net.displayphoenix.init.ColorInit;
import net.displayphoenix.widgets.ProgressWidget;

import java.awt.*;

/**
 * @author TBroski
 */
public class CircleProgressWidget extends ProgressWidget {

    private Color loadingColor;
    private Color emptyColor;
    private float radius;
    private float thickness;

    private int offsetX = 0;
    private int offsetY = 0;

    public CircleProgressWidget(float maxProgress, float radius, float thickness, Color loadingColor, Color emptyColor) {
        super(maxProgress);
        this.radius = radius;
        this.thickness = thickness;
        this.loadingColor = loadingColor;
        this.emptyColor = emptyColor;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        int centerX = (getWidth() / 2) - offsetX;
        int centerY = (getHeight() / 2) - offsetY;

        for (int pointX = 0; pointX <= this.getWidth(); pointX++) {
            for (int pointY = 0; pointY <= this.getHeight(); pointY++) {
                float dx = pointX - centerX;
                float dy = pointY - centerY;
                float d = (float) Math.sqrt((dx * dx) + (dy * dy));
                if (d < radius + thickness && d > radius - thickness) {
                    if (this.getLoadingProgress() > 0 && contains(this.getMaxProgress() / this.getLoadingProgress(), pointX, pointY)) { //max / curr // curr > 0 &&
                        g.setColor(loadingColor);
                    }
                    else {
                        g.setColor(emptyColor);
                    }
                }
                else {
                    g.setColor(ColorInit.TRANSPARENT);
                }

                g.fillRect(pointX, pointY, 1, 1);
            }
        }
    }

    public CircleProgressWidget offset(int x, int y) {
        this.offsetX = x;
        this.offsetY = y;
        return this;
    }

    private boolean contains(float ratio, double x, double y) {
        double ellw = getWidth();
        if (ellw <= 0.0) {
            return false;
        }
        double normx = (x - getX()) / ellw - 0.5;
        double ellh = getHeight();
        if (ellh <= 0.0) {
            return false;
        }
        double normy = (y - getY()) / ellh - 0.5;
        double distSq = (normx * normx + normy * normy);
        if (distSq >= 0.25) {
            return false;
        }
        double angExt = Math.abs(360 / ratio);
        if (angExt >= 360.0) {
            return true;
        }
        boolean inarc = containsAngle(ratio, -Math.toDegrees(Math.atan2(normy, normx)));
        return inarc;
    }

    private boolean containsAngle(float ratio, double angle) {
        double angExt = 360 / ratio;
        boolean backwards = (angExt < 0.0);
        if (backwards) {
            angExt = -angExt;
        }
        if (angExt >= 360.0) {
            return true;
        }
        angle = normalizeDegrees(angle) - normalizeDegrees(90);
        if (backwards) {
            angle = -angle;
        }
        if (angle < 0.0) {
            angle += 360.0;
        }


        return (angle >= 0.0) && (angle < angExt);
    }

    private static double normalizeDegrees(double angle) {
        if (angle > 180.0) {
            if (angle <= (180.0 + 360.0)) {
                angle = angle - 360.0;
            } else {
                angle = Math.IEEEremainder(angle, 360.0);
                if (angle == -180.0) {
                    angle = 180.0;
                }
            }
        } else if (angle <= -180.0) {
            if (angle > (-180.0 - 360.0)) {
                angle = angle + 360.0;
            } else {
                angle = Math.IEEEremainder(angle, 360.0);
                if (angle == -180.0) {
                    angle = 180.0;
                }
            }
        }
        return angle;
    }

    @Override
    public int getWidth() {
        return Math.round(this.radius + this.thickness) * 2;
    }

    @Override
    public int getHeight() {
        return Math.round(this.radius + this.thickness) * 2;
    }
}
