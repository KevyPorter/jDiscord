package me.itsghost.jdiscord.internal.request.poll.voice;

import me.itsghost.jdiscord.internal.request.WebSocketClient;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.drafts.Draft_17;

public class WebClientImpl extends WebSocketImpl {
    public WebClientImpl(WebSocketClient a){
        super(a, new Draft_17());
        //onWebsocketMessageFragment
    }

}
