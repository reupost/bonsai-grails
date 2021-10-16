package bonsaiapp.dto


class BonsaiDTO {

    Long id
    String name
    String tag
    Long taxonId
    Long userId

    String toString() {
        'DTO: ' + id + ' - [' + tag + '] ' + name + ' (' + taxonId + '), user ' + userId
    }
}