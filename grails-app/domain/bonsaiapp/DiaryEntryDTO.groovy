package bonsaiapp

import java.time.LocalDateTime

class DiaryEntryDTO {

    Long id
    LocalDateTime entryDate
    String entryText
    Long bonsaiId

    String toString() {
        id + ': [' + bonsaiId + '] (' + entryDate.toString() + '): ' + entryText
    }
}