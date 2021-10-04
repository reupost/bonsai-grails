package bonsaiapp.dto


class BonsaiDTO {

    Long id
    String name
    String tag
    Long taxonId

    String toString() {
        'DTO: ' + id + ' - [' + tag + '] ' + name + ' (' + taxonId + ')'
    }
}