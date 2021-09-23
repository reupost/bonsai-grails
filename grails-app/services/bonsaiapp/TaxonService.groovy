package bonsaiapp

import bonsaiapp.ITaxonService
import bonsaiapp.InputCleaner
import bonsaiapp.ResultPage
import bonsaiapp.Taxon
import bonsaiapp.TaxonDTO
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

class TaxonService implements ITaxonService {

    def grailsApplication

    @Override
    Taxon get(Serializable id) {
        def queryUrl = grailsApplication.config.bonsaiws.baseurl

        BlockingHttpClient client = HttpClient.create((queryUrl as String).toURL()).toBlocking()

        HttpRequest request = HttpRequest.GET("taxon/${id}")
        HttpResponse<String> resp = client.exchange(request, String)
        client.close()

        JsonToObject.fromJson(resp.body(), new TypeReference<Taxon>(){})
    }

    @Override
    List<Taxon> list(Map args) {
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

        def sort = args['sort']
        //if (sort == 'taxon') sort = 'taxon.fullName' //do not sort by taxon id
        def dir = args['order']

        HttpRequest request = HttpRequest.GET("taxon/page?filter=${filter}&page=${page}&sort=${sort}&dir=${dir}")
        HttpResponse<String> resp = client.exchange(request, String)
        client.close()

        String json = resp.body()
        //this is bit of a hacky way of getting around the REST service returning 'content:[array]' instead of just 'array'
        def parser = new JsonSlurper().setType(JsonParserType.LAX)
        def jsonResp = parser.parseText(json)
        def jsonTaxonList = new JsonBuilder(jsonResp.content).toPrettyString()

        List<Taxon> taxonList = JsonToObject.fromJson(jsonTaxonList, new TypeReference<List<Taxon>>(){})

        resultPage.results = taxonList
        resultPage.pageModel = [taxonCount: jsonResp.totalElements]
        resultPage
    }

    @Override
    Long count() {
        def queryUrl = grailsApplication.config.bonsaiws.baseurl

        BlockingHttpClient client = HttpClient.create((queryUrl as String).toURL()).toBlocking()

        HttpRequest request = HttpRequest.GET("taxon/count")
        HttpResponse<String> resp = client.exchange(request, String)
        client.close()

        resp.body().toLong()
    }

    @Override
    void delete(Serializable id) {
        def queryUrl = grailsApplication.config.bonsaiws.baseurl

        BlockingHttpClient client = HttpClient.create((queryUrl as String).toURL()).toBlocking()

        HttpRequest request = HttpRequest.DELETE("taxon/${id}")
        HttpResponse<String> resp = client.exchange(request, String)
        client.close()

        if (resp.getStatus() != HttpStatus.OK) {
            def e = new NotFoundException("Taxon with id ${id} could not be deleted")
            throw e
        }
    }

    @Override
    Taxon save(Taxon taxon) {
        def queryUrl = grailsApplication.config.bonsaiws.baseurl

        BlockingHttpClient client = HttpClient.create((queryUrl as String).toURL()).toBlocking()

        TaxonDTO taxonDTO = new TaxonDTO()
        Copy.copy(taxon, taxonDTO)
        taxonDTO.id = taxon.getProperty("id") as Long

        HttpRequest request = HttpRequest.PUT("taxon/dto", taxonDTO)
        HttpResponse<String> resp = client.exchange(request, String)
        client.close()

        if (resp.getStatus() == HttpStatus.OK) {
            //TODO refactor: how much do we need the DTO if we end up resorting to parsing raw json?
            JsonToObject.fromJson(resp.body(), new TypeReference<Taxon>(){})
        } else {
            null
        }
    }

}
