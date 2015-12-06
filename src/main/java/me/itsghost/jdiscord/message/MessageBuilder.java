package me.itsghost.jdiscord.message;

import me.itsghost.jdiscord.internal.impl.MessageImpl;
import me.itsghost.jdiscord.talkable.Group;
import me.itsghost.jdiscord.talkable.GroupUser;
import me.itsghost.jdiscord.talkable.User;
import org.json.JSONArray;

public class MessageBuilder {

    private StringBuilder sb = new StringBuilder();
    private JSONArray mentions = new JSONArray();

    public MessageBuilder addString(String string) {
        sb.append(string);
        return this;
    }

    public MessageBuilder addObject(Object obj) {
        sb.append(obj);
        return this;
    }

    public MessageBuilder addBold(String text){
        sb.append("**" +  text + "**");
        return this;
    }

    public MessageBuilder addCode(String text){
        sb.append("`" +  text + "`");
        return this;
    }

    public MessageBuilder addItalic(String text){
        sb.append("_" +  text + "_");
        return this;
    }
    
    public MessageBuilder addUnderlined(String text){
        sb.append("__" +  text + "__");
        return this;
    }
    
    public MessageBuilder addStrikethrough(String text){
        sb.append("~~" +  text + "~~");
        return this;
    }
    
    public MessageBuilder addCodeBlock(String language, String text){
        sb.append(String.format("```%s\n", language) +  text + "```");
        return this;
    }

    public MessageBuilder addUserTag(GroupUser user, Group server) {
        return addUserTag(user.getUser().getUsername(), server);
    }

    public MessageBuilder addUserTag(User user, Group server) {
        return addUserTag(user.getUsername(), server);
    }

    public MessageBuilder addUserTag(String username, Group server) {
        addString("@" + username);

        GroupUser gp = server.getServer().getGroupUserByUsername(username);
        if (gp == null)
            return this;

        sb = new StringBuilder(sb.toString().replace("@" + username, "<@" + gp.getUser().getId() + ">")); //

        mentions.put(gp.getUser().getId().toString());
        return this;
    }

    public Message build() {
        MessageImpl message = new MessageImpl(sb.toString());
        message.setMentions(mentions);
        return message;
    }
}
