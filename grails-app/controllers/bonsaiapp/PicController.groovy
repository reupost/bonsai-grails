package bonsaiapp

import grails.validation.ValidationException
import javassist.NotFoundException
import org.springframework.web.multipart.commons.CommonsMultipartFile
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest

import static org.springframework.http.HttpStatus.*

class PicController {

    IPicService picService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond picService.list(params), model:[picCount: picService.count()]
    }

    def show(Long id) {
        //respond picService.get(id)
        render(view: 'show', model:[pic: picService.get(id)])
    }

    def viewImage() {
        byte[] image = picService.getImg(params.id)
        response.outputStream << image
    }

    def viewImageThumb() {
        byte[] image = picService.getImgThumb(params.id)
        response.outputStream << image
    }

    def create() {
        render(view: 'create', model:[pic: new Pic(params)])
    }

    def save(Pic pic) {
        if (pic == null) {
            notFound()
            return
        }

        def f = request.getFile('imgFile')

        if (f.empty) {
            flash.message = 'file cannot be empty'
            render(view: 'create')
            return
        }

        try {
            picService.saveWithImg(pic, f)
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

    def update(Long id) {
        Pic pic = picService.get(id)

        if (pic == null) {
            notFound()
            return
        }

        pic.properties = params

        def imgFile = request.getFile('imgFile')

        try {
            picService.saveWithImg(pic, imgFile)
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

        def isDeleted = true
        try {
            picService.delete(id)
        } catch (NotFoundException e) {
            isDeleted = false
        }

        request.withFormat {
            form multipartForm {
                if (isDeleted) {
                    flash.message = message(code: 'default.deleted.message', args: [message(code: 'pic.label', default: 'Pic'), id])
                } else {
                    flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'pic.label', default: 'Pic'), id])
                }
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
