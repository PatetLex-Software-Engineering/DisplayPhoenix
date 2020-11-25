package net.displayphoenix.canvasly.interfaces;

import net.displayphoenix.canvasly.elements.Layer;

import java.awt.*;
import java.util.Set;

/**
 * @author TBroski
 */
public interface LayerListener {
    void onLayerRemoved(Layer layer);
    void onLayerAdded(Layer layer);
    void onLayerPainted(Layer layer, Graphics g);
    void onLayerSet(Set<Layer> layers);
}
