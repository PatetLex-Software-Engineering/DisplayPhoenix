package com.patetlex.displayphoenix.maps.gen;

import com.patetlex.displayphoenix.maps.Maps;
import com.patetlex.displayphoenix.maps.js.TomTomJS;

public interface MapHtmlGenerator {
    default String generateHtml() {
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "  <head>\n" +
                "    <title>Map</title>\n" +
/*                "    <script>" + TomTomJS.getTomTomCompressedContent() + "</script>\n" +
                "    <style type=\"text/css\">" + TomTomJS.getTomTomStylingContent() + "</style>\n" +*/
                "    <link rel='stylesheet' type='text/css' href='https://api.tomtom.com/maps-sdk-for-web/cdn/6.x/6.13.0/maps/maps.css'>\n" +
                "    <script src=\"https://api.tomtom.com/maps-sdk-for-web/cdn/6.x/6.13.0/maps/maps-web.min.js\"></script>" +
                "<style>\n" +
                "  body, html { margin: 0; padding: 0; }\n" +
                "  #map { width: 100vw; height: 100vh; }\n" +
                "</style>" +
                "  </head>\n" +
                "  <body>\n" +
                "    <div id=\"map\"></div>\n" +
                "    <script>\n" +
                "var map = tt.map({ key: '" + Maps.getApiKey() + "', container: 'map', center: { lat: " + Maps.Location.getPublicLocation().getLocation().getLatitude() + ", lng: " + Maps.Location.getPublicLocation().getLocation().getLongitude() + " }, zoom: 8 });"  +
                "" +
                "" +
                "" +
                "" +
                "" +
                "" +
                "\n" +
                "    </script>\n" +
                "  </body>\n" +
                "</html>";
    }
}
