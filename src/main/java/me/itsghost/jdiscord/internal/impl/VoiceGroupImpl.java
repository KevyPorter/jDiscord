package me.itsghost.jdiscord.internal.impl;

import lombok.Getter;
import lombok.Setter;
import me.itsghost.jdiscord.DiscordAPI;
import me.itsghost.jdiscord.Server;
//import me.itsghost.jdiscord.internal.voice.VoiceClientHandle;
import me.itsghost.jdiscord.internal.request.WebSocketClient;
import org.json.JSONObject;

public class VoiceGroupImpl {

    @Getter private String id;
    @Getter private String name;
    @Getter private Server server;
    @Getter private WebSocketClient client;
    @Getter @Setter private String session;
  //  @Getter private VoiceClientHandle voice;
    @Getter private String token;
    private DiscordAPI api;

    public VoiceGroupImpl(String id, String name, Server server, DiscordAPI api){
        this.id = id;
        this.name = name;
        this.server = server;
        this.api = api;
        client = ((DiscordAPIImpl)api).getRequestManager().getSocketClient();
    }

    public void connect(){
        JSONObject obj = new JSONObject().put("op", 4)
                .put("d", new JSONObject()
                    .put("guild_id", server.getId())
                    .put("channel_id", id)
                    .put("self_mute", false)
                    .put("self_deaf", false)
                );
        client.send(obj);

        new loadThread(this).start();
    }

    public class loadThread extends Thread {
        private VoiceGroupImpl a;
        public loadThread(VoiceGroupImpl a){
            this.a = a;
        }

        @Override
        public void run() {
            while ((session == null)  ||  (((ServerImpl) server).getToken() == null)) {
                try { Thread.sleep(750);} catch (Exception e) {}
            }
            token = ((ServerImpl) server).getToken();
     //       voice = new VoiceClientHandle(api, ((ServerImpl) server).getServer(), a);
        }
    }
}
