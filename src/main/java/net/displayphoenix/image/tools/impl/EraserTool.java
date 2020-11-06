package net.displayphoenix.image.tools.impl;

import net.displayphoenix.image.CanvasPanel;
import net.displayphoenix.image.ToolPanel;
import net.displayphoenix.image.tools.Tool;
import net.displayphoenix.util.ImageHelper;

import javax.swing.*;

public class EraserTool extends Tool {
    @Override
    public ImageIcon getIcon() {
        return ImageHelper.getImage("image/eraser");
    }

    @Override
    public void onLeftClick(ToolPanel toolkit, CanvasPanel canvas, int x, int y) {
        canvas.setPixel(x, y, null);
    }

    @Override
    public void onRightClick(ToolPanel toolkit, CanvasPanel canvas, int x, int y) {

    }
}
