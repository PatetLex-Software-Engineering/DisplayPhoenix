package com.patetlex.displayphoenix.util;

import org.cef.handler.CefLoadHandler;
import org.cef.ui.WebPanel;

public class WebHelper {
    public static WebPanel createWithLoadHandler(CefLoadHandler loadHandler) {
        return createWithLoadHandler("https://www.google.com/", loadHandler);
    }

    public static WebPanel createWithLoadHandler(String url, CefLoadHandler loadHandler) {
        return new WebPanel(url) {
            public WebPanel add(CefLoadHandler l) {
                addLoadHandler(l);
                return this;
            }
        }.add(loadHandler);
    }
}
