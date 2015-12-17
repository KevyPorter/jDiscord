package me.itsghost.jdiscord.internal.request.poll;

import me.itsghost.jdiscord.OnlineStatus;
import me.itsghost.jdiscord.Role;
import me.itsghost.jdiscord.internal.impl.*;
import me.itsghost.jdiscord.SelfData;
import me.itsghost.jdiscord.Server;
import me.itsghost.jdiscord.events.APILoadedEvent;
import me.itsghost.jdiscord.internal.utils.GameIdUtils;
import me.itsghost.jdiscord.talkable.GroupUser;
import me.itsghost.jdiscord.talkable.User;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReadyPoll implements Poll {
    private Thread thread;
    private DiscordAPIImpl api;

    public ReadyPoll(DiscordAPIImpl api) {
        this.api = api;
    }

    /*
        What the fuck happened here?
     */

    @Override
    public void process(JSONObject content, JSONObject rawRequest, Server server) {
        if (api.isLoaded())
            return; //we reconnected

        SelfData data = new SelfData();
        JSONObject userDataJson = content.getJSONObject("user");

        data.setUsername(userDataJson.getString("username"));
        data.setEmail(userDataJson.getString("email"));
        data.setId(userDataJson.getString("id"));
        data.setAvatar("https://cdn.discordapp.com/avatars/" + data.getId() + "/" + (userDataJson.isNull("avatar") ? "" : userDataJson.getString("avatar")) + ".jpg");
        data.setAvatarId((userDataJson.isNull("avatar") ? "" : userDataJson.getString("avatar")));

        api.setSelfInfo(data);

        thread = new Thread(() -> {
            while (!api.getRequestManager().getSocketClient().getConnection().isClosed()) {
                api.getRequestManager().getSocketClient().send(new JSONObject().put("op", 1).put("d", System.currentTimeMillis()).toString());
                try {
                    Thread.sleep(content.getLong("heartbeat_interval"));
                } catch (Exception e) {
                    api.stop();
                }
            }
        });

        thread.start();

        setupServers(content);
        setupContacts(content);

        new Thread(){
            public void run(){
                try {
                    Thread.sleep(500);
                    api.getEventManager().executeEvent(new APILoadedEvent());
                }catch(Exception e){}
            }
        }.start();

        api.setLoaded(true);
    }

    public void setupContacts(JSONObject key) {
        JSONArray array = key.getJSONArray("private_channels");
        for (int i = 0; i < array.length(); i++) {
            JSONObject item = array.getJSONObject(i);
            JSONObject contact = item.getJSONObject("recipient");

            String id = contact.getString("id");

            if (item.getString("id").equals(api.getSelfInfo().getId()))
                api.setAs(contact.getString("id"));

            UserImpl userImpl = new UserImpl(contact.getString("username"), id, item.getString("id"), api);
            userImpl.setAvatar(contact.isNull("avatar") ? "" : "https://cdn.discordapp.com/avatars/" + id + "/" + contact.getString("avatar") + ".jpg");
            userImpl.setAvatarId(contact.isNull("avatar") ? "" : userImpl.getId());

            api.getAvailableDms().add(userImpl);
        }
    }

    public List<GroupUser> getGroupUsersFromJson(JSONObject obj, List<Role> roles) {
        JSONArray members = obj.getJSONArray("members");
        List<GroupUser> guList = new ArrayList<>();

        for (int i = 0; i < members.length(); i++) {
            JSONObject item = members.getJSONObject(i);
            JSONObject user = item.getJSONObject("user");

            String username = user.getString("username");
            String id = user.getString("id");
            String dis = String.valueOf(user.get("discriminator")); //Sometimes returns an int or string... just cast the obj to string
            String avatarId = (user.isNull("avatar") ? "" : user.getString("avatar"));
            UserImpl userImpl;

            if (api.isUserKnown(id)) {
                userImpl = (UserImpl) api.getUserById(id);
            } else {
                userImpl = new UserImpl(username, id, id, api);
                userImpl.setAvatar(user.isNull("avatar") ? "" : "https://cdn.discordapp.com/avatars/" + id + "/" + avatarId + ".jpg");
            }

            List<Role> rolesA = new ArrayList<>();

            if (item.getJSONArray("roles").length() > 0)
                for (Role roleV : roles)
                    if (roleV.getId().equals(item.getJSONArray("roles").opt(0)) || (roleV.getName().equals("@everyone")))
                        rolesA.add(roleV);

            GroupUser gu = new GroupUser(userImpl, dis);
            gu.setRoles(rolesA);
            guList.add(gu);
        }
        return guList;
    }

    public List<GroupUser> updateOnlineStatus(List<GroupUser> users, JSONArray presences){
        for (int i = 0; i < presences.length(); i++) {
            JSONObject item = presences.getJSONObject(i);
            for (GroupUser gUser : users){
                User user = gUser.getUser();
                if (user.equals(item.getJSONObject("user").getString("id"))){
                    String game = item.isNull("game_id") ? "ready to play" : GameIdUtils.getGameFromId(item.get("game_id").toString()); //sometimes int sometimes string
                    OnlineStatus status = OnlineStatus.fromName(item.getString("status"));
                    ((UserImpl)user).setGame(game);
                    ((UserImpl)user).setOnlineStatus(status);
                }
            }
        }
        return users;
    }

    public List<Role> getRoles(JSONArray rolesArray){
        List<Role> roles = new ArrayList<>();

        for (int i = 0; i < rolesArray.length(); i++) {
            JSONObject roleObj = rolesArray.getJSONObject(i);
            Map<String, Integer> perms = new HashMap<>();
            perms.put("allow", roleObj.getInt("permissions"));
            roles.add(new Role(roleObj.getString("name"),
                    roleObj.getString("id"),
                    roleObj.isNull("color") ? null : "#" + String.valueOf(roleObj.get("color")),
                    perms));
        }
        return roles;
    }

    public void setupServers(JSONObject key) {
        JSONArray guilds = key.getJSONArray("guilds");
        for (int i = 0; i < guilds.length(); i++) {
            JSONObject item = guilds.getJSONObject(i);

            ServerImpl server = new ServerImpl(item.getString("id"), api);
            server.setName(item.getString("name"));
            server.setLocation(item.getString("region"));
            server.setCreatorId(item.getString("owner_id"));
            server.setAvatar(item.isNull("icon") ? "" : "https://cdn.discordapp.com/icons/" + server.getId() + "/" + item.getString("icon") + ".jpg");
            server.setRoleMeta(item.getJSONArray("roles"));
            server.setRoles(getRoles(item.getJSONArray("roles")));

            List<GroupUser> users = getGroupUsersFromJson(item, getRoles(item.getJSONArray("roles")));
            users = updateOnlineStatus(users, item.getJSONArray("presences"));

            server.getConnectedClients().addAll(users);

            JSONArray channels = item.getJSONArray("channels");
            for (int ia = 0; ia < channels.length(); ia++) {
                JSONObject channel = channels.getJSONObject(ia);
                if (!channel.getString("type").equals("text")){
                    VoiceGroupImpl group = new VoiceGroupImpl(channel.getString("id"), channel.getString("name"), server, api);
                    server.getVoiceGroups().add(group);
                }else {
                    GroupImpl group = new GroupImpl(channel.getString("id"),
                            channel.getString("id"),
                            server,
                            api);
                    group.setName(channel.getString("name"));
                    server.getGroups().add(group);
                }
            }
            api.getAvailableServers().add(server);
        }
    }

    public void stop() {
        if (thread != null)
            thread.stop();
    }
}
