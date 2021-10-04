package bonsaiapp.impl

import bonsaiapp.Bonsai
import bonsaiapp.IBonsaiService
import bonsaiapp.ITaxonService
import bonsaiapp.InputCleaner
import bonsaiapp.ResultPage
import bonsaiapp.dto.BonsaiDTO
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

class BonsaiService implements IBonsaiService {

    def grailsApplication

    def ITaxonService taxonService

    @Override
    Bonsai get(Serializable id) {
        def queryUrl = grailsApplication.config.bonsaiws.baseurl

        BlockingHttpClient client = HttpClient.create((queryUrl as String).toURL()).toBlocking()

        HttpRequest request = HttpRequest.GET("bonsai/${id}")
        HttpResponse<String> resp = client.exchange(request, String)
        client.close()

        Bonsai x = JsonToObject.fromJson(resp.body(), new TypeReference<Bonsai>(){})
        x
    }

    @Override
    List<Bonsai> list(Map args) {
        ResultPage resultPage = pageList(args)
        resultPage.results
    }

    @Override
    List<Bonsai> listAll(Map args) {
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
        def page = Math.floor(offset/size).toInteger()

        def sort = args['sort'] ?: 'tag'
        if (sort == 'taxon') sort = 'taxon.fullName' //do not sort by taxon id
        def dir = args['order'] ?: 'ASC'

        HttpRequest request = HttpRequest.GET("bonsai/page?filter=${filter}&page=${page}&size=${size}&sort=${sort}&dir=${dir}")
        HttpResponse<String> resp = client.exchange(request, String)
        client.close()

        String json = resp.body()
        //this is bit of a hacky way of getting around the REST service returning 'content:[array]' instead of just 'array'
        def parser = new JsonSlurper().setType(JsonParserType.LAX)
        def jsonResp = parser.parseText(json)
        def jsonBonsaiList = new JsonBuilder(jsonResp.content).toPrettyString()

        List<Bonsai> bonsaiList = JsonToObject.fromJson(jsonBonsaiList, new TypeReference<List<Bonsai>>(){})

        resultPage.results = bonsaiList
        resultPage.pageModel = [bonsaiCount: jsonResp.totalElements]
        resultPage
    }

    @Override
    Long count() {
        def queryUrl = grailsApplication.config.bonsaiws.baseurl

        BlockingHttpClient client = HttpClient.create((queryUrl as String).toURL()).toBlocking()

        HttpRequest request = HttpRequest.GET("bonsai/count")
        HttpResponse<String> resp = client.exchange(request, String)
        client.close()

        resp.body().toLong()
    }

    @Override
    void delete(Serializable id) {
        def queryUrl = grailsApplication.config.bonsaiws.baseurl

        BlockingHttpClient client = HttpClient.create((queryUrl as String).toURL()).toBlocking()

        HttpRequest request = HttpRequest.DELETE("bonsai/${id}")
        HttpResponse<String> resp = client.exchange(request, String)
        client.close()

        if (resp.getStatus() != HttpStatus.OK) {
            def e = new NotFoundException("Bonsai with id ${id} could not be deleted")
            throw e
        }
    }

    @Override
    Bonsai save(Bonsai bonsai) {
        def queryUrl = grailsApplication.config.bonsaiws.baseurl

        BlockingHttpClient client = HttpClient.create((queryUrl as String).toURL()).toBlocking()

        BonsaiDTO bonsaiDTO = new BonsaiDTO()
        Copy.copy(bonsai, bonsaiDTO)
        bonsaiDTO.id = bonsai.getProperty("id") as Long

        HttpRequest request
        if (bonsaiDTO.id == null) {
            //create
            request = HttpRequest.POST("bonsai/dto", bonsaiDTO)
        } else {
            //edit
            request = HttpRequest.PUT("bonsai/dto", bonsaiDTO)
        }

        HttpResponse<String> resp = client.exchange(request, String)
        client.close()

        if (resp.getStatus() == HttpStatus.OK) {
            //TODO refactor: how much do we need the DTO if we end up resorting to parsing raw json?
            BonsaiDTO savedBonsaiDTO = JsonToObject.fromJson(resp.body(), new TypeReference<BonsaiDTO>(){})
            Copy.copy(savedBonsaiDTO, bonsai)
            bonsai.setProperty("id", savedBonsaiDTO.getId())
            bonsai
        } else {
            null
        }
    }
}
