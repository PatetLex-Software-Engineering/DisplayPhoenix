package net.displayphoenix.image.tools.impl;

import net.displayphoenix.file.DetailedFile;
import net.displayphoenix.file.FileDialog;
import net.displayphoenix.image.CanvasPanel;
import net.displayphoenix.image.ToolPanel;
import net.displayphoenix.image.elements.impl.ImageElement;
import net.displayphoenix.image.tools.Tool;
import net.displayphoenix.util.ImageHelper;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.IOException;

public class ImageTool extends Tool {
    @Override
    public ImageIcon getIcon() {
        return ImageHelper.getImage("image/image");
    }

    @Override
    public void onLeftClick(ToolPanel toolkit, CanvasPanel canvas, int x, int y) {
        DetailedFile image = FileDialog.openFile(null, "png");
        try {
            canvas.setElement(canvas.getSelectedLayer(), new ImageElement(new ImageIcon(ImageIO.read(image.getFile()))), x, y);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRightClick(ToolPanel toolkit, CanvasPanel canvas, int x, int y) {

    }
}
