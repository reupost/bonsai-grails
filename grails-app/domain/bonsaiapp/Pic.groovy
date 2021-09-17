package bonsaiapp

import grails.rest.Resource
import bonsaiapp.EntityPics

//@Resource(uri = '/api/pic')
class Pic {

    String filePath
    String title
    Date dateTaken

    static belongsTo = [entityPics:EntityPics]

    static constraints = {
    }
}
