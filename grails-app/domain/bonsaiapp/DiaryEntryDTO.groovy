package bonsaiapp

class DiaryEntryDTO {

    Long id
    Date entryDate
    String entryText
    Long bonsaiId

    String toString() {
        id + ': [' + bonsaiId + '] (' + entryDate.toString() + '): ' + entryText
    }
}