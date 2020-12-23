package net.displayphoenix.system.exe;

import net.displayphoenix.Application;
import net.displayphoenix.file.DetailedFile;
import net.displayphoenix.util.FileHelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Stack;

public class SystemProcessor {

    private SystemListener systemListener = new DefaultListener(true);
    private Stack<Process> cachedProcesses = new Stack<>();

    /**
     * Executes file
     *
     * @param file  File to execute
     * @return Exit value of command
     */
    public void executeFile(File file) {
        SystemListener prevListener = this.systemListener;
        setSystemListener(new DefaultListener(false));
        if (file.canExecute()) {
            runBatchCommand(file.getAbsolutePath(), false);
        }
        setSystemListener(prevListener);
    }

    /**
     * Executes batch command
     *
     * @param command  Command to execute; bat syntax
     * @return Exit value of command
     */
    public int runBatchCommand(String command, boolean follow) {
        DetailedFile file = FileHelper.storeTemporaryFile(command, Application.getTitle() + "_bat.bat");
        if (this.systemListener.showConsole()) {
            return run(new String[] {"cmd.exe", "/c", "start", "cmd", "/k", file.getFile().getPath()}, follow);
        }
        return run(file.getFile().getPath(), follow);
    }

    /**
     * Executes simple command
     *
     * @param command Command to execute
     * @return Exit value of command
     */
    public int run(String command, boolean follow) {
        return run(new String[] {command}, follow);
    }

    /**
     * Executes complex command
     *
     * @param commandArgs Command to execute
     * @return Exit value of command
     */
    public int run(String[] commandArgs, boolean follow) {
        try {
            Process process = Runtime.getRuntime().exec(commandArgs);

            this.cachedProcesses.push(process);

            if (follow) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    this.systemListener.onLineOutput(line);
                }
                int exitVal = process.waitFor();
                this.systemListener.onCommandFinished();
                return exitVal;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void clear() {
        for (Process process : this.cachedProcesses) {
            process.destroyForcibly();
        }
        this.cachedProcesses.pop();
    }

    public void setSystemListener(SystemListener systemListener) {
        this.systemListener = systemListener;
    }

    private static class DefaultListener implements SystemListener {

        private StringBuilder output = new StringBuilder();
        private boolean showConsole;

        public DefaultListener(boolean showConsole) {
            this.showConsole = showConsole;
        }

        @Override
        public void onLineOutput(String line) {
            this.output.append(line + "\n");
        }

        @Override
        public void onCommandFinished() {
            System.out.println(this.output.toString());
        }

        @Override
        public boolean showConsole() {
            return this.showConsole;
        }
    }
}
