package bonsaiapp


//@Resource(uri = '/api/taxon')
class Taxon {

    String family
    String genus
    String species
    String cultivar
    String commonName
    String fullName

    static hasMany = [bonsais: Bonsai]

    static constraints = {
        family nullable: true
        cultivar nullable: true
        commonName nullable: true
    }

    static mapping = {
    }

    String toString() {
        (fullName? fullName :
                genus + ' ' + species + (cultivar? " '" + cultivar + "'": '') + (commonName? ' (' + commonName + ')' : '')
        )
    }
}
