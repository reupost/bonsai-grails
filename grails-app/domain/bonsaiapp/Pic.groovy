package bonsaiapp
//@Resource(uri = '/api/pic')
class Pic {

    Long entityId
    String entityType

    String title
    Date dateTaken

    Integer dimx
    Integer dimy
    Integer dimxthumb
    Integer dimythumb

    static constraints = {
        entityId nullable: false
        entityType nullable: false
    }
}
