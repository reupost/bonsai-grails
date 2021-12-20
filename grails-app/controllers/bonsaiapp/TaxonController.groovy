package bonsaiapp

import grails.validation.ValidationException
import javassist.NotFoundException
import static org.springframework.http.HttpStatus.*

class TaxonController {

    TaxonService taxonService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond taxonService.list(params), model:[taxonCount: taxonService.count()]
    }

    def show(Long id) {
        respond taxonService.get(id)
    }

    def create() {
        respond new Taxon(params)
    }

    def save(Taxon taxon) {
        if (taxon == null) {
            notFound()
            return
        }

        try {
            taxonService.save(taxon)
        } catch (ValidationException e) {
            respond taxon.errors, view:'create'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'taxon.label', default: 'Taxon'), taxon.id])
                redirect taxon
            }
            '*' { respond taxon, [status: CREATED] }
        }
    }

    def edit(Long id) {
        respond taxonService.get(id)
    }

    def update(Long id) {
        Taxon taxon = taxonService.get(id)

        if (taxon == null) {
            notFound()
            return
        }

        taxon.properties = params

        try {
            taxonService.save(taxon)
        } catch (ValidationException e) {
            respond taxon.errors, view:'edit'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'taxon.label', default: 'Taxon'), taxon.id])
                redirect taxon
            }
            '*'{ respond taxon, [status: OK] }
        }
    }

    def delete(Long id) {
        if (id == null) {
            notFound()
            return
        }

        taxonService.delete(id)

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'taxon.label', default: 'Taxon'), id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'taxon.label', default: 'Taxon'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
