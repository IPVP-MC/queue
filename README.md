# queue #

A simple BungeeCord plugin that controls join rate of players to individual servers. 

### Adding a player to a queue ###
All servers defined in the BungeeCord instance have a queue that players can be sent added to programmatically. The plugin only enforces specific players joining the queue and does not override the behaviors of other plugins or commands such as `/server`.
 
Players can be added to a specific server queue through the Plugin Messaging Channel. The Queue plugin registers and listens for commands on the `Queue` channel, so plugins must register the outgoing channel appropriately. 
  
The `Join` subchannel is used to add a player to a specific queue and takes the following arguments:
* Player UUID
* Target server

An example of a helper method to send a player to a queue is:
```java
/**
 * Connects a player to a servers queue.
 * 
 * @param player Player to connect
 * @param target Queue to join
 */
public void joinServerQueue(Player player, String target) {
    ByteArrayDataOutput out = ByteStreams.newDataOutput();
    out.writeUTF("Join");
    out.writeUTF(player.getUniqueId().toString());
    out.writeUTF(target);
    player.sendPluginMessage(this, "Queue", out.toByteArray());
}
```

## License ##
This software is available under the following licenses:

* MIT