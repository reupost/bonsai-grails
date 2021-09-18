package bonsaiapp

import grails.validation.ValidationException
import static org.springframework.http.HttpStatus.*

class EntityPicsController {

    EntityPicsService entityPicsService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond entityPicsService.list(params), model:[entityPicsCount: entityPicsService.count()]
    }

    def show(Long id) {
        respond entityPicsService.get(id)
    }

    def create() {
        respond new EntityPics(params)
    }

    def save(EntityPics entityPics) {
        if (entityPics == null) {
            notFound()
            return
        }

        try {
            entityPicsService.save(entityPics)
        } catch (ValidationException e) {
            respond entityPics.errors, view:'create'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'entityPics.label', default: 'EntityPics'), entityPics.id])
                redirect entityPics
            }
            '*' { respond entityPics, [status: CREATED] }
        }
    }

    def edit(Long id) {
        respond entityPicsService.get(id)
    }

    def update(EntityPics entityPics) {
        if (entityPics == null) {
            notFound()
            return
        }

        try {
            entityPicsService.save(entityPics)
        } catch (ValidationException e) {
            respond entityPics.errors, view:'edit'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'entityPics.label', default: 'EntityPics'), entityPics.id])
                redirect entityPics
            }
            '*'{ respond entityPics, [status: OK] }
        }
    }

    def delete(Long id) {
        if (id == null) {
            notFound()
            return
        }

        entityPicsService.delete(id)

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'entityPics.label', default: 'EntityPics'), id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'entityPics.label', default: 'EntityPics'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
