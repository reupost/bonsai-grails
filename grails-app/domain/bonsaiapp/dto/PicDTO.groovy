package bonsaiapp

class PicDTO {

    Long entityId
    String entityType

    String filePath
    String title
    Date dateTaken

    Integer dimx
    Integer dimy
    Integer dimxthumb
    Integer dimythumb

    static constraints = {
    }
}
