package net.skyminigames.tags.tag;

import lombok.experimental.UtilityClass;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

/**
 * Listens to PermissionsEx's own change events, so tags update the moment a role, prefix or rank changes
 * (pex commands, pex reload or another plugin). Hooked by class name: SkyTags still works without PermissionsEx.
 */
@UtilityClass
public class PermissionsExHook {

    private static final String[] EVENTS = {
            // A player's groups, or a group's prefix / suffix / rank changed
            "ru.tehkode.permissions.events.PermissionEntityEvent",
            // pex reload
            "ru.tehkode.permissions.events.PermissionSystemEvent",
    };

    /**
     * @param onChange called on every change; may be called off the main thread
     * @return whether at least one event could be hooked
     */
    public boolean register(Plugin plugin, Runnable onChange) {
        Listener listener = new Listener() {
        };
        int hooked = 0;
        for (String name : EVENTS) {
            try {
                // The plugin class loader also finds classes of other loaded plugins (PermissionsEx)
                Class<?> type = Class.forName(name, false, plugin.getClass().getClassLoader());
                if (!Event.class.isAssignableFrom(type)) {
                    continue;
                }
                plugin.getServer().getPluginManager().registerEvent(type.asSubclass(Event.class), listener,
                        EventPriority.MONITOR, (ignored, event) -> onChange.run(), plugin);
                hooked++;
            } catch (ClassNotFoundException | RuntimeException e) {
                plugin.getLogger().fine("PermissionsEx event " + name + " not available: " + e);
            }
        }
        return hooked > 0;
    }
}
