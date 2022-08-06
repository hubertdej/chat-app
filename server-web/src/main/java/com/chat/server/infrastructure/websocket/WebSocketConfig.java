package com.chat.server.infrastructure.websocket;

import com.chat.server.domain.authentication.AuthenticationFacade;
import com.chat.server.domain.messagereceiver.MessageReceiverFacade;
import com.chat.server.domain.sessionstorage.SessionStorageFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import java.util.concurrent.ConcurrentHashMap;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    @Autowired
    MessageReceiverFacade messageReceiverFacade;
    @Autowired
    AuthenticationFacade authenticationFacade;
    @Autowired
    SessionStorageFacade sessionStorageFacade;
    @Autowired
    ConcurrentHashMap<WebSocketSession, SessionStorageFacade.Observer> observersMap; //TODO ok?

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(
                new WebSocketHandler(messageReceiverFacade, authenticationFacade, sessionStorageFacade, observersMap),
                "/chat"
        );
    }
}
