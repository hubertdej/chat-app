package com.chat.server.infrastructure.rest;

import com.chat.server.domain.registration.RegistrationFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@ComponentScan("com.chat.server.domain.registration")
public class UserListController {
    RegistrationFacade registrationFacade;

    @Autowired
    public UserListController(RegistrationFacade registrationFacade){
        this.registrationFacade = registrationFacade;
    }

    @GetMapping("/list-users")
    public List<String> listUsers(){
        return registrationFacade.listUsers();
    }
}
