package net.skyminigames.tags.command;

import lombok.RequiredArgsConstructor;
import net.skyminigames.tags.SkyTags;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * /skytags reload
 */
@RequiredArgsConstructor
public final class SkyTagsCommand implements CommandExecutor {

    private final SkyTags plugin;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("skytags.admin")) {
            sender.sendMessage(plugin.getMessages().get("command.no-permission"));
            return true;
        }
        if (args.length != 1 || !args[0].equalsIgnoreCase("reload")) {
            sender.sendMessage(plugin.getMessages().get("command.usage"));
            return true;
        }
        plugin.reload();
        sender.sendMessage(plugin.getMessages().get("command.reloaded"));
        return true;
    }
}
