package bonsaiapp

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import groovy.json.JsonBuilder
import groovy.json.JsonParserType
import groovy.json.JsonSlurper
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.BlockingHttpClient
import io.micronaut.http.client.HttpClient
import javassist.NotFoundException

public class BonsaiService implements IBonsaiService {

    def grailsApplication

    @Override
    public Bonsai get(Serializable id) {
        def queryUrl = grailsApplication.config.bonsaiws.baseurl

        BlockingHttpClient client = HttpClient.create(queryUrl.toURL()).toBlocking()

        HttpRequest request = HttpRequest.GET("bonsai/${id}")
        HttpResponse<String> resp = client.exchange(request, String)
        client.close()

        String json = resp.body()
        ObjectMapper objectMapper = new ObjectMapper()
        objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        objectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        Bonsai searchResult = objectMapper.readValue(json, Bonsai)
        searchResult
    }

    @Override
    public List<Bonsai> list(Map args) {
        ResultPage resultPage = pageList(args)
        resultPage.results
    }

    public ResultPage pageList(Map args) {

        ResultPage resultPage = new ResultPage()
        InputCleaner inputCleaner = new InputCleaner()

        def queryUrl = grailsApplication.config.bonsaiws.baseurl

        BlockingHttpClient client = HttpClient.create(queryUrl.toURL()).toBlocking()

        Integer offset = (args['offset'] ?: 0) as Integer
        Integer size = (args['max'] ?: 10) as Integer

        def filter = inputCleaner.getOnlyLettersAndNumbers((args['searchFilter'] ?: "").toString())
        filter = URLEncoder.encode(filter, "UTF-8")
        def page = Math.floor(offset/size).toInteger()

        def sort = args['sort']
        if (sort == 'taxon') sort = 'taxon.fullName' //do not sort by taxon id
        def dir = args['order']

        HttpRequest request = HttpRequest.GET("bonsai/page?filter=${filter}&page=${page}&sort=${sort}&dir=${dir}")
        HttpResponse<String> resp = client.exchange(request, String)
        client.close()

        String json = resp.body()
        //this is bit of a hacky way of getting around the REST service returning 'content:[array]' instead of just 'array'
        def parser = new JsonSlurper().setType(JsonParserType.LAX)
        def jsonResp = parser.parseText(json)
        def jsonBonsaiList = new JsonBuilder(jsonResp.content).toPrettyString()

        ObjectMapper objectMapper = new ObjectMapper()
        objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        objectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

        List<Bonsai> bonsaiList = objectMapper.readValue(jsonBonsaiList, new TypeReference<List<Bonsai>>(){})

        resultPage.results = bonsaiList
        resultPage.pageModel = [bonsaiCount: jsonResp.totalElements]
        resultPage
    }

    @Override
    public Long count() {
        def queryUrl = grailsApplication.config.bonsaiws.baseurl

        BlockingHttpClient client = HttpClient.create(queryUrl.toURL()).toBlocking()

        HttpRequest request = HttpRequest.GET("bonsai/count")
        HttpResponse<String> resp = client.exchange(request, String)
        client.close()

        resp.body().toLong()
    }

    @Override
    void delete(Serializable id) {
        def queryUrl = grailsApplication.config.bonsaiws.baseurl

        BlockingHttpClient client = HttpClient.create(queryUrl.toURL()).toBlocking()

        HttpRequest request = HttpRequest.DELETE("bonsai/${id}")
        HttpResponse<String> resp = client.exchange(request, String)
        client.close()

        if (resp.getStatus() != HttpStatus.OK) {
            def e = new NotFoundException("Bonsai with id ${id} could not be deleted")
            throw e
        }
    }

    @Override
    public Bonsai save(Bonsai bonsai) {
        def queryUrl = grailsApplication.config.bonsaiws.baseurl

        BlockingHttpClient client = HttpClient.create(queryUrl.toURL()).toBlocking()

        BonsaiDTO bonsaiDTO = new BonsaiDTO()
        Copy.copy(bonsai, bonsaiDTO)
        bonsaiDTO.id = bonsai.getProperty("id") as Long

        HttpRequest request = HttpRequest.PUT("bonsai/dto", bonsaiDTO)
        HttpResponse<String> resp = client.exchange(request, String)
        client.close()

        if (resp.getStatus() == HttpStatus.OK) {
            //TODO refactor: how much do we need the DTO if we end up resorting to parsing raw json?
            String json = resp.body()
            ObjectMapper objectMapper = new ObjectMapper()
            objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
            objectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            Bonsai res = objectMapper.readValue(json, Bonsai)
            res
        } else {
            null
        }
    }
}
