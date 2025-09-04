package com.emmerichbrowne.duels.util.reflect;

import com.emmerichbrowne.duels.util.Log;
import org.bukkit.Bukkit;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class ReflectionUtil {

    private static final String PACKAGE_VERSION;

    static {
        final String packageName = Bukkit.getServer().getClass().getPackage().getName();
        PACKAGE_VERSION = packageName.substring(packageName.lastIndexOf('.') + 1);
    }


    public static Class<?> getCBClass(final String path, final boolean logError) {
        try {
            return Class.forName("org.bukkit.craftbukkit." + PACKAGE_VERSION + "." + path);
        } catch (ClassNotFoundException ignored) {
        }

        try {
            return Class.forName("org.bukkit.craftbukkit." + path);
        } catch (ClassNotFoundException ex) {
            if (logError) {
                Log.error("Failed to find CraftBukkit class: " + path + " in both versioned and unversioned paths", ex);
            }
            return null;
        }
    }

    public static Class<?> getCBClass(final String path) {
        return getCBClass(path, true);
    }

    private static Method findDeclaredMethod(final Class<?> clazz, final String name, final Class<?>... parameters) throws NoSuchMethodException {
        final Method method = clazz.getDeclaredMethod(name, parameters);
        method.setAccessible(true);
        return method;
    }

    public static Method getDeclaredMethodUnsafe(final Class<?> clazz, final String name, final Class<?>... parameters) {
        try {
            return findDeclaredMethod(clazz, name, parameters);
        } catch (NoSuchMethodException ex) {
            return null;
        }
    }

    public static Field getDeclaredField(final Class<?> clazz, final String name) {
        try {
            final Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException ex) {
            Log.error(ex.getMessage(), ex);
            return null;
        }
    }

    private ReflectionUtil() {}
}