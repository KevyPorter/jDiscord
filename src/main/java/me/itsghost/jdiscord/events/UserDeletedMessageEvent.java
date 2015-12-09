package me.itsghost.jdiscord.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.itsghost.jdiscord.Server;
import me.itsghost.jdiscord.talkable.Group;

@Getter
@AllArgsConstructor
public class UserDeletedMessageEvent {
    private final Group group;
    private final String id;

    public Server getServer(){
        return group.getServer();
    }
}
