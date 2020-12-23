package net.displayphoenix.system.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WebConnection {

    public static boolean isConnected() {
        return getHtmlForPage("https://www.google.com/") != null;
    }

    public static String getHtmlForPage(String url) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(4000);
            connection.setInstanceFollowRedirects(true);
            connection.connect();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                StringBuilder output = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String out;
                while ((out = reader.readLine()) != null) {
                    output.append(out + "\n");
                }
                reader.close();
                return output.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
