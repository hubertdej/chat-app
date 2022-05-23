package com.chat.server.infrastructure.rest;

import com.chat.server.registration.RegistrationFacade;
import com.chat.server.registration.dto.UsernameTakenException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@org.springframework.web.bind.annotation.RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RestController {
    RegistrationFacade registrationFacade;

    @Autowired
    public RestController(RegistrationFacade registrationFacade){
        this.registrationFacade = registrationFacade;
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            registrationFacade.register(user.dto());
        } catch (UsernameTakenException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
        return ResponseEntity.ok().build();
    }
}
