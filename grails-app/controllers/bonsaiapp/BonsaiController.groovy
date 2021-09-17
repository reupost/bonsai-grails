package bonsaiapp

import grails.validation.ValidationException
import static org.springframework.http.HttpStatus.*

class BonsaiController {

    IBonsaiService bonsaiService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond bonsaiService.list(params), model:[bonsaiCount: bonsaiService.count()]
    }

    def show(Long id) {
        respond bonsaiService.get(id)
    }

    def create() {
        respond new Bonsai(params)
    }

    def save(Bonsai bonsai) {
        if (bonsai == null) {
            notFound()
            return
        }

        try {
            bonsaiService.save(bonsai)
        } catch (ValidationException e) {
            respond bonsai.errors, view:'create'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'bonsai.label', default: 'Bonsai'), bonsai.id])
                redirect bonsai
            }
            '*' { respond bonsai, [status: CREATED] }
        }
    }

    def edit(Long id) {
        respond bonsaiService.get(id)
    }

    def update(Bonsai bonsai) {
        if (bonsai == null) {
            notFound()
            return
        }

        try {
            bonsaiService.save(bonsai)
        } catch (ValidationException e) {
            respond bonsai.errors, view:'edit'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'bonsai.label', default: 'Bonsai'), bonsai.id])
                redirect bonsai
            }
            '*'{ respond bonsai, [status: OK] }
        }
    }

    def delete(Long id) {
        if (id == null) {
            notFound()
            return
        }

        bonsaiService.delete(id)

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'bonsai.label', default: 'Bonsai'), id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'bonsai.label', default: 'Bonsai'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
