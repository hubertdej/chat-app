package com.chat.server.domain.conversationstorage.dto;

public class NoSuchConversationException extends Exception{
    public NoSuchConversationException(){
        super();
    }
    public NoSuchConversationException(String message){
        super(message);
    }
}
