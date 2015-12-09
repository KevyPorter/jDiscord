package me.itsghost.jdiscord.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.itsghost.jdiscord.internal.httprequestbuilders.PacketBuilder;
import me.itsghost.jdiscord.internal.httprequestbuilders.RequestType;
import me.itsghost.jdiscord.internal.impl.DiscordAPIImpl;
import me.itsghost.jdiscord.internal.impl.MessageImpl;
import me.itsghost.jdiscord.talkable.Group;
import me.itsghost.jdiscord.talkable.User;
import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

@RequiredArgsConstructor
public class MessageHistory {
    private final DiscordAPIImpl api;
    @Getter private final Group group;
    private String lastId = null;
    private boolean atEnd = false;
    private List<Message> queued = new LinkedList<>();

    /**
     * Gets all available Messages. Can be called multiple times and always returns the full set
     * @return all available Messages
     */
    public List<Message> getAll() {
        while(!atEnd && get() != null) {
            //Nothing needed here
        }
        return queued;
    }

    /**
     * Returns all already by the get methods pulled messages of this history
     * @return the list of already pulled messages
     */
    public List<Message> getRecent() {
        return queued;
    }

    /**
     * Queues the next set of 50 Messages and returns them
     * If the end of the chat was already reached, this function returns null
     * @return a list of the next 50 Messages (max), or null if at end of chat
     */
    public List<Message> get() {
        return get(50);
    }

    /**
     * Queues the next set of Messages and returns them
     * If the end of the chat was already reached, this function returns null
     * @param amount the amount to Messages to queue
     * @return a list of the next [amount] Messages (max), or null if at end of chat
     */
    public List<Message> get(int amount) {
        if(atEnd) {
            return null;
        }
        PacketBuilder pb = new PacketBuilder(api);
        pb.setType(RequestType.GET);
        pb.setUrl("https://discordapp.com/api/channels/" + group.getId() + "/messages?limit=" + amount + (lastId != null ? "&before=" + lastId : ""));
        LinkedList<Message> out = new LinkedList<>();
        try {
            JSONArray array = new JSONArray(pb.makeRequest());
            for(int i = 0; i < array.length(); i++) {
                JSONObject content = array.getJSONObject(i);
                //Code from MessagePoll
                if (content.isNull("author"))
                    continue; //image update event?

                String id = content.getString("channel_id");
                String authorId = content.getJSONObject("author").getString("id");

                User user = api.getUserById(authorId);

                user = (user == null) ? api.getBlankUser() : user;

                String msgContent = (content.isNull("proxy_url") ? StringEscapeUtils.unescapeJson(content.getString("content")) : content.getJSONObject("embeds").getString("url"));
                String msgId = content.getString("id");

                MessageImpl msg = new MessageImpl(msgContent, msgId, id, api);
                msg.setSender(user);
                msg.setTimestamp(content.getString("timestamp"));

                if (!content.isNull("edited_timestamp"))
                    msg.setEdited(true);

                //End MessagePoll code

                out.add(msg);
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }

        if(out.size() < amount) {
            atEnd = true;
        }
        if(out.size() > 0) {
            lastId = out.getLast().getId();
        } else {
            return null;
        }
        queued.addAll(out);
        return out;
    }
}
