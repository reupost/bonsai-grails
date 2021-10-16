package bonsaiapp

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer

import java.time.LocalDateTime

class User {

    String userName
    String email
    String bio

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    LocalDateTime dateRegistered

    static hasMany = [bonsais: Bonsai]

    static constraints = {
        userName nullable: true
        bio nullable: true
    }

    static mapping = {
    }

    String toString() {
        userName? userName + ' (' + email + ')' : email
    }
}
