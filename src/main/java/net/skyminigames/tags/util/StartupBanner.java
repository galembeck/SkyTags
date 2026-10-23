package net.skyminigames.tags.util;

import lombok.experimental.UtilityClass;
import net.skyminigames.tags.SkyTags;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Prints the SkyTags logo and startup information to the console.
 */
@UtilityClass
public class StartupBanner {

    private static final String[] LOGO = {
            "  _____ _           _______",
            " / ____| |         |__   __|",
            "| (___ | | __ _   _   | |   __ _  __ _  ___",
            " \\___ \\| |/ /| | | |  | |  / _` |/ _` |/ __|",
            " ____) |   < | |_| |  | | | (_| | (_| |\\__ \\",
            "|_____/|_|\\_\\ \\__, |  |_|  \\__,_|\\__, ||___/",
            "               __/ |              __/ |",
            "              |___/              |___/",
    };

    /** Column where "Sky" ends and "Tags" begins in {@link #LOGO}. */
    private static final int LOGO_SPLIT = 20;
    private static final String INDENT = "  ";

    public void printEnable(SkyTags plugin, long elapsedMillis) {
        ConsoleCommandSender console = Bukkit.getConsoleSender();

        console.sendMessage("");
        for (String line : LOGO) {
            console.sendMessage(INDENT + colorLogoLine(line));
        }
        console.sendMessage("");
        console.sendMessage(INDENT + ChatColor.AQUA + ChatColor.BOLD + plugin.getName()
                + ChatColor.GRAY + " v" + plugin.getDescription().getVersion()
                + ChatColor.DARK_GRAY + " by " + ChatColor.WHITE + String.join(", ", plugin.getDescription().getAuthors()));
        info(console, "Server", Bukkit.getName() + " " + Bukkit.getBukkitVersion().split("-")[0]
                + ChatColor.DARK_GRAY + " (Java " + System.getProperty("java.version") + ")");
        info(console, "Groups from", plugin.getChat().getName() + ChatColor.DARK_GRAY + " (via Vault)");
        info(console, "Role changes", plugin.isInstantUpdates()
                ? ChatColor.GREEN + "instant" + ChatColor.DARK_GRAY + " (PermissionsEx events)"
                : "every " + plugin.getTagsConfig().getUpdateIntervalSeconds() + "s");
        info(console, "Tab header/footer", plugin.getTagsConfig().isHeaderFooter() ? "on" : ChatColor.GRAY + "off");
        info(console, "Status", ChatColor.GREEN + "Enabled in " + elapsedMillis + " ms");
        console.sendMessage("");
    }

    public void printDisable(JavaPlugin plugin) {
        Bukkit.getConsoleSender().sendMessage(INDENT + ChatColor.AQUA + ChatColor.BOLD + plugin.getName()
                + ChatColor.GRAY + " v" + plugin.getDescription().getVersion()
                + ChatColor.DARK_GRAY + " » " + ChatColor.RED + "Disabled.");
    }

    private String colorLogoLine(String line) {
        if (line.length() <= LOGO_SPLIT) {
            return ChatColor.AQUA + line;
        }
        return ChatColor.AQUA + line.substring(0, LOGO_SPLIT) + ChatColor.WHITE + line.substring(LOGO_SPLIT);
    }

    private void info(ConsoleCommandSender console, String label, String value) {
        console.sendMessage(INDENT + ChatColor.DARK_GRAY + "» " + ChatColor.GRAY + label + ": " + ChatColor.WHITE + value);
    }
}
