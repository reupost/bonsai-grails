package bonsaiapp

import grails.rest.Resource

//@Resource(uri = '/api/pic')
class Pic {

    static belongsTo = [entityWithPic:EntityWithPic]
    String filePath

    static constraints = {
    }
}
