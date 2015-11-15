package me.itsghost.jdiscord.internal.request.poll;

import me.itsghost.jdiscord.Server;
import me.itsghost.jdiscord.events.ChannelDeletedEvent;
import me.itsghost.jdiscord.events.ChannelPermsUpdatedEvent;
import me.itsghost.jdiscord.events.ChannelUpdatedEvent;
import me.itsghost.jdiscord.internal.impl.DiscordAPIImpl;
import me.itsghost.jdiscord.internal.impl.GroupImpl;
import me.itsghost.jdiscord.talkable.Group;
import me.itsghost.jdiscord.talkable.GroupUser;
import org.json.JSONArray;
import org.json.JSONObject;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class ChannelUpdatePoll implements Poll {
    private DiscordAPIImpl api;

    public ChannelUpdatePoll(DiscordAPIImpl api) {
        this.api = api;
    }

    @Override
    public void process(JSONObject content, JSONObject rawRequest, Server server){
        GroupImpl group = (GroupImpl)server.getGroupById(content.getString("id"));
        if (content.isNull("permission_overwrites")) {
            group.setName(content.getString("name"));
            api.getEventManager().executeEvent(new ChannelUpdatedEvent(group));
        }else{
            //TODO:
            JSONArray array = content.getJSONArray("permission_overwrites");
            for (int i = 0; i < array.length(); i ++){
                JSONObject obj = array.getJSONObject(i);
                switch (obj.getString("type")){
                    case "role":
                        break;

                    case "member":
                        GroupUser user = server.getGroupUserById(obj.getString("id"));
                        System.out.println("User: " + user);
                        System.out.println("Allow: " + obj.getInt("allow"));
                        System.out.println("Deny: " + obj.getInt("deny"));
                        break;
                    default:
                        throw new NotImplementedException();
                }
            }
            api.getEventManager().executeEvent(new ChannelPermsUpdatedEvent(group));
        }
    }
}
