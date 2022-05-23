package com.chat.server.messagestorage;

class MessageStorageConfiguration {
    MessageStorageFacade messageStorageFacade(){
        MessageRepository messageRepository = new InMemoryMessageRepository();
        return new MessageStorageFacade(messageRepository);
    }
}
