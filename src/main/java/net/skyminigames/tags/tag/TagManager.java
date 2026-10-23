package net.skyminigames.tags.tag;

import net.milkbowl.vault.chat.Chat;
import net.skyminigames.tags.config.TagsConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Puts every player in a scoreboard team for their group. The team gives the coloured prefix above the
 * head and in the tab list, and its name sorts the tab list by rank.
 * <p>
 * Uses the main scoreboard, which every player sees unless another plugin gives them their own.
 */
public final class TagManager {

    private final Chat chat;
    private TagsConfig config;
    /** The tag currently applied to each online player. */
    private final Map<UUID, PlayerTag> applied = new HashMap<>();
    /** Teams created by SkyTags, so only those are ever removed. */
    private final Set<String> ownTeams = new HashSet<>();

    public TagManager(Chat chat, TagsConfig config) {
        this.chat = chat;
        this.config = config;
    }

    public void setConfig(TagsConfig config) {
        this.config = config;
    }

    /** Reads the player's group, prefix, suffix and rank from PermissionsEx (through Vault). */
    public PlayerTag resolve(Player player) {
        String world = player.getWorld().getName();
        String group = chat.getPrimaryGroup(player);
        if (group == null || group.isEmpty()) {
            group = "default";
        }

        String prefix = config.getPrefixOverride(group);
        if (prefix == null) {
            prefix = colored(chat.getPlayerPrefix(player));
        }
        String suffix = config.getSuffixOverride(group);
        if (suffix == null) {
            suffix = colored(chat.getPlayerSuffix(player));
        }
        int rank = chat.getGroupInfoInteger(world, group, "rank", config.getDefaultRank());

        return new PlayerTag(group, rank, PlayerTag.limit(prefix), PlayerTag.limit(suffix));
    }

    /** Applies the player's current tag; does nothing if it hasn't changed. */
    public void update(Player player) {
        PlayerTag tag = resolve(player);
        PlayerTag current = applied.get(player.getUniqueId());
        if (tag.equals(current)) {
            return;
        }

        Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
        removeFromTeam(board, player.getName(), current);

        String name = tag.teamName();
        Team team = board.getTeam(name);
        if (team == null) {
            team = board.registerNewTeam(name);
            ownTeams.add(name);
        }
        if (!tag.getPrefix().equals(team.getPrefix())) {
            team.setPrefix(tag.getPrefix());
        }
        if (!tag.getSuffix().equals(team.getSuffix())) {
            team.setSuffix(tag.getSuffix());
        }
        team.addEntry(player.getName());
        applied.put(player.getUniqueId(), tag);
    }

    public void updateAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            update(player);
        }
    }

    public void remove(Player player) {
        PlayerTag current = applied.remove(player.getUniqueId());
        removeFromTeam(Bukkit.getScoreboardManager().getMainScoreboard(), player.getName(), current);
    }

    /** Forgets what was applied, so the next {@link #updateAll()} re-applies every tag (after /skytags reload). */
    public void reset() {
        applied.clear();
    }

    /**
     * The main scoreboard is saved with the world, so teams from a crash would survive a restart.
     * Removes leftover teams in SkyTags' own name format (on enable).
     *
     * @return how many were removed
     */
    public int removeLeftoverTeams() {
        int removed = 0;
        for (Team team : new HashSet<>(Bukkit.getScoreboardManager().getMainScoreboard().getTeams())) {
            if (PlayerTag.isTeamName(team.getName())) {
                team.unregister();
                removed++;
            }
        }
        return removed;
    }

    /** Removes every team SkyTags created (on disable). */
    public void clear() {
        Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
        for (String name : ownTeams) {
            Team team = board.getTeam(name);
            if (team != null) {
                team.unregister();
            }
        }
        ownTeams.clear();
        applied.clear();
    }

    private void removeFromTeam(Scoreboard board, String entry, PlayerTag tag) {
        if (tag == null) {
            return;
        }
        Team team = board.getTeam(tag.teamName());
        if (team == null) {
            return;
        }
        team.removeEntry(entry);
        if (team.getSize() == 0 && ownTeams.remove(team.getName())) {
            team.unregister();
        }
    }

    /**
     * Prefixes / suffixes can contain line breaks meant for the chat (Legendchat puts the staff suffix
     * around messages); a name tag must stay on one line.
     */
    static String colored(String text) {
        if (text == null) {
            return "";
        }
        return org.bukkit.ChatColor.translateAlternateColorCodes('&', text.replace("\r", "").replace("\n", ""));
    }
}
