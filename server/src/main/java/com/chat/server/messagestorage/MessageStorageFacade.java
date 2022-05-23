package com.chat.server.messagestorage;

import com.chat.server.messagestorage.dto.MessageDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MessageStorageFacade {
    MessageRepository messageRepository;

    public void addChannel(String channelId){
        messageRepository.add(channelId);
    }

    public void removeChannel(String channelId){
        messageRepository.remove(channelId);
    }

    public void saveMessage(String channelId, MessageDto messageDto){
        messageRepository.save(channelId, new MessageCreator().create(messageDto));
    }

    public Page<MessageDto> getMessages(String channelId, Pageable pageable){
        return messageRepository.findAll(channelId, pageable).map(Message::dto);
    }
}
