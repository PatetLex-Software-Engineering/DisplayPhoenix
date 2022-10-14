package com.patetlex.displayphoenix.maps.ui;

import com.patetlex.displayphoenix.blockly.event.IBlocklyListener;
import com.patetlex.displayphoenix.maps.Maps;
import com.patetlex.displayphoenix.maps.elements.Marker;
import com.patetlex.displayphoenix.maps.event.IMapListener;
import com.patetlex.displayphoenix.maps.event.MapEvent;
import com.patetlex.displayphoenix.maps.event.events.MarkerClickEvent;
import com.patetlex.displayphoenix.maps.gen.MapHtmlGenerator;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.handler.CefLoadHandlerAdapter;
import org.cef.js.JavaScriptGenerator;
import org.cef.ui.WebPanel;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MapPanel extends WebPanel implements MapHtmlGenerator, JavaScriptGenerator {

    private List<Runnable> runOnLoad = new ArrayList<>();
    private List<Runnable> runOnNextLoad = new ArrayList<>();
    private boolean isLoaded;

    private List<Marker> markers = new ArrayList<>();

    private List<IMapListener> eventListeners = new ArrayList<>();

    /**
     * MapPanel utilizes Google Maps
     *
     * @see Maps#loadApi(String) Api key should be loaded in this method.
     *
     * @throws NullPointerException if Api key is not found, exception is thrown
     */
    public MapPanel() {
        if (Maps.getApiKey() == null)
            throw new NullPointerException("Api key not-found/not-loaded-to-Maps#loadApi(String), visit https://developers.google.com/maps/documentation/javascript/get-api-key.");
        loadHtml(generateHtml());
        setMember("mappanel", this);
        addLoadHandler(new CefLoadHandlerAdapter() {
            @Override
            public void onLoadEnd(CefBrowser browser, CefFrame frame, int httpStatusCode) {
                super.onLoadEnd(browser, frame, httpStatusCode);
                MapPanel.this.isLoaded = true;

                String code = " function onMarkerClick(marker) {" +
                        "if (mappanel !== null && typeof mappanel !== \"undefined\" && marker != null)" +
                        "fire('marker-click', marker.position.lat, marker.position.lng);" +
                        "}" +
                        "map.addOnMarkerClickListener(onMarkerClick);";
                browser.executeJavaScript(generateMembers() + code, browser.getURL(), 1);
                System.out.println("ran");

                for (Runnable runnable : MapPanel.this.runOnLoad) {
                    runnable.run();
                }
                List<Runnable> runnablesToDestroy = new ArrayList<>();
                for (Runnable runnable : MapPanel.this.runOnNextLoad) {
                    runnable.run();
                    runnablesToDestroy.add(runnable);
                }
                for (Runnable runnable : runnablesToDestroy) {
                    MapPanel.this.runOnNextLoad.remove(runnable);
                }
            }
        });
    }

    public boolean isLoaded() {
        return this.isLoaded;
    }

    public void addMarker(double lat, double lng) {
        addMarker(lat, lng, new Consumer<Marker>() {
            @Override
            public void accept(Marker marker) {

            }
        });
    }

    public void addMarker(double lat, double lng, Consumer<Marker> marker) {
        if (this.isLoaded()) {
            System.out.println("added");
            executeScript("var marker = new tt.Marker().setLngLat({ lat: " + lat + ", lng: " + lng + " }).addTo(map);" +
                    "");
            Marker m = new Marker(lat, lng);
            this.markers.add(m);
            marker.accept(m);
        } else {
            queueOnNextLoad(() -> addMarker(lat, lng, marker));
        }
    }

    /**
     * Queue statement to run when Google Maps loads, continuous
     *
     * @param runnable Code to run
     */
    public void queueOnLoad(Runnable runnable) {
        this.runOnLoad.add(runnable);
    }

    /**
     * Queue statement to run when Google Maps loads next time, once
     *
     * @param runnable Code to run
     */
    public void queueOnNextLoad(Runnable runnable) {
        this.runOnNextLoad.add(runnable);
    }

    public void addMapEventListener(IMapListener listener) {
        this.eventListeners.add(listener);
    }

    /**
     * Fires an event, called from JavaScript
     */
    @SuppressWarnings("unused")
    public void fire(Object type, Object value1, Object value2) {
        MapEvent event = new MapEvent((String) type, this);
        System.out.println("Event - " + value1 + " - " + value2 + ": " + type);
        if (((String) type).startsWith("marker")) {
            for (Marker marker : this.markers) {
                if (marker.getLocation().getLatitude() == (double) value1 && marker.getLocation().getLongitude() == (double) value2) {
                    if (type.equals("marker-click")) {
                        event = new MarkerClickEvent((String) type, this, marker);
                    }
                    break;
                }
            }
        }
        for (IMapListener listener : this.eventListeners) {
            listener.onMapEvent(event);
        }
    }
}
