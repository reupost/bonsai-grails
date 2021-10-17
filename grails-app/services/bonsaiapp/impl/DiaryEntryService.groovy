package bonsaiapp.impl

import bonsaiapp.Copy
import bonsaiapp.DiaryEntry
import bonsaiapp.IDiaryEntryService
import bonsaiapp.JsonToObject
import bonsaiapp.ResultPage
import bonsaiapp.dto.DiaryEntryDTO
import com.fasterxml.jackson.core.type.TypeReference
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.BlockingHttpClient
import io.micronaut.http.client.HttpClient

class DiaryEntryService extends BaseService implements IDiaryEntryService {

    static String REST_URL_ROOT = "diaryEntry"

    def grailsApplication
    String queryUrl
    BlockingHttpClient client

    private setServiceTarget() {
        queryUrl = grailsApplication.config.bonsaiws.baseurl
        client = HttpClient.create((queryUrl as String).toURL()).toBlocking()
    }

    @Override
    DiaryEntry get(Serializable id) {
        String json = getRestJsonObject(grailsApplication, REST_URL_ROOT, id)
        JsonToObject.fromJson(json, new TypeReference<DiaryEntry>(){})
    }

    ResultPage pageList(Map args) {
        ResultPage resultPage = new ResultPage()

        args['sort'] = args['sort'] ?: 'entryDate'
        args['order'] = args['order'] ?: 'DESC'
        def (String jsonList, Long totalElements) = getRestJsonList(grailsApplication, REST_URL_ROOT, args)
        List<DiaryEntry> diaryEntryList = JsonToObject.fromJson(jsonList, new TypeReference<List<DiaryEntry>>(){})

        resultPage.results = diaryEntryList
        resultPage.pageModel = [diaryEntryCount: totalElements]
        resultPage
    }

    @Override
    Long count() {
        getRestCount(grailsApplication, REST_URL_ROOT)
    }

    @Override
    void delete(Serializable id) {
        deleteRestObject(grailsApplication, REST_URL_ROOT, id)
    }

    @Override
    DiaryEntry save(DiaryEntry diaryEntry) {
        HttpResponse<String> resp = saveRestObject(grailsApplication, REST_URL_ROOT, diaryEntry)

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