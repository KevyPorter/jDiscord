package me.itsghost.jdiscord.internal.request.poll.voice;

import me.itsghost.jdiscord.DiscordAPI;
import me.itsghost.jdiscord.internal.impl.DiscordAPIImpl;
import me.itsghost.jdiscord.internal.impl.VoiceGroupImpl;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.net.URI;


public class VoiceClientHandle extends org.java_websocket.client.WebSocketClient{

    private DiscordAPIImpl api;
    private VoiceGroupImpl vGroup;

    public VoiceClientHandle(DiscordAPI api, String url, VoiceGroupImpl vGroup) {
        super(URI.create("ws://" + url));
        this.api = (DiscordAPIImpl)api;
        this.vGroup = vGroup;
        this.api.log("Connecting to voice server " + url);
        this.connect();
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        api.log("Opened voice chat!");
        api.log("Handshaking...");
        handshake();
        api.log("Connected to voice chat!");
    }

    @Override
    public void onMessage(String s) {
        try {
            JSONObject obj = new JSONObject(s);
            int id = obj.getInt("op");

            switch (id){
                case 2:

                    new Thread(() -> {
                        try {
                            api.log(String.valueOf(obj.getJSONObject("d").getLong("heartbeat_interval")));
                            Thread.sleep(obj.getJSONObject("d").getLong("heartbeat_interval"));
                            send(new JSONObject().put("op", 3).put("d", JSONObject.NULL));
                            api.log("PINGED>>>>>>>");
                            api.log("PINGED " + new JSONObject().put("op", 3).put("d", JSONObject.NULL).toString());
                            api.log("PINGED ");
                            api.log("PINGED ");
                            api.log("PINGED>>>>>>");

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }).start();

                    break;

                case 4:
                    api.log("Voice: " + s);
                    break;
                case 5:

                    break;
                default:
                    api.log("UNKNOWN VOICE DATA: " + s);
                    break;
            }

        }catch(Exception e){}

    }

    @Override
    public void onClose(int i, String s, boolean b) {
        System.err.println("Discord Testing: YOU DONE FUCKED IT UP, exit code: " + i);
    }

    @Override
    public void onError(Exception e) {
        System.err.println("Discord Testing: Internal error wasn't handled");
        e.printStackTrace();
    }

    public void handshake(){
        JSONObject obj = new JSONObject()
                .put("op", 0)
                .put("compress", true)
                .put("d", new JSONObject()
                    .put("server_id", vGroup.getServer().getId())
                    .put("user_id", api.getSelfInfo().getId())
                    .put("session_id", vGroup.getSession())
                    .put("token", vGroup.getToken()));
        send(obj);
        send ("{\"op\":1,\"d\":{\"protocol\":\"webrtc\",\"data\":\"v=0\\no=- 5210216809399196657 2 IN IP4 127.0.0.1\\ns=-\\nt=0 0\\na=group:BUNDLE audio\\na=msid-semantic: WMS\\nm=audio 19094 UDP/TLS/RTP/SAVPF 111 103 104 9 0 8 106 105 13 126\\nc=IN IP4 52.18.31.92\\na=rtcp:13829 IN IP4 52.18.31.92\\na=candidate:828646434 1 UDP 2122260223 192.168.220.1 54119 typ host generation 0\\na=candidate:209874304 1 UDP 2122194687 192.168.234.1 54120 typ host generation 0\\na=candidate:2138393493 1 UDP 2122129151 192.168.0.4 54121 typ host generation 0\\na=candidate:828646434 2 UDP 2122260222 192.168.220.1 54122 typ host generation 0\\na=candidate:209874304 2 UDP 2122194686 192.168.234.1 54123 typ host generation 0\\na=candidate:2138393493 2 UDP 2122129150 192.168.0.4 54124 typ host generation 0\\na=candidate:3593126976 2 UDP 1685921534 86.21.201.3 54124 typ srflx raddr 192.168.0.4 rport 54124 generation 0\\na=candidate:3593126976 1 UDP 1685921535 86.21.201.3 54121 typ srflx raddr 192.168.0.4 rport 54121 generation 0\\na=candidate:2018991400 1 UDP 41754367 52.18.31.92 19094 typ relay raddr 86.21.201.3 rport 54121 generation 0\\na=candidate:2018991400 2 UDP 41754366 52.18.31.92 13829 typ relay raddr 86.21.201.3 rport 54124 generation 0\\na=ice-ufrag:sH+cJWHn/IyShwIu\\na=ice-pwd:1Mq9ylf23CfAy8ltl6a3fQn8\\na=fingerprint:sha-256 7E:91:FE:C5:6D:7F:71:D8:FC:1E:8B:F5:B2:7C:3D:EA:80:61:D5:F1:2A:AD:82:BD:6C:0F:F3:8D:64:B2:F7:D9\\na=setup:actpass\\na=mid:audio\\na=extmap:1 urn:ietf:params:rtp-hdrext:ssrc-audio-level\\na=extmap:3 http://www.webrtc.org/experiments/rtp-hdrext/abs-send-time\\na=recvonly\\na=rtcp-mux\\na=rtpmap:111 opus/48000/2\\na=fmtp:111 minptime=10; useinbandfec=1\\na=rtpmap:103 ISAC/16000\\na=rtpmap:104 ISAC/32000\\na=rtpmap:9 G722/8000\\na=rtpmap:0 PCMU/8000\\na=rtpmap:8 PCMA/8000\\na=rtpmap:106 CN/32000\\na=rtpmap:105 CN/16000\\na=rtpmap:13 CN/8000\\na=rtpmap:126 telephone-event/8000\\na=maxptime:60\\n\"}}");
    }

    public void send(JSONObject obj){
        send(obj.toString());
    }

    @Override
    public void send(String s){
        try{
            while(!(this.getConnection().isOpen())){
                Thread.sleep(500);
            }
            super.send(s);
        }catch(Exception e){}
    }

}
