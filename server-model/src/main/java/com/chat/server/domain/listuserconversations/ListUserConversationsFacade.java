package com.chat.server.domain.listuserconversations;

import com.chat.server.domain.conversationstorage.ConversationStorageFacade;
import com.chat.server.domain.conversationstorage.dto.ConversationDto;
import com.chat.server.domain.conversationstorage.dto.ConversationUpdatedEvent;
import com.chat.server.domain.conversationstorage.dto.MessageDto;
import com.chat.server.domain.listuserconversations.dto.ListConversationsRequestDto;
import com.chat.server.domain.conversationstorage.dto.ConversationRemovedEvent;

import java.sql.Timestamp;
import java.util.*;

public class ListUserConversationsFacade {
    private final UserConversationRepository userConversationRepository;

    public ListUserConversationsFacade(
            UserConversationRepository userConversationRepository,
            ConversationStorageFacade conversationStorageFacade) {
        this.userConversationRepository = userConversationRepository;
        conversationStorageFacade.addObserver(new ConversationEventHandler());
    }

    List<ConversationDto> listConversations(String username){
        return userConversationRepository.get(username);
    }

    public List<ConversationDto> listConversations(ListConversationsRequestDto listConversationsRequestDto) {
        List<ConversationDto> conversationDtos = listConversations(listConversationsRequestDto.from());
        if(listConversationsRequestDto.lastMessage().isEmpty())
            return conversationDtos;
        List<ConversationDto> conversationDtosFromTimestamp = new ArrayList<>();
        Map<UUID, Timestamp> lastMessages = listConversationsRequestDto.lastMessage();
        for(ConversationDto conversationDto : conversationDtos){
            if(lastMessages.containsKey(conversationDto.getConversationId())){
                List<MessageDto> messageDtos = conversationDto.getMessages();
                List<MessageDto> messageDtosFromTimestamp = getFromTimestamp(messageDtos, lastMessages.get(conversationDto.getConversationId()));
                conversationDtosFromTimestamp.add(new ConversationDto(conversationDto.getConversationId(), conversationDto.getName(), conversationDto.getMembers(), messageDtosFromTimestamp));
            }
            else
                conversationDtosFromTimestamp.add(conversationDto);
        }
        return conversationDtosFromTimestamp;
    }

    public List<MessageDto> listMessages(ListConversationsRequestDto listConversationsRequestDto){
        List<ConversationDto> conversationDtos = listConversations(listConversationsRequestDto);
        return conversationDtos.stream().map(ConversationDto::getMessages).flatMap(Collection::stream).toList();
    }

    private List<MessageDto> getFromTimestamp(List<MessageDto> messageDtos, Timestamp timestamp) {
        int n = messageDtos.size();
        int lo = 0;
        int hi = n-1;
        int ans = -1;
        while(lo < hi){
            int m = (lo + hi) / 2;
            Timestamp curTimestamp = messageDtos.get(m).timestamp();
            if(curTimestamp.compareTo(timestamp) == 0){
                return m+1 <= n-1 ? messageDtos.subList(m+1, n) : List.of();
            }
            else if(curTimestamp.compareTo(timestamp) < 0){
                lo = m+1;
            }
            else{
                hi = m-1;
                ans = m;
            }
        }
        return ans+1 < n-1 ? messageDtos.subList(ans+1, n-1) : List.of();
    }

    private void add(String username, ConversationDto conversationDto){
        userConversationRepository.add(username, conversationDto);
    }

    private void remove(String username, UUID conversationId){
        userConversationRepository.remove(username, conversationId);
    }

    private class ConversationEventHandler implements ConversationStorageFacade.ConversationObserver {

        @Override
        public void notifyUpdate(ConversationUpdatedEvent event) {
            if(event.isNewConversation()) {
                ConversationDto conversationDto = new ConversationDto(
                        event.conversationId(),
                        event.name(),
                        event.members(),
                        new ArrayList<>());
                event.members().forEach(username -> add(username, conversationDto));
            } else {
                event.members().forEach(username -> userConversationRepository.addMessage(event.newMessageDto(), username));
            }
        }

        @Override
        public void notifyRemove(ConversationRemovedEvent event) {
            event.members().forEach(username -> remove(username, event.conversationId()));
        }
    }
}
