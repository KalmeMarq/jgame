package me.kalmemarq.jgame.logging;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class LogManager {
    protected static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
    protected static final StackWalker stackWalker = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
    protected static Logger.LogLevel logLevel = Logger.LogLevel.ERROR;
    protected static final List<StreamEntry> streams = new ArrayList<>();
    protected static boolean enableAnsi = true;

    public static Logger getLogger(String name) {
        return new SimpleLogger(name);
    }

    public static Logger getLogger(Class<?> clazz) {
        return new SimpleLogger(clazz.getSimpleName());
    }

    public static Logger getLogger() {
        return new SimpleLogger(LogManager.stackWalker.getCallerClass().getSimpleName());
    }

    public static void setDateFormat(String format) {
        LogManager.dateFormat = new SimpleDateFormat(format);
    }

    public static void addStream(PrintStream stream) {
        LogManager.streams.add(new StreamEntry(new PrintStream(stream), false));
    }

    public static void addFileStream(Path path) {
        try {
            LogManager.streams.add(new StreamEntry(new PrintStream(Files.newOutputStream(path)), true));
        } catch (IOException ignored) {
        }
    }

    public static void setLevel(Logger.LogLevel level) {
        LogManager.logLevel = level;
    }

    public static void enableAnsi() {
        LogManager.enableAnsi = true;
    }

    public static void disableAnsi() {
        LogManager.enableAnsi = false;
    }

    protected static boolean isLevelAllowed(Logger.LogLevel level) {
        return LogManager.logLevel.ordinal() >= level.ordinal();
    }

    protected static class Ansi {
        public static final String RESET = "\033[0m";
        public static final String RED = "\033[0;31m";
        public static final String GREEN = "\033[0;32m";
        public static final String YELLOW = "\033[0;33m";
        public static final String BLUE = "\033[0;34m";
        public static final String CYAN = "\033[0;36m";
    }

    protected record StreamEntry(PrintStream printStream, boolean isFile) {
    }
}
