package me.itsghost.jdiscord.internal.impl;

import lombok.Getter;
import lombok.Setter;
import me.itsghost.jdiscord.DiscordAPIImpl;
import me.itsghost.jdiscord.talkable.Group;
import me.itsghost.jdiscord.talkable.Talkable;
import me.itsghost.jdiscord.talkable.User;

public class UserImpl implements User, Talkable {
    @Getter
    private String username;
    @Getter
    private String id;
    @Getter
    private String cid;
    @Getter
    @Setter
    private String avatar;
    @Getter
    @Setter
    private String avatarId;
    private DiscordAPIImpl api;

    public UserImpl(String username, String id, String cid, DiscordAPIImpl api) {
        this.api = api;
        this.id = id;
        this.cid = cid;
        this.username = username;

        if (!api.getUserGroups().containsKey(id)) {
            GroupImpl group = new GroupImpl(id, cid, null, api);
            group.setName(username);
            api.getUserGroups().put(id, group);
        }
    }

    @Override
    public String toString() {
        return username;
    }

    @Override
    public Group getGroup() {
        return api.getUserGroups().get(id);
    }

    @Override
    public boolean equals(Object a){
        return ((a instanceof String) && ((((String)a).equals(id)) || (((String)a).equals(cid))));
    }
}
