package com.duncpro.bukkit.persistence;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class NamespacedKeys {
    public static String getClassName(String key) {
        StringBuilder caseSensitive = new StringBuilder();

        boolean nextCharIsUppercase = false;
        for (int i = 0; i < key.length(); i++) {
            if (key.charAt(i) == '/') {
                nextCharIsUppercase = true;
                continue;
            }

            if (nextCharIsUppercase) {
                caseSensitive.append(Character.toUpperCase(key.charAt(i)));
                nextCharIsUppercase = false;
            } else {
                caseSensitive.append(key.charAt(i));
            }
        }

        return caseSensitive.toString();
    }

    public static NamespacedKey get(Plugin plugin, Class<?> javaClass) {
        StringBuilder caseInsensitiveUnique = new StringBuilder();
        for (char c : javaClass.getName().toCharArray()) {
            if (Character.isUpperCase(c)) {
                caseInsensitiveUnique.append('/');
            }
            caseInsensitiveUnique.append(Character.toLowerCase(c));
        }
        return get(plugin, caseInsensitiveUnique.toString());
    }

    public static NamespacedKey get(Plugin plugin, String key) {
        final var created = new NamespacedKey(plugin, key);
        if (!Objects.equals(created.getKey(), key)) throw new IllegalArgumentException("Key is not in valid format.");
        return created;
    }

    public static NamespacedKey get(String pluginName, String key) {
        final var created = new NamespacedKey(pluginName, key);
        if (!Objects.equals(created.getKey(), key)) throw new IllegalArgumentException("Key is not in valid format.");
        return created;
    }

    public static String getNamespace(Plugin plugin) {
        requireNonNull(plugin);
        return get(plugin, "unused").getNamespace();
    }

    public static boolean matches(NamespacedKey key, Plugin plugin) {
        return Objects.equals(key.getNamespace(), getNamespace(plugin));
    }

    public static boolean startsWith(NamespacedKey key, NamespacedKey prefix) {
        if (!Objects.equals(key.getNamespace(), prefix.getNamespace())) return false;
        return key.getKey().startsWith(prefix.getKey());
    }

    public static boolean startsWith(NamespacedKey key, String prefix) {
        return startsWith(key, new NamespacedKey(key.getNamespace(), prefix));
    }

    public static boolean startsWith(NamespacedKey key, Plugin plugin, String prefix) {
        return startsWith(key, get(plugin, prefix));
    }

    public static NamespacedKey replaceFirst(NamespacedKey key, String prefix, String with) {
        final var prefixKey = get(key.getNamespace(), prefix);
        return replaceFirst(key, prefixKey, with);
    }

    public static NamespacedKey replaceFirst(NamespacedKey key, NamespacedKey prefix, String with) {
        if (!Objects.equals(key.getNamespace(), prefix.getNamespace())) throw new IllegalArgumentException();
        return get(key.getNamespace(), key.getKey().replaceFirst(prefix.getKey(), with));
    }
}
