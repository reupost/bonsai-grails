package bonsaiapp

import grails.rest.Resource

//@Resource(uri = '/api/diary')
class DiaryEntry extends EntityWithPic {

    static belongsTo = [bonsai:Bonsai]

    Date date
    String notes

    static constraints = {
    }

    static mapping = {
    }

    String toString() {
        date + ': ' + notes
    }
}