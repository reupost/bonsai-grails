package bonsaiapp.impl

import bonsaiapp.Bonsai
import bonsaiapp.Copy
import bonsaiapp.DiaryEntry
import bonsaiapp.IDiaryEntryService
import bonsaiapp.InputCleaner
import bonsaiapp.JsonToObject
import bonsaiapp.ResultPage
import bonsaiapp.Taxon
import bonsaiapp.dto.BonsaiDTO
import bonsaiapp.dto.DiaryEntryDTO
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

class DiaryEntryService extends BaseService implements IDiaryEntryService {

    def grailsApplication
    String queryUrl
    BlockingHttpClient client

    private setServiceTarget() {
        queryUrl = grailsApplication.config.bonsaiws.baseurl
        client = HttpClient.create((queryUrl as String).toURL()).toBlocking()
    }

    @Override
    DiaryEntry get(Serializable id) {
        String json = getRestJsonObject(grailsApplication, "diaryEntry", id)
        JsonToObject.fromJson(json, new TypeReference<DiaryEntry>(){})
    }

    ResultPage pageList(Map args) {
        ResultPage resultPage = new ResultPage()

        args['sort'] = args['sort'] ?: 'entryDate'
        args['order'] = args['order'] ?: 'DESC'
        def (String jsonList, Long totalElements) = getRestJsonList(grailsApplication, "diaryEntry", args)
        List<DiaryEntry> diaryEntryList = JsonToObject.fromJson(jsonList, new TypeReference<List<DiaryEntry>>(){})

        resultPage.results = diaryEntryList
        resultPage.pageModel = [diaryEntryCount: totalElements]
        resultPage
    }

    @Override
    Long count() {
        getRestCount(grailsApplication, "diaryEntry")
    }

    @Override
    void delete(Serializable id) {
        deleteRestObject(grailsApplication, "diaryEntry", id)
    }

    @Override
    DiaryEntry save(DiaryEntry diaryEntry) {
        HttpResponse<String> resp = saveRestObject(grailsApplication, "diaryEntry", diaryEntry)

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