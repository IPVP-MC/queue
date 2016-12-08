package org.ipvp.queue.sign;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.block.Sign;

public class JoinSign extends QueueSign {

    public JoinSign(Sign handle, String server) {
        super(handle, Type.JOIN, server);
    }

    /**
     * Updates the sign with the players online and the total number of players allowed on the server
     *
     * @param online Amount of players online
     * @param max Maximum server capacity
     */
    public void update(int online, int max) {
        Sign sign = getHandle();
        sign.setLine(0, ChatColor.BOLD + getServer());
        sign.setLine(1, online + "/" + max);
        sign.setLine(2, ChatColor.GREEN + "Click to join");
        sign.setLine(3, ChatColor.GREEN + "the queue!");
        sign.update();
    }
}
