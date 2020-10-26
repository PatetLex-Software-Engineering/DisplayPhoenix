package net.displayphoenix.image.interfaces;

import net.displayphoenix.image.elements.Layer;

import java.awt.*;

/**
 * @author TBroski
 */
public interface LayerListener {
    void onLayerRemoved(Layer layer);
    void onLayerAdded(Layer layer);
    void onLayerPainted(Layer layer, Graphics g);
}
