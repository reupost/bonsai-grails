package bonsaiapp

import grails.rest.Resource
import bonsaiapp.EntityPics

//@Resource(uri = '/api/diary')
class DiaryEntry {

    Date entryDate
    String entryText

    static belongsTo = [bonsai:Bonsai]

    static constraints = {
    }

    static mapping = {
    }

    String toString() {
        entryDate.toString() + ': ' + entryText
    }
}