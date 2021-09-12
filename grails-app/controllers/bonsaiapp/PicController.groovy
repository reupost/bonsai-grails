package bonsaiapp

import grails.validation.ValidationException
import static org.springframework.http.HttpStatus.*

class PicController {

    PicService picService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond picService.list(params), model:[picCount: picService.count()]
    }

    def show(Long id) {
        respond picService.get(id)
    }

    def create() {
        respond new Pic(params)
    }

    def save(Pic pic) {
        if (pic == null) {
            notFound()
            return
        }

        try {
            picService.save(pic)
        } catch (ValidationException e) {
            respond pic.errors, view:'create'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'pic.label', default: 'Pic'), pic.id])
                redirect pic
            }
            '*' { respond pic, [status: CREATED] }
        }
    }

    def edit(Long id) {
        respond picService.get(id)
    }

    def update(Pic pic) {
        if (pic == null) {
            notFound()
            return
        }

        try {
            picService.save(pic)
        } catch (ValidationException e) {
            respond pic.errors, view:'edit'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'pic.label', default: 'Pic'), pic.id])
                redirect pic
            }
            '*'{ respond pic, [status: OK] }
        }
    }

    def delete(Long id) {
        if (id == null) {
            notFound()
            return
        }

        picService.delete(id)

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'pic.label', default: 'Pic'), id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'pic.label', default: 'Pic'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
