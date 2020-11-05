package net.displayphoenix.image.tools.impl;

import net.displayphoenix.image.CanvasPanel;
import net.displayphoenix.image.Pixel;
import net.displayphoenix.image.ToolPanel;
import net.displayphoenix.image.tools.Tool;
import net.displayphoenix.util.ImageHelper;

import javax.swing.*;

public class PencilTool extends Tool {
    @Override
    public ImageIcon getIcon() {
        return ImageHelper.getImage("image/pencil");
    }

    @Override
    public void onLeftClick(ToolPanel toolkit, CanvasPanel canvas, int x, int y) {
        Pixel pixel = new Pixel(toolkit.getColorWheel().getColor());
        canvas.setPixel(x, y, pixel);
    }

    @Override
    public void onRightClick(ToolPanel toolkit, CanvasPanel canvas, int x, int y) {

    }
}
