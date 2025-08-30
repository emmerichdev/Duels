package com.meteordevelopments.duels.util.inventory;

import com.meteordevelopments.duels.util.Log;
import org.bukkit.inventory.ItemStack;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import org.bukkit.util.io.BukkitObjectInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public final class ItemUtil {

    private ItemUtil() {
    }

    public static ItemStack itemFrom64(final String data) {
        try {
            final ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            try (final BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream)) {
                return (ItemStack) dataInput.readObject();
            }
        } catch (ClassNotFoundException | IOException ex) {
            Log.error("Failed to deserialize ItemStack from Base64 data", ex);
            return null;
        }
    }
}
