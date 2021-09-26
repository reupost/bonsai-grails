package bonsaiapp

import grails.rest.Resource
import bonsaiapp.EntityPics

import java.time.LocalDateTime

//@Resource(uri = '/api/diary')
class DiaryEntry {

    LocalDateTime entryDate
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