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
import io.micronaut.http.client.BlockingHttpClient
import io.micronaut.http.client.HttpClient

import javax.naming.directory.SearchResult

public class BonsaiService implements IBonsaiService {

    def grailsApplication


    @Override
    public Bonsai get(Serializable id) {
        def queryUrl = grailsApplication.config.bonsaiws.baseurl

        BlockingHttpClient client = HttpClient.create(queryUrl.toURL()).toBlocking()

        HttpRequest request = HttpRequest.GET("/bonsai/bonsai/${id}")
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

        def queryUrl = grailsApplication.config.bonsaiws.baseurl

        BlockingHttpClient client = HttpClient.create(queryUrl.toURL()).toBlocking()

        Integer offset = (args['offset'] ?: 0) as Integer
        Integer size = (args['max'] ?: 10) as Integer

        def filter = (args['searchFilter'] ?: "").replaceAll("[^a-zA-Z0-9]", "") //TODO unicode
        def page = Math.floor(offset/size).toInteger()

        def sort = args['sort']
        if (sort == 'taxon') sort = 'taxon.fullName' //do not sort by taxon id
        def dir = args['order']

        HttpRequest request = HttpRequest.GET("/bonsai/bonsais_page?filter=${filter}&page=${page}&sort=${sort}&dir=${dir}")
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
        return null;
    }

    @Override
    public void delete(Serializable id) {

    }

    @Override
    public Bonsai save(Bonsai bonsai) {
        return null;
    }
}
