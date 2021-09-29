package bonsaiapp

import com.fasterxml.jackson.core.type.TypeReference
import grails.util.Holders
import groovy.json.JsonBuilder
import groovy.json.JsonParserType
import groovy.json.JsonSlurper
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.BlockingHttpClient
import io.micronaut.http.client.HttpClient
import javassist.NotFoundException

class DiaryEntryService implements IDiaryEntryService {

    def grailsApplication
    String queryUrl
    BlockingHttpClient client

    private setServiceTarget() {
        queryUrl = grailsApplication.config.bonsaiws.baseurl
        client = HttpClient.create((queryUrl as String).toURL()).toBlocking()
    }

    @Override
    DiaryEntry get(Serializable id) {
        setServiceTarget()
        HttpRequest request = HttpRequest.GET("diaryEntry/${id}")
        HttpResponse<String> resp = this.client.exchange(request, String)
        client.close()

        JsonToObject.fromJson(resp.body(), new TypeReference<DiaryEntry>(){})
    }

    @Override
    List<DiaryEntry> list(Map args) {
        setServiceTarget()
        ResultPage resultPage = pageList(args)
        resultPage.results
    }

    ResultPage pageList(Map args) {
        setServiceTarget()
        ResultPage resultPage = new ResultPage()
        InputCleaner inputCleaner = new InputCleaner()

        Integer offset = (args['offset'] ?: 0) as Integer
        Integer size = (args['max'] ?: 10) as Integer

        def filter = inputCleaner.getOnlyLettersAndNumbers((args['searchFilter'] ?: "").toString())
        filter = URLEncoder.encode(filter, "UTF-8")
        def page = Math.floor(offset/size).toInteger()

        def sort = args['sort'] ?: 'entryDate'
        def dir = args['order'] ?: 'DESC'

        HttpRequest request = HttpRequest.GET("diaryEntry/page?filter=${filter}&page=${page}&size=${size}&sort=${sort}&dir=${dir}")
        HttpResponse<String> resp = client.exchange(request, String)
        client.close()

        String json = resp.body()
        //this is bit of a hacky way of getting around the REST service returning 'content:[array]' instead of just 'array'
        def parser = new JsonSlurper().setType(JsonParserType.LAX)
        def jsonResp = parser.parseText(json)
        def jsonDiaryEntryList = new JsonBuilder(jsonResp.content).toPrettyString()

        List<DiaryEntry> diaryEntryList = JsonToObject.fromJson(jsonDiaryEntryList, new TypeReference<List<DiaryEntry>>(){})

        resultPage.results = diaryEntryList
        resultPage.pageModel = [diaryEntryCount: jsonResp.totalElements]
        resultPage
    }

    @Override
    Long count() {
        setServiceTarget()
        HttpRequest request = HttpRequest.GET("diaryEntry/count")
        HttpResponse<String> resp = client.exchange(request, String)
        client.close()

        resp.body().toLong()
    }

    @Override
    void delete(Serializable id) {
        setServiceTarget()
        HttpRequest request = HttpRequest.DELETE("diaryEntry/${id}")
        HttpResponse<String> resp = client.exchange(request, String)
        client.close()

        if (resp.getStatus() != HttpStatus.OK) {
            def e = new NotFoundException("DiaryEntry with id ${id} could not be deleted")
            throw e
        }
    }

    @Override
    DiaryEntry save(DiaryEntry diaryEntry) {
        setServiceTarget()
        DiaryEntryDTO diaryEntryDTO = new DiaryEntryDTO()
        Copy.copy(diaryEntry, diaryEntryDTO)
        diaryEntryDTO.id = diaryEntry.getProperty("id") as Long

        HttpRequest request
        if (diaryEntryDTO.id == null) {
            //create
            request = HttpRequest.POST("diaryEntry/dto", diaryEntryDTO)
        } else {
            //edit
            request = HttpRequest.PUT("diaryEntry/dto", diaryEntryDTO)
        }

        HttpResponse<String> resp = client.exchange(request, String)
        client.close()

        if (resp.getStatus() == HttpStatus.OK) {
            //TODO refactor: how much do we need the DTO if we end up resorting to parsing raw json?
            DiaryEntryDTO savedDiaryEntryDTO = JsonToObject.fromJson(resp.body(), new TypeReference<DiaryEntryDTO>(){})
            Copy.copy(savedDiaryEntryDTO, diaryEntry)
            diaryEntry.setProperty("id", savedDiaryEntryDTO.getId())
            diaryEntry
        } else {
            null
        }
    }
}