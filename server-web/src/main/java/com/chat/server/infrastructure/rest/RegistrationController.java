package com.chat.server.infrastructure.rest;

import com.chat.server.domain.registration.RegistrationFacade;
import com.chat.server.domain.registration.dto.UsernameTakenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ComponentScan("com.chat.server.domain.registration")
public class RegistrationController {

    private final RegistrationFacade registrationFacade;

    @Autowired
    public RegistrationController(RegistrationFacade registrationFacade){
        this.registrationFacade = registrationFacade;
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> register(@RequestBody User user) {
        try {
            registrationFacade.register(user.dto());
        } catch (UsernameTakenException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
        return ResponseEntity.ok().build();
    }
}
