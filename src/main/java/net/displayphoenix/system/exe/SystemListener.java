package net.displayphoenix.system.exe;

public interface SystemListener {
    void onLineOutput(String line);
    void onCommandFinished();
    boolean showConsole();
}
