package com.patetlex.displayphoenix.maps;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.patetlex.displayphoenix.system.web.WebConnection;

public class Maps {

    private static final Gson gson = new Gson();

    private static String apiKey;

    public static void loadApi(String key) {
        apiKey = key;
    }

    public static String getApiKey() {
        return apiKey;
    }

    public static class Location {

        private static Location lastLocationRetrieved;

        protected JsonObject obj;
        protected String ip;

        public Location(JsonObject obj, String ip) {
            this.obj = obj;
            this.ip = ip;
        }

        public String getCity() {
            return this.obj.get("city").getAsString();
        }

        public String getRegion() {
            return this.obj.get("region").getAsString();
        }

        public String getPostalCode() {
            return this.obj.get("postal").getAsString();
        }

        public String getTimeZone() {
            return this.obj.get("timezone").getAsString();
        }

        public String getCountry() {
            return this.obj.get("country").getAsString();
        }

        public GeoLocation getLocation() {
            String loc = this.obj.get("loc").getAsString();
            String[] coords = loc.split(",");
            double lat = Double.parseDouble(coords[0]);
            double lng = Double.parseDouble(coords[1]);
            return new GeoLocation(lat, lng);
        }

        public static Location getPublicLocation() {
            if (WebConnection.isConnected()) {
                String ip = WebConnection.getServerIp();
                if (lastLocationRetrieved != null && ip.equalsIgnoreCase(lastLocationRetrieved.ip))
                    return lastLocationRetrieved;
                String locationJson = WebConnection.streamHtml("https://ipinfo.io/" + ip);
                Location loc = new Location(gson.fromJson(locationJson, JsonObject.class), ip);
                lastLocationRetrieved = loc;
                return loc;
            }
            return null;
        }

        public static class GeoLocation {

            private double lat;
            private double lng;

            public GeoLocation(double lat, double lng) {
                this.lat = lat;
                this.lng = lng;
            }

            public double getLatitude() {
                return this.lat;
            }

            public double getLongitude() {
                return this.lng;
            }

            @Override
            public boolean equals(Object obj) {
                if (obj instanceof GeoLocation) {
                    GeoLocation loc = (GeoLocation) obj;
                    return loc.lng == this.lng && loc.lat == this.lat;
                }
                return false;
            }
        }
    }
}
