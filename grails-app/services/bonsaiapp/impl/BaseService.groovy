package bonsaiapp.impl

import bonsaiapp.Copy
import bonsaiapp.InputCleaner
import bonsaiapp.ResultPage
import grails.core.GrailsApplication
import groovy.json.JsonBuilder
import groovy.json.JsonParserType
import groovy.json.JsonSlurper
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.BlockingHttpClient
import io.micronaut.http.client.HttpClient
import javassist.NotFoundException

class BaseService {

    static String queryUrl
    static BlockingHttpClient client

    private static setServiceTarget(GrailsApplication grailsApplication) {
        queryUrl = grailsApplication.config.bonsaiws.baseurl
        client = HttpClient.create((queryUrl as String).toURL()).toBlocking()
    }

    static String getRestJsonObject(GrailsApplication grailsApplication, String urlPath, Serializable id) {
        setServiceTarget(grailsApplication)
        HttpRequest request = HttpRequest.GET("${urlPath}/${id}")
        HttpResponse<String> resp = client.exchange(request, String)
        client.close()

        resp.body()
    }

    List list(Map args) {
        ResultPage resultPage = pageList(args)
        resultPage.results
    }

    List listAll(Map args) {
        args['offset'] = 0
        args['max'] = 1000 //well, not all but a lot
        ResultPage resultPage = pageList(args)
        resultPage.results
    }

    static Tuple2 getRestJsonList(GrailsApplication grailsApplication, String urlPath, Map args) {
        setServiceTarget(grailsApplication)
        InputCleaner inputCleaner = new InputCleaner()

        Integer offset = (args['offset'] ?: 0) as Integer
        Integer size = (args['max'] ?: 10) as Integer

        def filter = inputCleaner.getOnlyLettersAndNumbers((args['searchFilter'] ?: "").toString())
        filter = URLEncoder.encode(filter, "UTF-8")
        def page = Math.floor(offset/size).toInteger()

        def sort = args['sort'] ?: 'id'
        def dir = args['order'] ?: 'ASC'

        HttpRequest request = HttpRequest.GET("${urlPath}/page?filter=${filter}&page=${page}&size=${size}&sort=${sort}&dir=${dir}")
        HttpResponse<String> resp = client.exchange(request, String)
        client.close()

        String json = resp.body()
        //this is bit of a hacky way of getting around the REST service returning 'content:[array],pageable,etc' instead of just 'array'
        def parser = new JsonSlurper().setType(JsonParserType.LAX)
        def jsonResp = parser.parseText(json)
        def jsonList = new JsonBuilder(jsonResp.content).toPrettyString()
        [jsonList, jsonResp.totalElements]
    }

    static Long getRestCount(GrailsApplication grailsApplication, String urlPath) {
        setServiceTarget(grailsApplication)

        HttpRequest request = HttpRequest.GET("${urlPath}/count")
        HttpResponse<String> resp = client.exchange(request, String)
        client.close()

        resp.body().toLong()
    }

    static void deleteRestObject(GrailsApplication grailsApplication, String urlPath, Serializable id) {
        setServiceTarget(grailsApplication)

        HttpRequest request = HttpRequest.DELETE("${urlPath}/${id}")
        HttpResponse<String> resp = client.exchange(request, String)
        client.close()

        if (resp.getStatus() != HttpStatus.OK) {
            def e = new NotFoundException("${urlPath} with id ${id} could not be deleted")
            throw e
        }
    }

    static HttpResponse<String> saveRestObject(GrailsApplication grailsApplication, String urlPath, GroovyObject object) {
        setServiceTarget(grailsApplication)

        Class objClass = object.getClass()
        String className = objClass.getName()
        String[] qualifiers = className.split('[.]') //should be bonsaiapp.[Class] but need bonsaiapp.dto.[Class]DTO
        String classNameDto = qualifiers[0] + '.dto.' + qualifiers[1] + 'DTO'
        GroovyObject objectInstanceDto = Class.forName(classNameDto).newInstance() as GroovyObject

        Copy.copy(object, objectInstanceDto)
        objectInstanceDto.id = object.getProperty("id") as Long

        HttpRequest request
        if (objectInstanceDto.id == null) {
            //create
            request = HttpRequest.POST("${urlPath}/dto", objectInstanceDto)
        } else {
            //edit
            request = HttpRequest.PUT("${urlPath}/dto", objectInstanceDto)
        }

        HttpResponse<String> resp = client.exchange(request, String)
        client.close()
        resp
    }
}
