package com.duncpro.bukkit.log;

import com.duncpro.bukkit.plugin.PostConstruct;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

import static java.util.Objects.requireNonNull;

public class AsyncFileHandler extends Handler {
    private final BlockingQueue<String> queue = new LinkedBlockingQueue<>();
    private final Thread writeThread;
    private final File logFile;
    private volatile boolean isEnabled;

    public AsyncFileHandler(File logFile) {
        this.logFile = requireNonNull(logFile);
        this.isEnabled = true;
        this.writeThread = new Thread(this::consumeQueue);
        this.writeThread.start();
    }

    private void append(String line) throws IOException {
        if (!line.endsWith("\n")) line += '\n';

        logFile.getParentFile().mkdirs();

        Files.writeString(logFile.toPath(), line, StandardOpenOption.APPEND, StandardOpenOption.CREATE);
    }

    private void consumeQueue() {
        try {
            while (isEnabled || !queue.isEmpty()) {
                try {
                    final var line = queue.take();
                    append(line);
                } catch (InterruptedException e) {
                    // We're done
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to append to log file.");
            e.printStackTrace();
        }
    }

    @Override
    public void publish(LogRecord record) {
        if (!isEnabled) throw new IllegalStateException();
        queue.offer(getFormatter().format(record));
    }

    @Override
    public void flush() {}

    @Override
    public void close() throws SecurityException {
        isEnabled = false;
        writeThread.interrupt();
    }

    @Override
    public final Formatter getFormatter() {
        return new SimpleFormatter();
    }

    @Override
    public final void setFormatter(Formatter formatter) {
        throw new UnsupportedOperationException();
    }
}
