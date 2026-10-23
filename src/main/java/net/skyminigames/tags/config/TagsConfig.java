package net.skyminigames.tags.config;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Typed, read-only view of config.yml.
 */
@Getter
public final class TagsConfig {

    /** Prefix / suffix overrides per group (lower-case key), already coloured. {@code null} = not overridden. */
    private final Map<String, String[]> groupOverrides;
    private final int defaultRank;
    private final int updateIntervalSeconds;
    private final boolean headerFooter;

    public TagsConfig(FileConfiguration config) {
        Map<String, String[]> overrides = new HashMap<>();
        ConfigurationSection groups = config.getConfigurationSection("groups");
        if (groups != null) {
            for (String group : groups.getKeys(false)) {
                overrides.put(group.toLowerCase(Locale.ROOT), new String[]{
                        color(groups.getString(group + ".prefix")),
                        color(groups.getString(group + ".suffix"))});
            }
        }
        groupOverrides = Collections.unmodifiableMap(overrides);
        defaultRank = config.getInt("default-rank", 999);
        updateIntervalSeconds = Math.max(1, config.getInt("update-interval-seconds", 5));
        headerFooter = config.getBoolean("tablist.header-footer", true);
    }

    public String getPrefixOverride(String group) {
        String[] override = groupOverrides.get(group.toLowerCase(Locale.ROOT));
        return override == null ? null : override[0];
    }

    public String getSuffixOverride(String group) {
        String[] override = groupOverrides.get(group.toLowerCase(Locale.ROOT));
        return override == null ? null : override[1];
    }

    private static String color(String text) {
        return text == null ? null : ChatColor.translateAlternateColorCodes('&', text);
    }
}
