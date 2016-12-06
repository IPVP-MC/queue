package org.ipvp.queue.sign;

import org.bukkit.block.Sign;

public class JoinSign extends QueueSign {

    public JoinSign(Sign handle, String server) {
        super(handle, server);
    }

    /**
     * Updates the sign with the players online and the total number of players allowed on the server
     *
     * @param online Amount of players online
     * @param max Maximum server capacity
     */
    public void update(int online, int max) {
        Sign sign = getHandle();
        sign.setLine(2, online + "/" + max);
        sign.update();
    }
}
