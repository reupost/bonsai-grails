package bonsaiapp

import grails.rest.Resource
import bonsaiapp.EntityPics

//@Resource(uri = '/api/diary')
class DiaryEntry {

    Date date
    String notes

    static belongsTo = [bonsai:Bonsai]

    static constraints = {
    }

    static mapping = {
    }

    String toString() {
        date + ': ' + notes
    }
}