package me.itsghost.jdiscord.internal.impl;

import lombok.Getter;
import lombok.Setter;
import me.itsghost.jdiscord.Server;
import me.itsghost.jdiscord.internal.httprequestbuilders.PacketBuilder;
import me.itsghost.jdiscord.internal.httprequestbuilders.RequestType;
import me.itsghost.jdiscord.message.Message;
import me.itsghost.jdiscord.message.MessageHistory;
import me.itsghost.jdiscord.talkable.Group;
import me.itsghost.jdiscord.talkable.Talkable;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class GroupImpl implements Group, Talkable {
    @Getter @Setter private String cid;
    @Getter @Setter private String id;
    @Getter @Setter private String name;
    @Getter private Map<String, Map<String, Integer>> permsOverride = new HashMap<>();
    private DiscordAPIImpl api;
    private Server server;

    public GroupImpl(String id, String cid, Server server, DiscordAPIImpl api) {
        this.api = api;
        this.id = id;
        this.name = id;
        this.cid = cid;
        this.server = server;
    }

    @Override
    public String toString() {
        return name;
    }


    @Override
    public Server getServer() {
        return server;
    }

    @Override
    public MessageHistory getMessageHistory() {
        return new MessageHistory(api, this);
    }

    @Override
    public Message sendMessage(String message) {
        return sendMessage(new MessageImpl(message, id, id, api));
    }

    @Override
    public Message sendMessage(Message messageInterface) {
        if (server == null)
            updateId();

        MessageImpl message = (MessageImpl) messageInterface;
        message.setId(String.valueOf(System.currentTimeMillis()));

        PacketBuilder pb = new PacketBuilder(api);
        pb.setType(RequestType.POST);
        pb.setData(new JSONObject().put("content", message.getMessage())
                .put("mentions", message.getMentions())
                .put("nonce", message.getId())
                .put("tts", false).toString());
        pb.setUrl("https://discordapp.com/api/channels/" + id + "/messages");

        String a = pb.makeRequest();
        if (a != null){
        	message.setId(new JSONObject(a).getString("id"));
        	message.setGroupId(id);
        }

        return message;
    }

    @Override
    public void typing() {
        if ((server == null) || (cid == null))
            updateId();

        PacketBuilder pb = new PacketBuilder(api);
        pb.setType(RequestType.POST);
        pb.setUrl("https://discordapp.com/api/channels/" + id + "/typing");
        pb.makeRequest();
    }

    private JSONObject getLocalAuthor() {
        JSONObject a = new JSONObject()
                .put("username", api.getSelfInfo().getUsername())
                .put("discriminator", server.getGroupUserByUsername(api.getSelfInfo().getUsername()).getDiscriminator())
                .put("avatar", api.getSelfInfo().getAvatarId());
        return a;
    }

    private String getTimestamp() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "T" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + ".135000+00:00";
    }

    private void updateId() {
        if (id.equals(api.getSelfInfo().getId()))
            return;

        PacketBuilder pb = new PacketBuilder(api);
        pb.setUrl("https://discordapp.com/api/users/" + api.getSelfInfo().getId() + "/channels");
        pb.setType(RequestType.POST);
        pb.setData(new JSONObject().put("recipient_id", id).toString());
        String a = pb.makeRequest();

        if (a == null)
            return;

        id = new JSONObject(a).getString("id");
    }

}
