package bonsaiapp


import bonsaiapp.dto.TaxonDTO
import com.fasterxml.jackson.core.type.TypeReference
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus

class TaxonService extends BaseService {

    static String REST_URL_ROOT = "taxon"

    def grailsApplication

    Taxon get(Serializable id) {
        String json = getRestJsonObject(grailsApplication, REST_URL_ROOT, id)
        JsonToObject.fromJson(json, new TypeReference<Taxon>(){})
    }

    ResultPage pageList(Map args) {
        ResultPage resultPage = new ResultPage()

        args['sort'] = args['sort'] ?: 'fullName'
        def (String jsonList, Long totalElements) = getRestJsonList(grailsApplication, REST_URL_ROOT, args)
        List<Taxon> taxonList = JsonToObject.fromJson(jsonList, new TypeReference<List<Taxon>>(){})

        resultPage.results = taxonList
        resultPage.pageModel = [taxonCount: totalElements]
        resultPage
    }

    Long count() {
        getRestCount(grailsApplication, REST_URL_ROOT)
    }

    void delete(Serializable id) {
        deleteRestObject(grailsApplication, REST_URL_ROOT, id)
    }

    Taxon save(Taxon taxon) {
        HttpResponse<String> resp = saveRestObject(grailsApplication, REST_URL_ROOT, taxon)

        if (resp.getStatus() == HttpStatus.OK) {
            //TODO refactor: how much do we need the DTO if we end up resorting to parsing raw json?
            TaxonDTO savedTaxonDTO = JsonToObject.fromJson(resp.body(), new TypeReference<TaxonDTO>(){})
            Copy.copy(savedTaxonDTO, taxon)
            taxon.setProperty("id", savedTaxonDTO.getId())
            taxon

        } else {
            null
        }
    }

}
