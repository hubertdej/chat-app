package com.chat.server.registration

import com.chat.server.registration.dto.UserDto
import com.chat.server.registration.dto.UsernameTakenException
import spock.lang.Specification

class RegistrationSpec extends Specification{
    RegistrationFacade registrationFacade = new RegistrationConfiguration().registrationFacade()

    def "should return credentials for registered user"() {
        given: "John is registered with password: 'johnpass'"
        registrationFacade.register(new UserDto("John", "johnpass"))

        when: "user asks for John credentials"
        String credentials = registrationFacade.getCredentials("John").get()

        then: "module returns 'johnpass'"
        credentials == "johnpass"
    }

    def "should return empty optional for unregistered user"(){
        given: "John is not registered"

        when: "user asks for John's credentials"
        Optional<String> credentials = registrationFacade.getCredentials("John")

        then: "module returns empty optional"
        credentials.isEmpty()
    }

    def "should throw exception if username is taken"(){
        given: "John is registered"
        registrationFacade.register(new UserDto("John", "johnpass"))

        when: "User tries to register another John"
        registrationFacade.register(new UserDto("John", "johnpass2"))

        then: "UsernameTakenException is thrown"
        thrown UsernameTakenException
    }
}

