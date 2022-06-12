package com.chat.server.domain.listconversations;


import com.chat.server.domain.conversationstorage.ConversationStorageFacade;
import com.chat.server.domain.conversationstorage.dto.ConversationAddedEvent;
import com.chat.server.domain.conversationstorage.dto.ConversationRemovedEvent;
import com.chat.server.domain.listconversations.dto.ConversationPreviewDto;
import org.springframework.context.event.EventListener;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ListConversationsFacade {
    private final ConversationRepository conversationRepository;
    private final ConversationStorageFacade conversationStorageFacade;

    public ListConversationsFacade(ConversationRepository conversationRepository, ConversationStorageFacade conversationStorageFacade) {
        this.conversationRepository = conversationRepository;
        this.conversationStorageFacade = conversationStorageFacade;
    }

    public void add(String username, UUID conversationId){
        conversationRepository.add(username, conversationId);
    }

    public void remove(String username, UUID conversationId){
        conversationRepository.remove(username, conversationId);
    }

    public List<UUID> listConversations(String username){
        return conversationRepository.get(username);
    }

    public List<ConversationPreviewDto> listConversationPreviews(String username){
        List<UUID> conversations = listConversations(username);
        return conversations.stream().map(conversationStorageFacade::get).filter(Optional::isPresent).map(conversationDto -> conversationDto.get().conversationPreviewDto()).toList();
    }

    @EventListener
    public void handleConversationRemovedEvent(ConversationRemovedEvent event){
        event.members().forEach(username -> remove(username, event.conversationId()));
    }

    @EventListener
    public void handleConversationAddedEvent(ConversationAddedEvent event){
        event.members().forEach(username -> add(username, event.conversationId()));
    }
}
