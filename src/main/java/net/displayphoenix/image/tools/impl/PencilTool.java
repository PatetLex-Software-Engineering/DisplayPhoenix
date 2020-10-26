package net.displayphoenix.image.tools.impl;

import net.displayphoenix.image.CanvasPanel;
import net.displayphoenix.image.ToolPanel;
import net.displayphoenix.image.elements.impl.PixelElement;
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
        PixelElement pixel = new PixelElement();
        pixel.setColor(toolkit.getColorWheel().getColor());
        pixel.setOffsetX(x + 1);
        pixel.setOffsetY(y);
        canvas.addElement(pixel);
        canvas.setSelectedElement(null);
    }

    @Override
    public void onRightClick(ToolPanel toolkit, CanvasPanel canvas, int x, int y) {
        PixelElement pixel1 = new PixelElement();
        PixelElement pixel2 = new PixelElement();
        PixelElement pixel3 = new PixelElement();
        PixelElement pixel4 = new PixelElement();
        pixel1.setColor(toolkit.getColorWheel().getColor());
        pixel2.setColor(toolkit.getColorWheel().getColor());
        pixel3.setColor(toolkit.getColorWheel().getColor());
        pixel4.setColor(toolkit.getColorWheel().getColor());
        pixel1.setOffsetX(x + 1);
        pixel2.setOffsetX(x);
        pixel3.setOffsetX(x + 1);
        pixel4.setOffsetX(x);
        pixel1.setOffsetY(y);
        pixel2.setOffsetY(y);
        pixel3.setOffsetY(y + 1);
        pixel4.setOffsetY(y + 1);
        canvas.addElement(pixel1);
        canvas.addElement(pixel2);
        canvas.addElement(pixel3);
        canvas.addElement(pixel4);
        canvas.setSelectedElement(null);
    }
}
