package net.skyminigames.tags;

import lombok.Getter;
import net.milkbowl.vault.chat.Chat;
import net.skyminigames.tags.command.SkyTagsCommand;
import net.skyminigames.tags.config.TagsConfig;
import net.skyminigames.tags.listener.PlayerListener;
import net.skyminigames.tags.message.Messages;
import net.skyminigames.tags.tab.TabListHeader;
import net.skyminigames.tags.tag.PermissionsExHook;
import net.skyminigames.tags.tag.TagManager;
import net.skyminigames.tags.util.StartupBanner;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.atomic.AtomicBoolean;

@Getter
public final class SkyTags extends JavaPlugin {

    private TagsConfig tagsConfig;
    private Messages messages;
    private Chat chat;
    private TagManager tagManager;
    private TabListHeader tabListHeader;
    private BukkitTask refreshTask;
    /** Whether PermissionsEx's change events are hooked (tags update instantly). */
    private boolean instantUpdates;
    private final AtomicBoolean refreshQueued = new AtomicBoolean();

    @Override
    public void onEnable() {
        long start = System.currentTimeMillis();

        saveDefaultConfig();
        tagsConfig = new TagsConfig(getConfig());
        messages = new Messages(this);
        messages.reload();

        RegisteredServiceProvider<Chat> provider = getServer().getServicesManager().getRegistration(Chat.class);
        if (provider == null) {
            getLogger().severe("Vault has no chat/prefix provider (is PermissionsEx installed?). SkyTags is disabled.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        chat = provider.getProvider();

        tagManager = new TagManager(chat, tagsConfig);
        int leftovers = tagManager.removeLeftoverTeams();
        if (leftovers > 0) {
            getLogger().info("Removed " + leftovers + " leftover tag team(s) from a previous run.");
        }
        tabListHeader = new TabListHeader(messages);

        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getCommand("skytags").setExecutor(new SkyTagsCommand(this));
        instantUpdates = PermissionsExHook.register(this, this::scheduleRefresh);

        startRefreshTask();
        refreshEveryone();

        StartupBanner.printEnable(this, System.currentTimeMillis() - start);
    }

    @Override
    public void onDisable() {
        if (refreshTask != null) {
            refreshTask.cancel();
        }
        if (tagManager != null) {
            tagManager.clear();
        }
        StartupBanner.printDisable(this);
    }

    /** /skytags reload: re-reads config.yml and messages.yml and re-applies everything. */
    public void reload() {
        reloadConfig();
        tagsConfig = new TagsConfig(getConfig());
        messages.reload();
        tagManager.setConfig(tagsConfig);
        tagManager.reset();
        startRefreshTask();
        refreshEveryone();
        if (!tagsConfig.isHeaderFooter()) {
            for (Player player : getServer().getOnlinePlayers()) {
                tabListHeader.clear(player);
            }
        }
    }

    /** Fallback re-check (e.g. permissions.yml edited by hand without pex reload). */
    private void startRefreshTask() {
        if (refreshTask != null) {
            refreshTask.cancel();
        }
        long period = tagsConfig.getUpdateIntervalSeconds() * 20L;
        refreshTask = getServer().getScheduler().runTaskTimer(this, this::refreshEveryone, period, period);
    }

    /**
     * Refreshes on the next tick (thread-safe). Many changes at once (e.g. pex reload) give a single refresh.
     */
    public void scheduleRefresh() {
        if (isEnabled() && refreshQueued.compareAndSet(false, true)) {
            getServer().getScheduler().runTask(this, () -> {
                refreshQueued.set(false);
                refreshEveryone();
            });
        }
    }

    private void refreshEveryone() {
        tagManager.updateAll();
        if (tagsConfig.isHeaderFooter()) {
            tabListHeader.updateAll();
        }
    }
}
