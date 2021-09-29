package bonsaiapp

import com.fasterxml.jackson.core.type.TypeReference
import groovy.json.JsonBuilder
import groovy.json.JsonParserType
import groovy.json.JsonSlurper
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.BlockingHttpClient
import io.micronaut.http.client.HttpClient
import javassist.NotFoundException

class PicService implements IPicService {

    def grailsApplication

    @Override
    Pic get(Serializable id) {
        def queryUrl = grailsApplication.config.bonsaiws.baseurl

        BlockingHttpClient client = HttpClient.create((queryUrl as String).toURL()).toBlocking()

        HttpRequest request = HttpRequest.GET("pic/${id}")
        HttpResponse<String> resp = client.exchange(request, String)
        client.close()

        Pic pic = JsonToObject.fromJson(resp.body(), new TypeReference<Pic>() {})
        pic
    }

    //note: not part of interface
    byte[] getImg(Serializable id) {
        def queryUrl = grailsApplication.config.bonsaiws.baseurl

        BlockingHttpClient client = HttpClient.create((queryUrl as String).toURL()).toBlocking()

        HttpRequest requestImg = HttpRequest.GET("pic/image/${id}")
        HttpResponse<byte[]> respImg = client.exchange(requestImg, byte[])
        client.close()

        byte[] img = respImg.body()
        img
    }

    //note: not part of interface
    byte[] getImgThumb(Serializable id) {
        def queryUrl = grailsApplication.config.bonsaiws.baseurl

        BlockingHttpClient client = HttpClient.create((queryUrl as String).toURL()).toBlocking()

        HttpRequest requestImg = HttpRequest.GET("pic/thumb/${id}")
        HttpResponse<byte[]> respImg = client.exchange(requestImg, byte[])
        client.close()

        byte[] img = respImg.body()
        img
    }

    @Override
    List<Pic> list(Map args) {
        ResultPage resultPage = pageList(args)
        resultPage.results
    }

    @Override
    List<Pic> listAll(Map args) {
        args['offset'] = 0
        args['max'] = 9999;
        ResultPage resultPage = pageList(args)
        resultPage.results
    }

    ResultPage pageList(Map args) {

        ResultPage resultPage = new ResultPage()
        InputCleaner inputCleaner = new InputCleaner()

        def queryUrl = grailsApplication.config.bonsaiws.baseurl

        BlockingHttpClient client = HttpClient.create((queryUrl as String).toURL()).toBlocking()

        Integer offset = (args['offset'] ?: 0) as Integer
        Integer size = (args['max'] ?: 10) as Integer

        def filter = inputCleaner.getOnlyLettersAndNumbers((args['searchFilter'] ?: "").toString())
        filter = URLEncoder.encode(filter, "UTF-8")
        def page = Math.floor(offset / size).toInteger()

        def sort = args['sort'] ?: 'tag'
        //if (sort == 'taxon') sort = 'taxon.fullName' //do not sort by taxon id
        def dir = args['order'] ?: 'ASC'

        HttpRequest request = HttpRequest.GET("pic/page?filter=${filter}&page=${page}&size=${size}&sort=${sort}&dir=${dir}")
        HttpResponse<String> resp = client.exchange(request, String)
        client.close()

        String json = resp.body()
        //this is bit of a hacky way of getting around the REST service returning 'content:[array]' instead of just 'array'
        def parser = new JsonSlurper().setType(JsonParserType.LAX)
        def jsonResp = parser.parseText(json)
        def jsonPicList = new JsonBuilder(jsonResp.content).toPrettyString()

        List<Pic> picList = JsonToObject.fromJson(jsonPicList, new TypeReference<List<Pic>>() {})

        resultPage.results = picList
        resultPage.pageModel = [picCount: jsonResp.totalElements]
        resultPage
    }

    @Override
    Long count() {
        def queryUrl = grailsApplication.config.bonsaiws.baseurl

        BlockingHttpClient client = HttpClient.create((queryUrl as String).toURL()).toBlocking()

        HttpRequest request = HttpRequest.GET("pic/count")
        HttpResponse<String> resp = client.exchange(request, String)
        client.close()

        resp.body().toLong()
    }

    @Override
    void delete(Serializable id) {
        def queryUrl = grailsApplication.config.bonsaiws.baseurl

        BlockingHttpClient client = HttpClient.create((queryUrl as String).toURL()).toBlocking()

        HttpRequest request = HttpRequest.DELETE("pic/${id}")
        HttpResponse<String> resp = client.exchange(request, String)
        client.close()

        if (resp.getStatus() != HttpStatus.OK) {
            def e = new NotFoundException("Pic with id ${id} could not be deleted")
            throw e
        }
    }

    @Override
    Pic save(Pic pic) {
        return null
    }
//    @Override
//    Pic save(Pic pic) {
//        def queryUrl = grailsApplication.config.bonsaiws.baseurl
//
//        BlockingHttpClient client = HttpClient.create((queryUrl as String).toURL()).toBlocking()
//
//        PicDTO picDTO = new PicDTO()
//        Copy.copy(pic, picDTO)
//        picDTO.id = pic.getProperty("id") as Long
//
//        HttpRequest request
//        if (picDTO.id == null) {
//            //create
//            request = HttpRequest.POST("pic/dto", picDTO)
//        } else {
//            //edit
//            request = HttpRequest.PUT("pic/dto", picDTO)
//        }
//
//        HttpResponse<String> resp = client.exchange(request, String)
//        client.close()
//
//        if (resp.getStatus() == HttpStatus.OK) {
//            //TODO refactor: how much do we need the DTO if we end up resorting to parsing raw json?
//            PicDTO savedPicDTO = JsonToObject.fromJson(resp.body(), new TypeReference<PicDTO>(){})
//            Copy.copy(savedPicDTO, pic)
//            pic.setProperty("id", savedPicDTO.getId())
//            pic
//        } else {
//            null
//        }
//    }
}
