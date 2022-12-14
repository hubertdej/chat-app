package com.chat.server.domain.listuserconversations;

import com.chat.server.domain.conversationstorage.dto.ConversationDto;
import com.chat.server.domain.conversationstorage.dto.MessageDto;

import java.util.*;

public class InMemoryUserConversationRepository implements UserConversationRepository {
    private final Map<String, Map<UUID, ConversationDto>> storage;

    public InMemoryUserConversationRepository(Map<String, Map<UUID, ConversationDto>> storage){
       this.storage = storage;
    }

    @Override
    public void add(String username, ConversationDto conversationDto) {
        storage.computeIfAbsent(username, u -> new HashMap<>()).put(conversationDto.getConversationId(), conversationDto);
    }
    @Override
    public void addMessage(MessageDto messageDto, String member) {
        Map<UUID, ConversationDto> conversationDtoMap = storage.getOrDefault(member, new HashMap<>());
        ConversationDto conversationDto = conversationDtoMap.get(messageDto.to());
        if(conversationDto != null) {
            List<MessageDto> messageDtos = new ArrayList<>(conversationDto.getMessages());
            messageDtos.add(messageDto);
            ConversationDto newConversationDto = new ConversationDto(
                    conversationDto.getConversationId(),
                    conversationDto.getName(),
                    conversationDto.getMembers(),
                    messageDtos);
            conversationDtoMap.put(messageDto.to(), newConversationDto);
            storage.put(member, conversationDtoMap);
        }
        else
            System.out.println("WARNING: messageDto " + messageDto + "is refering to unknown conversation");
    }

    @Override
    public void remove(String username, UUID conversationId) {
        storage.computeIfPresent(username,
                (uname, conversations) -> {
                    conversations.remove(conversationId);
                    return conversations;
                });
    }

    @Override
    public List<ConversationDto> get(String username) {
        return storage.getOrDefault(username, Map.of()).values().stream().toList();
    }
}
