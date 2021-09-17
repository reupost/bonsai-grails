package bonsaiapp

class EntityPics {

    Long entityId
    String entityType

    static hasMany = [pics:Pic]

    static constraints = {
    }

    static mapping = {
    }

    String toString() {
    }
}
