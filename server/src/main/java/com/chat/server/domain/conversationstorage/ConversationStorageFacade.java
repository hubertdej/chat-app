package com.chat.server.domain.conversationstorage;

import com.chat.server.domain.conversationstorage.dto.ConversationDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ConversationStorageFacade {
    ConversationRepository conversationRepository;

    public UUID add(List<String> members){
        return add(String.join(",", members), members);
    }

    public UUID add(String name, List<String> members){
        Conversation conversation = new Conversation(UUID.randomUUID(), name, members, new ArrayList<>());
        conversationRepository.save(conversation);
        return conversation.conversationId();
    }

    public void remove(UUID conversationId){
        conversationRepository.remove(conversationId);
    }

    public Optional<ConversationDto> get(UUID conversationId){
        return conversationRepository.get(conversationId).map(Conversation::dto);
    }
}
