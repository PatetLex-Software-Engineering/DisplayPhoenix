package com.patetlex.displayphoenix.maps.elements;

import com.patetlex.displayphoenix.maps.Maps;

/**
 * @author TBroski
 */
public class Marker {

    private Maps.Location.GeoLocation location;

    public Marker(double lat, double lng) {
        this.location = new Maps.Location.GeoLocation(lat, lng);
    }

    public Maps.Location.GeoLocation getLocation() {
        return this.location;
    }
}
