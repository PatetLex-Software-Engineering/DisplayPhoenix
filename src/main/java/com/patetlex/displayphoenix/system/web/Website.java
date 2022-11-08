package com.patetlex.displayphoenix.system.web;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author TBroski
 */
public class Website {

    private String url;

    public Website(String url) {
        this.url = url;
    }

    public String getLink() {
        return url;
    }

    public void open() {
        try {
            Desktop.getDesktop().browse(new URI(this.url));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String html() {
        return WebConnection.streamHtml(this.url);
    }
}
