package net.displayphoenix.util;

import javafx.application.Platform;

public class ThreadHelper {

    private static boolean fxRunning = false;

    private static void setupFxThreadIfNotAlready() {
        if (fxRunning)
            return;

        try {
            Platform.setImplicitExit(false);
            Platform.runLater(() -> {
            });
        } catch (Exception e) {
            fxRunning = true;
        }
    }

    public static void runOnFxThread(Runnable runnable) {
        setupFxThreadIfNotAlready();

        if (Platform.isFxApplicationThread()) {
            runnable.run();
        }
        else {
            Platform.setImplicitExit(false);
            Platform.runLater(runnable);
        }
    }
}
