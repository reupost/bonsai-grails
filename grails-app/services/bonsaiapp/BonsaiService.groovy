package bonsaiapp


import bonsaiapp.dto.BonsaiDTO
import com.fasterxml.jackson.core.type.TypeReference
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus

class BonsaiService extends BaseService {

    static String REST_URL_ROOT = "bonsai"

    def grailsApplication

    def TaxonService taxonService

    Bonsai get(Serializable id) {
        String json = getRestJsonObject(grailsApplication, REST_URL_ROOT, id)
        JsonToObject.fromJson(json, new TypeReference<Bonsai>(){})
    }

    ResultPage pageList(Map args) {
        ResultPage resultPage = new ResultPage()

        args['sort'] = args['sort'] ?: 'tag'
        if (args['sort'] == 'taxon') args['sort'] = 'taxon.fullName'
        def (String jsonList, Long totalElements) = getRestJsonList(grailsApplication, REST_URL_ROOT, args)
        List<Bonsai> bonsaiList = JsonToObject.fromJson(jsonList, new TypeReference<List<Bonsai>>(){})

        resultPage.results = bonsaiList
        resultPage.pageModel = [bonsaiCount: totalElements]
        resultPage
    }

    Long count() {
        getRestCount(grailsApplication, REST_URL_ROOT)
    }

    void delete(Serializable id) {
        deleteRestObject(grailsApplication, REST_URL_ROOT, id)
    }

    Bonsai save(Bonsai bonsai) {
        HttpResponse<String> resp = saveRestObject(grailsApplication, REST_URL_ROOT, bonsai)

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
