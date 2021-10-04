package bonsaiapp.dto

class TaxonDTO {

    Long id
    String family
    String genus
    String species
    String cultivar
    String commonName
    String fullName

    static constraints = {
        family nullable: true
        cultivar nullable: true
        commonName nullable: true
    }

    String toString() {
        id + ': ' + (fullName? fullName :
                genus + ' ' + species + (cultivar? " '" + cultivar + "'": '') + (commonName? ' (' + commonName + ')' : '')
        )
    }
}
