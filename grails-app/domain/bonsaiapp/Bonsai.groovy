package bonsaiapp


//@Resource(uri = '/api/bonsai')
//@CompileStatic
class Bonsai {

    String name
    String tag

    static belongsTo = [taxon: Taxon]
    static hasMany = [diaryEntries: DiaryEntry]

    static constraints = {
        name nullable: true
    }

    static mapping = {
    }

    String toString() {
        tag + ' ' + taxon
    }
}