package com.chat.server.domain.listconversationids;

import com.chat.server.domain.conversationstorage.ConversationRepository;
import com.chat.server.domain.conversationstorage.ConversationStorageFacade;
import com.chat.server.domain.listconversationids.dto.ConversationIdAddedEvent;
import com.chat.server.domain.listconversationids.dto.ConversationIdRemovedEvent;
import com.chat.server.domain.listconversationids.dto.ConversationPreviewDto;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ListConversationIdsFacade {
    private final ConversationIdRepository conversationIdRepository;
    private final ConversationStorageFacade conversationStorageFacade;

    public ListConversationIdsFacade(
            ConversationIdRepository conversationIdRepository,
            ConversationStorageFacade conversationStorageFacade) {
        this.conversationIdRepository = conversationIdRepository;
        this.conversationStorageFacade = conversationStorageFacade;
        conversationStorageFacade.getConversationRepository().addIdObserver(observer);
    }

    public void add(String username, UUID conversationId){
        conversationIdRepository.add(username, conversationId);
    }

    public void remove(String username, UUID conversationId){
        conversationIdRepository.remove(username, conversationId);
    }

    public List<UUID> listConversations(String username){
        return conversationIdRepository.get(username);
    }

    public List<ConversationPreviewDto> listConversationPreviews(String username){
        List<UUID> conversations = listConversations(username);
        return conversations.stream()
                .map(conversationStorageFacade::get)
                .filter(Optional::isPresent)
                .map(conversationDto -> conversationDto.get().conversationPreviewDto())
                .toList();
    }
    private final ConversationRepository.IdObserver observer = new ConversationRepository.IdObserver() {
        @Override
        public void notifyAdd(ConversationIdAddedEvent event) {
            event.members().forEach(username -> remove(username, event.conversationId()));
        }

        @Override
        public void notifyRemove(ConversationIdRemovedEvent event) {
            event.members().forEach(username -> add(username, event.conversationId()));
        }
    };
}
