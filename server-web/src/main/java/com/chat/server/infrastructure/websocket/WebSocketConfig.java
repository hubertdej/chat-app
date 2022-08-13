package com.chat.server.infrastructure.websocket;

import com.chat.server.domain.authentication.AuthenticationFacade;
import com.chat.server.domain.listuserconversations.ListUserConversationsFacade;
import com.chat.server.domain.messagereceiver.MessageReceiverFacade;
import com.chat.server.domain.sessionstorage.SessionStorageFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    private final MessageReceiverFacade messageReceiverFacade;
    private final AuthenticationFacade authenticationFacade;
    private final SessionStorageFacade sessionStorageFacade;
    private final ListUserConversationsFacade listUserConversationsFacade;

    @Autowired
    public WebSocketConfig(MessageReceiverFacade messageReceiverFacade,
                           AuthenticationFacade authenticationFacade,
                           SessionStorageFacade sessionStorageFacade,
                           ListUserConversationsFacade listUserConversationsFacade) {
        this.messageReceiverFacade = messageReceiverFacade;
        this.authenticationFacade = authenticationFacade;
        this.sessionStorageFacade = sessionStorageFacade;
        this.listUserConversationsFacade = listUserConversationsFacade;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(
                new WebSocketHandler(messageReceiverFacade, authenticationFacade, sessionStorageFacade, listUserConversationsFacade),
                "/chat"
        );
    }
}
