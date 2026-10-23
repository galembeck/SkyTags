package net.skyminigames.tags.listener;

import lombok.RequiredArgsConstructor;
import net.skyminigames.tags.SkyTags;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
public final class PlayerListener implements Listener {

    private final SkyTags plugin;

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        plugin.getTagManager().update(player);
        // Everyone's "online" count changed
        if (plugin.getTagsConfig().isHeaderFooter()) {
            plugin.getTabListHeader().updateAll();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        plugin.getTagManager().remove(event.getPlayer());
        // The player still counts as online during this event: update a tick later
        if (plugin.getTagsConfig().isHeaderFooter()) {
            plugin.getServer().getScheduler().runTask(plugin, () -> plugin.getTabListHeader().updateAll());
        }
    }
}
