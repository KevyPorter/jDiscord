package me.itsghost.jdiscord.internal.request.poll;

import me.itsghost.jdiscord.Role;
import me.itsghost.jdiscord.Server;
import me.itsghost.jdiscord.events.ChannelDeletedEvent;
import me.itsghost.jdiscord.internal.impl.DiscordAPIImpl;
import me.itsghost.jdiscord.internal.impl.ServerImpl;
import me.itsghost.jdiscord.internal.impl.UserImpl;
import me.itsghost.jdiscord.talkable.Group;
import me.itsghost.jdiscord.talkable.GroupUser;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserUpdatePoll implements Poll {
    private DiscordAPIImpl api;

    public UserUpdatePoll(DiscordAPIImpl api) {
        this.api = api;
    }

    @Override
    public void process(JSONObject content, JSONObject rawRequest, Server server){
        if (content.isNull("user"))
            return; //proxy url nolonger valid

        JSONObject user = content.getJSONObject("user");
        JSONArray rolesArray = content.getJSONArray("roles");
        GroupUser gUser = server.getGroupUserById(user.getString("id"));

        ((UserImpl)gUser.getUser()).setUsername(user.getString("username"));
        ((UserImpl)gUser.getUser()).setAvatarId(user.getString("avatar"));
        ((UserImpl)gUser.getUser()).setAvatar("https://cdn.discordapp.com/avatars/" + api.getSelfInfo().getId() + "/" + (user.isNull("avatar") ? "" : user.getString("avatar")) + ".jpg");


        List<Role> roles = new ArrayList<>();

        for (int i = 0; i < rolesArray.length(); i++) {
            JSONObject roleObj = rolesArray.getJSONObject(i);
            for (Role role : getRoles(((ServerImpl)server).getRoleMeta()))
                if (role.getId().equals(roleObj.getString("id")))
                    roles.add(role);
        }

        gUser.setRoles(roles);
    }


    public List<Role> getRoles(JSONArray rolesArray){
        List<Role> roles = new ArrayList<>();
        for (int i = 0; i < rolesArray.length(); i++) {
            JSONObject roleObj = rolesArray.getJSONObject(i);


            Map<String, Integer> perms = new HashMap<>();
            perms.put("allow", roleObj.getInt("allow"));
            perms.put("deny", roleObj.getInt("deny"));

            roles.add(new Role(roleObj.getString("name"),
                    roleObj.getString("id"),
                    roleObj.isNull("color") ? null : "#" + String.valueOf(roleObj.getInt("color")),
                    perms));
        }
        return roles;
    }
}
