package org.ipvp.queue.command;

import net.md_5.bungee.api.plugin.Command;
import org.ipvp.queue.QueuePlugin;

public abstract class QueuePluginCommand extends Command {

    private QueuePlugin plugin;

    public QueuePluginCommand(QueuePlugin plugin, String name) {
        super(name);
        this.plugin = plugin;
    }

    public QueuePluginCommand(QueuePlugin plugin, String name, String permission, String... aliases) {
        super(name, permission, aliases);
        this.plugin = plugin;
    }

    public QueuePlugin getPlugin() {
        return plugin;
    }
}
