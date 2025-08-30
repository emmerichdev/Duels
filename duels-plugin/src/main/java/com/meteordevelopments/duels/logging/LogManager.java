package com.meteordevelopments.duels.logging;

import com.meteordevelopments.duels.DuelsPlugin;
import com.meteordevelopments.duels.util.DateUtil;
import com.meteordevelopments.duels.util.Log.LogSource;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.logging.*;

public class LogManager implements LogSource {

    @Getter
    private final Logger logger = Logger.getAnonymousLogger();
    private final FileHandler handler;

    public LogManager(final DuelsPlugin plugin) throws IOException {
        final File folder = getFile(plugin);

        logger.setLevel(Level.ALL);
        logger.setUseParentHandlers(false);

        final File file = new File(folder, DateUtil.formatDate(new Date()) + ".log");

        if (!file.exists()) {
            if (!file.createNewFile()) {
                throw new IOException("Failed to create log file: " + file.getAbsolutePath());
            }
        }

        handler = new FileHandler(file.getCanonicalPath(), true);
        handler.setLevel(Level.ALL);
        handler.setFormatter(new Formatter() {
            @Override
            public String format(final LogRecord record) {
                String thrown = "";

                if (record.getThrown() != null) {
                    final StringWriter stringWriter = new StringWriter();
                    final PrintWriter printWriter = new PrintWriter(stringWriter);
                    record.getThrown().printStackTrace(printWriter);
                    printWriter.close();
                    thrown = stringWriter.toString();
                }

                return "[" + DateUtil.formatDatetime(record.getMillis()) + "] [" + record.getLevel().getName() + "] " + record.getMessage() + '\n' + thrown;
            }
        });
        logger.addHandler(handler);
    }

    private static @NotNull File getFile(DuelsPlugin plugin) throws IOException {
        final File pluginFolder = plugin.getDataFolder();

        if (!pluginFolder.exists()) {
            if (!pluginFolder.mkdir()) {
                throw new IOException("Failed to create plugin data folder: " + pluginFolder.getAbsolutePath());
            }
        }

        final File folder = new File(pluginFolder, "logs");

        if (!folder.exists()) {
            if (!folder.mkdir()) {
                throw new IOException("Failed to create logs folder: " + folder.getAbsolutePath());
            }
        }
        return folder;
    }

    public void handleDisable() {
        handler.close();
        logger.removeHandler(handler);
    }

    public void debug(final String s) {
        log(Level.INFO, "[DEBUG] " + s);
    }

    @Override
    public void log(final Level level, final String s) {
        log(level, s, null);
    }

    @Override
    public void log(final Level level, final String s, final Throwable thrown) {
        if (handler == null) {
            return;
        }

        if (thrown != null) {
            getLogger().log(level, s, thrown);
        } else {
            getLogger().log(level, s);
        }
    }
}
