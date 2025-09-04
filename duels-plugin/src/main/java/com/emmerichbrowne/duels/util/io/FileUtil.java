package com.emmerichbrowne.duels.util.io;

import java.io.File;
import java.io.IOException;

public final class FileUtil {

    private FileUtil() {
    }

    public static boolean checkNonEmpty(final File file, final boolean create) throws IOException {
        if (!file.exists()) {
            if (create) {
                if (!file.createNewFile()) {
                    if (!file.exists() || !file.isFile()) {
                        throw new IOException("Failed to create file: " + file.getAbsolutePath() + 
                            (file.exists() ? " (path exists but is not a regular file)" : " (file does not exist)"));
                    }
                }
            }
            return false;
        }

        return file.length() > 0;
    }
}
