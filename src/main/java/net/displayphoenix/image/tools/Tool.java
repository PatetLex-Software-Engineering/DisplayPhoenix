package net.displayphoenix.image.tools;

import net.displayphoenix.image.CanvasPanel;
import net.displayphoenix.image.ToolPanel;

import javax.swing.*;

public abstract class Tool {

    public abstract ImageIcon getIcon();

    public abstract void onLeftClick(ToolPanel toolkit, CanvasPanel canvas, int x, int y);
    public abstract void onRightClick(ToolPanel toolkit, CanvasPanel canvas, int x, int y);
}
