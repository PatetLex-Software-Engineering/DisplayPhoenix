package net.displayphoenix.image.components;

import net.displayphoenix.Application;
import net.displayphoenix.image.interfaces.ISettingComponent;
import net.displayphoenix.image.tools.IntegerSetting;
import net.displayphoenix.image.tools.Setting;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class IntegerSettingComponent extends JPanel implements ISettingComponent<Integer>, MouseListener, MouseMotionListener {

    private int pickerX;

    private IntegerSetting setting;

    private Point cachedPoint;

    public IntegerSettingComponent(IntegerSetting setting) {
        this.setting = setting;
        setForeground(Application.getTheme().getColorTheme().getSecondaryColor());
        setBackground(Application.getTheme().getColorTheme().getAccentColor());
        addMouseListener(this);
        addMouseMotionListener(this);
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        float rgbPickerWidth = getWidth() * 0.1F;
        if (this.pickerX == 0) {
            this.pickerX = Math.round((((float) this.setting.getDefaultValue() - (float) this.setting.getMinValue()) / ((float) this.setting.getMaxValue())) * ((float) getWidth() - rgbPickerWidth));
            this.pickerX += Math.round(rgbPickerWidth / 2F);
        }
        g.setColor(getBackground());
        g.fillRect(0, Math.round((getHeight() + 3F) / 2F), getWidth(), 3);
        int centerY = getHeight() / 2;
        float r = rgbPickerWidth / 2;
        for (int rgbPX = 0; rgbPX < rgbPickerWidth; rgbPX++) {
            for (int rgbPY = 0; rgbPY < rgbPickerWidth; rgbPY++) {
                float dx = rgbPX - r;
                float dy = rgbPY - r;
                if (dy < 0)
                    dy *= -1;
                if (dx < 0)
                    dx *= -1;
                float d = (float) Math.sqrt((dx * dx) + (dy * dy));
                if (d < r && d > r - 2.5F) {
                    g.setColor(getForeground());
                    g.fillRect(this.pickerX + rgbPX - Math.round(r), centerY + rgbPY - Math.round(r), 1, 1);
                }
            }
        }
        g.setColor(getForeground());
        g.drawString(String.valueOf(getValue()), Math.round(getWidth() / 2F), Math.round((getHeight() / 2F) + (getHeight() / 2F)));
    }

    @Override
    public Integer getValue() {
        float radius = (getWidth() * 0.1F) / 2F;
        float r = ((((float) this.pickerX - radius) / ((float) getWidth() - (radius * 2F))));
        return Math.round((r * ((float) this.setting.getMaxValue() - this.setting.getMinValue())) + this.setting.getMinValue());
    }

    @Override
    public Setting getSetting() {
        return this.setting;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        this.pickerX = e.getX();
        repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        this.cachedPoint = e.getPoint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        float dx = (float) (e.getX() - this.cachedPoint.getX());
        float pickerWidth = getWidth() * 0.1F;
        float radius = pickerWidth / 2F;
        if (this.pickerX + dx >= radius && this.pickerX + dx < this.getWidth() - radius + 1) {
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            this.pickerX += dx;
        }
        this.cachedPoint = e.getPoint();
        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}
