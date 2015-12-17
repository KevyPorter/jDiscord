package me.itsghost.jdiscord.internal.request.poll;

import me.itsghost.jdiscord.events.AddedToServer;
import me.itsghost.jdiscord.events.ChannelDeletedEvent;
import me.itsghost.jdiscord.internal.impl.DiscordAPIImpl;
import me.itsghost.jdiscord.Server;
import me.itsghost.jdiscord.internal.impl.GroupImpl;
import me.itsghost.jdiscord.internal.impl.ServerImpl;
import me.itsghost.jdiscord.internal.impl.UserImpl;
import me.itsghost.jdiscord.talkable.GroupUser;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class NewContactOrGroupPoll implements Poll {
    private DiscordAPIImpl api;

    public NewContactOrGroupPoll(DiscordAPIImpl api) {
        this.api = api;
    }

    @Override
    public void process(JSONObject content, JSONObject rawRequest, Server server) {
        if (content.getBoolean("is_private")) {
            String cid = content.getString("id");

            JSONObject recp = content.getJSONObject("recipient");
            String id = recp.getString("id");
            String avatarId = recp.getString("avatar");
            String avatar = recp.isNull("avatar") ? "" : "https://cdn.discordapp.com/avatars/" + id + "/" + avatarId + ".jpg";
            String username = recp.getString("username");

            UserImpl userImpl = new UserImpl(username, id, cid, api);
            userImpl.setAvatar(avatar);
            userImpl.setAvatarId(avatarId);

            api.updateContact(userImpl);
        }else{
            GroupImpl group = new GroupImpl(content.getString("id"), content.getString("id"), server, api);
            group.setName(content.getString("name"));
            server.getGroups().add(group);
            api.getEventManager().executeEvent(new ChannelDeletedEvent(group));
        }
    }
}
