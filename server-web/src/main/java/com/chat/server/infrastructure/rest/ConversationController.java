package com.chat.server.infrastructure.rest;

import com.chat.server.domain.authentication.AuthenticationFacade;
import com.chat.server.domain.conversationstorage.ConversationStorageFacade;
import com.chat.server.domain.conversationstorage.dto.ConversationDto;
import com.chat.server.domain.listconversationids.ListConversationIdsFacade;
import com.chat.server.domain.listconversationids.dto.ConversationPreviewDto;
import com.chat.server.infrastructure.rest.dto.AddConversationRequestDto;
import com.chat.server.infrastructure.rest.dto.GetConversationRequestDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@ComponentScan({"com.chat.server.domain.conversationstorage",
        "com.chat.server.domain.listconversationids",
        "com.chat.server.domain.registration",
        "com.chat.server.domain.authentication"})
public class ConversationController {
    private final ConversationStorageFacade conversationStorageFacade;
    private final ListConversationIdsFacade listConversationIdsFacade;
    private final AuthenticationFacade authenticationFacade;
    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    public ConversationController(ConversationStorageFacade conversationStorageFacade,
                                  ListConversationIdsFacade listConversationIdsFacade,
                                  AuthenticationFacade authenticationFacade) {
        this.conversationStorageFacade = conversationStorageFacade;
        this.listConversationIdsFacade = listConversationIdsFacade;
        this.authenticationFacade = authenticationFacade;
    }

    @RequestMapping(value = "/add-conversation", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<String> addConversation(@RequestBody AddConversationRequestDto request){
        if(request.name() == null || request.name().isEmpty()
                || request.members() == null || request.members().isEmpty())
            return ResponseEntity.badRequest().build();
        UUID conversationId = conversationStorageFacade.add(request.name(), request.members());
        return ResponseEntity.ok(conversationId.toString());
    }

    @RequestMapping(value = "/list-conversation-previews", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> listConversationPreviews(@RequestBody User user){
        if(!authenticationFacade.authenticate(user.getUsername(), user.getPassword()))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        List<ConversationPreviewDto> previews = listConversationIdsFacade.listConversationPreviews(user.getUsername());
        try{
            String marshalled = mapper.writeValueAsString(previews);
            return ResponseEntity.ok(marshalled);
        } catch(JsonProcessingException e){
            return ResponseEntity.internalServerError().build();
        }
    }

    @RequestMapping(value = "/get-conversation", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getConversation(@RequestBody GetConversationRequestDto getConversationRequestDto){
        if(!authenticationFacade.authenticate(getConversationRequestDto.username(), getConversationRequestDto.password()))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        Optional<ConversationDto> conversationDtoOptional = conversationStorageFacade.get(getConversationRequestDto.conversationId());
        if(conversationDtoOptional.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        ConversationDto conversationDto = conversationDtoOptional.get();
        try{
            String marshalled = mapper.writeValueAsString(conversationDto);
            return ResponseEntity.ok(marshalled);
        } catch(JsonProcessingException e){
            return ResponseEntity.internalServerError().build();
        }
    }
}
