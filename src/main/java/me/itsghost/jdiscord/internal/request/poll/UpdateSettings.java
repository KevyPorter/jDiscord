package me.itsghost.jdiscord.internal.request.poll;

import me.itsghost.jdiscord.Server;
import me.itsghost.jdiscord.events.UserTypingEvent;
import me.itsghost.jdiscord.internal.impl.DiscordAPIImpl;
import me.itsghost.jdiscord.talkable.Group;
import me.itsghost.jdiscord.talkable.GroupUser;
import me.itsghost.jdiscord.talkable.User;
import org.json.JSONObject;

public class UpdateSettings implements Poll {
    private DiscordAPIImpl api;

    public UpdateSettings(DiscordAPIImpl api) {
        this.api = api;
    }

    @Override
    public void process(JSONObject content, JSONObject rawRequest, Server server) {
        if (content.getString("id").equals(api.getSelfInfo().getId())) {
            api.getSelfInfo().setUsername(content.getString("username"));
            api.getSelfInfo().setEmail(content.getString("email"));
            api.getSelfInfo().setAvatarId("https://cdn.discordapp.com/avatars/" + api.getSelfInfo().getId() + "/" + (content.isNull("avatar") ? "" : content.getString("avatar")) + ".jpg");
            api.getSelfInfo().setAvatar(content.isNull("avatar") ? "" : content.getString("avatar"));
        } else {
            System.out.println(rawRequest);
        }
    }
}
