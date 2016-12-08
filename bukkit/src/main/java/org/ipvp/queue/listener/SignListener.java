package org.ipvp.queue.listener;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.ipvp.queue.QueuePlugin;
import org.ipvp.queue.sign.InfoSign;
import org.ipvp.queue.sign.JoinSign;
import org.ipvp.queue.sign.QueueSign;

public class SignListener implements Listener {

    public QueuePlugin plugin;

    public SignListener(QueuePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onSignCreate(SignChangeEvent event) {
        Player player = event.getPlayer();
        Sign sign = (Sign) event.getBlock().getState();
        String header = event.getLine(0);

        if (header.equalsIgnoreCase("[Join]")) {
            if (!player.hasPermission("queue.sign.create.join")) {
                player.sendMessage(ChatColor.RED + "You do not have permission to create join signs");
                event.setCancelled(true);
                return;
            }
            event.setLine(0, ChatColor.DARK_BLUE + "[Join]");
            plugin.registerSign(new JoinSign(sign, event.getLine(1)));
            player.sendMessage(ChatColor.GREEN + "Successfully created queue join sign");
        } else if (header.equalsIgnoreCase("[Info]")) {
            if (!player.hasPermission("queue.sign.create.info")) {
                player.sendMessage(ChatColor.RED + "You do not have permission to create info signs");
                event.setCancelled(true);
                return;
            }
            event.setLine(0, ChatColor.DARK_BLUE + "[Info]");
            plugin.registerSign(new InfoSign(sign, event.getLine(1)));
            player.sendMessage(ChatColor.GREEN + "Successfully created queue info sign");
        }
    }

    @EventHandler
    public void onUseSign(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK
                && event.getClickedBlock().getState() instanceof Sign) {
            Sign sign = (Sign) event.getClickedBlock().getState();

            QueueSign qs = plugin.getSign(sign.getLocation());
            if (qs instanceof JoinSign) {
                String target = qs.getServer();
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("Join");
                out.writeUTF(event.getPlayer().getUniqueId().toString());
                out.writeUTF(target);
                player.sendPluginMessage(plugin, "Queue", out.toByteArray());
            }
        }
    }
}
