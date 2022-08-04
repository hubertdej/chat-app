package com.chat.server.domain.conversationstorage;

import com.chat.server.domain.conversationstorage.dto.ConversationDto;
import com.chat.server.domain.conversationstorage.dto.MessageDto;
import com.chat.server.domain.conversationstorage.dto.NoSuchConversationException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ConversationStorageFacade {
    private final ConversationRepository conversationRepository;

    public ConversationStorageFacade(ConversationRepository conversationRepository) {
        this.conversationRepository = conversationRepository;
    }

    public ConversationRepository getConversationRepository() {
        return conversationRepository;
    }

    public UUID add(List<String> members){
        return add(String.join(",", members), members);
    }

    public UUID add(String name, List<String> members){
        Conversation conversation = new Conversation(UUID.randomUUID(), name, members, new ArrayList<>());
        conversationRepository.save(conversation);
        return conversation.conversationId();
    }

    public void add(UUID conversationId, MessageDto messageDto) throws NoSuchConversationException {
        Message message = new MessageCreator().create(messageDto);
        conversationRepository.addMessage(conversationId, message);
    }

    public void remove(UUID conversationId){
        conversationRepository.remove(conversationId);
    }

    public Optional<ConversationDto> get(UUID conversationId){
        return conversationRepository.get(conversationId).map(Conversation::dto);
    }
}
