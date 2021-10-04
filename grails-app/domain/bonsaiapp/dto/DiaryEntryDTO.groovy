package bonsaiapp.dto

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer

import java.time.LocalDateTime

class DiaryEntryDTO {

    Long id

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    LocalDateTime entryDate

    String entryText
    Long bonsaiId

    String toString() {
        id + ': [' + bonsaiId + '] (' + entryDate.toString() + '): ' + entryText
    }
}