package me.itsghost.jdiscord.message;

import me.itsghost.jdiscord.talkable.Group;
import me.itsghost.jdiscord.talkable.GroupUser;
import me.itsghost.jdiscord.talkable.User;

import java.util.List;

public interface Message {
    String getMessage();

    void setMessage(String message);

    User getSender();

    String getId();

    String getGroupId();

    boolean isEdited();

    void applyUserTag(String username, Group server);

    List<GroupUser> getMentions();

    void deleteMessage();

    void editMessage(String message);
}
