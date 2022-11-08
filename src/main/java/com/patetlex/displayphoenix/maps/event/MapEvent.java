package com.patetlex.displayphoenix.maps.event;

import com.patetlex.displayphoenix.maps.ui.MapPanel;

public class MapEvent {

    private String type;
    private MapPanel map;

    public MapEvent(String type, MapPanel map) {
        this.type = type;
        this.map = map;
    }

    public String getType() {
        return type;
    }

    public MapPanel getMap() {
        return map;
    }
}
