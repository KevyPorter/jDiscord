package me.itsghost.jdiscord.talkable;

import lombok.Getter;
import lombok.Setter;
import me.itsghost.jdiscord.Role;
import me.itsghost.jdiscord.Server;
import me.itsghost.jdiscord.internal.impl.GroupImpl;
import me.itsghost.jdiscord.internal.impl.ServerImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupUser {
    @Getter private User user;
    @Getter @Setter private List<Role> roles;
    @Getter private String discriminator;

    public GroupUser(User user, List<Role> role, String discriminator) {
        this.user = user;
        this.roles = role;
        this.discriminator = discriminator;
    }

    public String toString() {
        return user.getUsername();
    }

    public Map<String, Integer> getUserPerms(Group group){
        GroupImpl a = (GroupImpl) group;
        if (a.getPermsOverride().containsKey(user.getId())){
            return a.getPermsOverride().get(user.getId());
        }else{
            List<Map<String, Integer>> combo = new ArrayList<>();
            int allow = 0;
            int deny = 0;
            for (Role role : roles){
                combo.add(role.getRole());
            }
            for (Map<String, Integer> key : combo){
                int localAllow = key.get("allow");
                int localDeny = key.containsKey("deny") ? key.get("deny") : 0;
                allow = localAllow > allow ? localAllow : allow;
                deny = localDeny > deny ? localDeny : deny;
            }
            Map<String, Integer> perms = new HashMap<>();
            perms.put("allow", allow);
            perms.put("deny", deny);
            return perms;
        }
    }

    public Map<Role, Map<String, Integer>> getAllPerms(Server server){
        Map<Role, Map<String, Integer>> e = new HashMap<>();
        for (Role role : ((ServerImpl)server).getRoles()){
            e.put(role, role.getRole());
        }
        return e;
    }
}
