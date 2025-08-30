package com.meteordevelopments.duels.util.io;

import java.io.File;
import java.io.IOException;

public final class FileUtil {

    private FileUtil() {
    }

    public static boolean checkNonEmpty(final File file, final boolean create) throws IOException {
        if (!file.exists()) {
            if (create) {
                if (!file.createNewFile()) {
                    throw new IOException("Failed to create file: " + file.getAbsolutePath());
                }
            }
            return false;
        }

        return file.length() > 0;
    }
}
