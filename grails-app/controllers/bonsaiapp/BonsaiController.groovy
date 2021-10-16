package bonsaiapp

import grails.validation.ValidationException
import javassist.NotFoundException

import static org.springframework.http.HttpStatus.*

class BonsaiController {

    IBonsaiService bonsaiService
    ITaxonService taxonService
    IUserService userService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)

        ResultPage resultPage = bonsaiService.pageList(params)

        respond resultPage.results, model: resultPage.pageModel
    }

    def show(Long id) {
        respond bonsaiService.get(id)
    }

    def create()     {
        render(view: 'create', model:[bonsai: new Bonsai(params), taxonService: taxonService, userService: userService])
    }

    def save(Bonsai bonsai) {
        if (bonsai == null) {
            notFound()
            return
        }
        if (bonsai.getProperty("taxon") == null) { // when create this is null
            bonsai.setProperty("taxon", taxonService.get(params.taxon?:0))
        }
        if (bonsai.getProperty("user") == null) { // when create this is null
            bonsai.setProperty("user", userService.get(params.user?:0))
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
        render(view: 'edit', model:[bonsai: bonsaiService.get(id), taxonService: taxonService, userService: userService])
    }

    def update(Long id) {
        Bonsai bonsai = bonsaiService.get(id)

        if (bonsai == null) {
            notFound()
            return
        }

        bonsai.properties = params
        //bonsai.setProperty("taxon", taxonService.get(params.taxon)) //params.taxon = taxon id


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

        def isDeleted = true
        try {
            bonsaiService.delete(id)
        } catch (NotFoundException e) {
            isDeleted = false
        }

        request.withFormat {
            form multipartForm {
                if (isDeleted) {
                    flash.message = message(code: 'default.deleted.message', args: [message(code: 'bonsai.label', default: 'Bonsai'), id])
                } else {
                    flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'bonsai.label', default: 'Bonsai'), id])
                }
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
