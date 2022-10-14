package com.patetlex.displayphoenix.maps.event;

import com.patetlex.displayphoenix.maps.elements.Marker;
import com.patetlex.displayphoenix.maps.ui.MapPanel;

public class MarkerEvent extends MapEvent {

    private Marker marker;

    public MarkerEvent(String type, MapPanel map, Marker marker) {
        super(type, map);
        this.marker = marker;
    }
}
