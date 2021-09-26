package bonsaiapp

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer

import java.time.LocalDateTime

//@Resource(uri = '/api/diary')
class DiaryEntry {

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    LocalDateTime entryDate

    String entryText

    static belongsTo = [bonsai:Bonsai]

    static constraints = {
    }

    static mapping = {
    }

    String toString() {
        entryDate.toString() + ': ' + entryText
    }
}