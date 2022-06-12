package com.chat.server.infrastructure.websocket;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

@Controller
public class ChatController {

    @MessageMapping("/public")
    @SendTo("/topic/public")
    public Message sendToAll(Message message) {
        return new Message(HtmlUtils.htmlEscape(message.getContent()));
    }
}
