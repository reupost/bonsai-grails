package bonsaiapp


class BonsaiDTO {

    Long id
    String name
    String tag
    Integer taxonId

    String toString() {
        'DTO: ' + id + ' - [' + tag + '] ' + name + ' (' + taxonId + ')'
    }
}