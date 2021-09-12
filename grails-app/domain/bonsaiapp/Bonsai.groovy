package bonsaiapp

import grails.rest.Resource

//@Resource(uri = '/api/bonsai')
class Bonsai extends EntityWithPic {

    static belongsTo = [taxon:Taxon]
    static hasMany = [diaryEntries: DiaryEntry]

    String name
    String tag

    static constraints = {
        name nullable: true
    }

    static mapping = {
    }

    String toString() {
        tag + ' ' + taxon
    }
}