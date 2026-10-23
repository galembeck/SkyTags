package net.skyminigames.tags.tag;

import lombok.Value;
import org.bukkit.ChatColor;

import java.util.regex.Pattern;

/**
 * What one player should look like: their group's prefix / suffix and position in the tab list.
 */
@Value
public class PlayerTag {

    /** Minecraft 1.8 limit for team names, prefixes and suffixes. */
    public static final int MAX_LENGTH = 16;
    private static final int MAX_RANK = 999;
    private static final Pattern TEAM_NAME = Pattern.compile("^\\d{3}[A-Za-z0-9_]{0,10}[0-9a-f]{3}$");

    String group;
    int rank;
    String prefix;
    String suffix;

    /**
     * The scoreboard team for this tag. The tab list is sorted by team name, so it starts with the rank
     * (zero-padded: "100" before "700"). A short hash keeps groups with the same rank but different
     * prefixes apart. Always 16 characters or fewer.
     */
    public String teamName() {
        String rankPart = String.format("%03d", Math.max(0, Math.min(MAX_RANK, rank)));
        String hash = String.format("%03x", (group + '|' + prefix + '|' + suffix).hashCode() & 0xfff);
        String groupPart = group.replaceAll("[^A-Za-z0-9_]", "");
        int room = MAX_LENGTH - rankPart.length() - hash.length();
        if (groupPart.length() > room) {
            groupPart = groupPart.substring(0, room);
        }
        return rankPart + groupPart + hash;
    }

    /** Whether a team name has the format produced by {@link #teamName()}. */
    public static boolean isTeamName(String name) {
        return TEAM_NAME.matcher(name).matches();
    }

    /**
     * Cuts a coloured text to 16 characters without leaving a dangling colour symbol at the end.
     */
    public static String limit(String text) {
        if (text == null) {
            return "";
        }
        if (text.length() <= MAX_LENGTH) {
            return text;
        }
        String cut = text.substring(0, MAX_LENGTH);
        return cut.charAt(cut.length() - 1) == ChatColor.COLOR_CHAR ? cut.substring(0, cut.length() - 1) : cut;
    }
}
