package bonsaiapp

import grails.validation.ValidationException
import static org.springframework.http.HttpStatus.*

class DiaryEntryController {

    IDiaryEntryService diaryEntryService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond diaryEntryService.list(params), model:[diaryEntryCount: diaryEntryService.count()]
    }

    def show(Long id) {
        respond diaryEntryService.get(id)
    }

    def create() {
        respond new DiaryEntry(params)
    }

    def save(DiaryEntry diaryEntry) {
        if (diaryEntry == null) {
            notFound()
            return
        }

        try {
            diaryEntryService.save(diaryEntry)
        } catch (ValidationException e) {
            respond diaryEntry.errors, view:'create'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'diaryEntry.label', default: 'DiaryEntry'), diaryEntry.id])
                redirect diaryEntry
            }
            '*' { respond diaryEntry, [status: CREATED] }
        }
    }

    def edit(Long id) {
        DiaryEntry diaryEntry = diaryEntryService.get(id)
        render(view: 'edit', model:[diaryEntry: diaryEntry])
    }

    def update(Long id) {
        DiaryEntry diaryEntry = diaryEntryService.get(id)

        if (diaryEntry == null) {
            notFound()
            return
        }

        diaryEntry.properties = params

        try {
            diaryEntryService.save(diaryEntry)
        } catch (ValidationException e) {
            respond diaryEntry.errors, view:'edit'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'diaryEntry.label', default: 'DiaryEntry'), diaryEntry.id])
                redirect diaryEntry
            }
            '*'{ respond diaryEntry, [status: OK] }
        }
    }

    def delete(Long id) {
        if (id == null) {
            notFound()
            return
        }

        diaryEntryService.delete(id)

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'diaryEntry.label', default: 'DiaryEntry'), id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'diaryEntry.label', default: 'DiaryEntry'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
