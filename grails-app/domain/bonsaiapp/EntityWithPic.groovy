package bonsaiapp

abstract class EntityWithPic {

    static hasMany = [pics:Pic]

    static constraints = {
    }

    static mapping = {
        tablePerConcreteClass true
        tablePerHierarchy false
        id generator: 'increment' // https://jira.grails.org/browse/GRAILS-10849

    }

    String toString() {
    }
}
