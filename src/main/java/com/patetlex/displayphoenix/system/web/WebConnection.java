package com.patetlex.displayphoenix.system.web;

import com.patetlex.displayphoenix.maps.Maps;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Scanner;

public class WebConnection {

    public static boolean isConnected() {
        return streamHtml("https://www.google.com/") != null;
    }

    public static String getServerIp() {
        return streamHtml("http://bot.whatismyipaddress.com/");
    }

    public static String getClientIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress().trim();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String streamHtml(String url) {
        try {
            return streamHtml(new URL(url));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String streamHtml(URL url) {
        try {
            Scanner scanner = new Scanner(url.openStream());
            StringBuffer buffer = new StringBuffer();
            while (scanner.hasNext()) {
                buffer.append(scanner.next() + " ");
            }
            return buffer.toString();
        } catch (IOException e) { }
        return null;
    }
}
