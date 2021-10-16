package bonsaiapp.dto

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer

import java.time.LocalDateTime

class UserDTO {

    Long id
    String userName
    String email
    String bio

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    LocalDateTime dateRegistered

    static constraints = {
        userName nullable: true
        bio nullable: true
    }

    static mapping = {
    }

    String toString() {
        id + ': ' + (userName? userName + ' (' + email + ')' : email)
    }

}
