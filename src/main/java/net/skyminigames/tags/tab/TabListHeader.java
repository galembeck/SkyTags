package net.skyminigames.tags.tab;

import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.skyminigames.tags.message.Messages;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * The header and footer of the tab list (texts in messages.yml).
 */
@RequiredArgsConstructor
public final class TabListHeader {

    private final Messages messages;

    public void update(Player player) {
        int online = Bukkit.getOnlinePlayers().size();
        int max = Bukkit.getMaxPlayers();
        BaseComponent[] header = text(String.join("\n", messages.lines("tablist.header", "online", online, "max", max, "player", player.getName())));
        BaseComponent[] footer = text(String.join("\n", messages.lines("tablist.footer", "online", online, "max", max, "player", player.getName())));
        player.setPlayerListHeaderFooter(header, footer);
    }

    public void updateAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            update(player);
        }
    }

    public void clear(Player player) {
        player.setPlayerListHeaderFooter(text(""), text(""));
    }

    private static BaseComponent[] text(String legacy) {
        return TextComponent.fromLegacyText(legacy);
    }
}
