package com.patetlex.displayphoenix.maps.event.events;

import com.patetlex.displayphoenix.maps.elements.Marker;
import com.patetlex.displayphoenix.maps.event.MarkerEvent;
import com.patetlex.displayphoenix.maps.ui.MapPanel;

public class MarkerClickEvent extends MarkerEvent {
    public MarkerClickEvent(String type, MapPanel map, Marker marker) {
        super(type, map, marker);
    }
}
