package me.itsghost.jdiscord.talkable;

import lombok.Getter;
import lombok.Setter;
import me.itsghost.jdiscord.Role;

import java.util.List;

public class GroupUser {
    @Getter private User user;
    @Getter @Setter private List<Role> roles;
    @Getter private String discriminator;

    public GroupUser(User user, String discriminator) {
        this.user = user;
        this.discriminator = discriminator;
    }

    public String toString() {
        return user.getUsername();
    }

    public boolean equals(Object e) {
        return user.equals(e);
    }

    public boolean hasPerm(Permissions perm) {
        if (perm == null) return true;
        for(Role r : getRoles()){
            int allow = r.getRole().get("allow");
            int offset = perm.getOffset();
            if(((allow >> offset) & 1) == 1)
                return true;
        }
        return false;
    }

    public enum Permissions {
        INSTANT_INVITE(0),
        KICK_MEMBER(1),
        BAN_MEMBER(2),
        MANAGE_ROLES(3),
        MANAGE_CHANNELS(4),
        MANAGE_SERVER(5),
        READ_MESSAGES(10),
        SEND_MESSAGES(11),
        SEND_TTS_MESSAGES(12),
        MANAGE_MESSAGES(13),
        EMBED_LINKS(14),
        ATTACH_FILES(15),
        READ_MESSAGE_HISTORY(16),
        MENTION_EVERYONE(17),
        VOICE_CONNECT(20),
        VOICE_SPEAK(21),
        VOICE_MUTE_MEMBERS(22),
        VOICE_DEAFEN_MEMBERS(23),
        VOICE_MOVE_MEMBERS(24),
        VOICE_USE_VAD(25);

        int offset;

        Permissions(int offset) {
            this.offset = offset;
        }

        public int getOffset() {
            return offset;
        }
        }

}
