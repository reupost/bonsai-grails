package bonsaiapp.impl

import bonsaiapp.Copy
import bonsaiapp.ITaxonService
import bonsaiapp.InputCleaner
import bonsaiapp.JsonToObject
import bonsaiapp.ResultPage
import bonsaiapp.Taxon
import bonsaiapp.User
import bonsaiapp.dto.BonsaiDTO
import bonsaiapp.dto.TaxonDTO
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

class TaxonService extends BaseService implements ITaxonService {

    def grailsApplication

    @Override
    Taxon get(Serializable id) {
        String json = getRestJsonObject(grailsApplication, "taxon", id)
        JsonToObject.fromJson(json, new TypeReference<Taxon>(){})
    }

    ResultPage pageList(Map args) {
        ResultPage resultPage = new ResultPage()

        args['sort'] = args['sort'] ?: 'fullName'
        def (String jsonList, Long totalElements) = getRestJsonList(grailsApplication, "taxon", args)
        List<Taxon> taxonList = JsonToObject.fromJson(jsonList, new TypeReference<List<Taxon>>(){})

        resultPage.results = taxonList
        resultPage.pageModel = [taxonCount: totalElements]
        resultPage
    }

    @Override
    Long count() {
        getRestCount(grailsApplication, "taxon")
    }

    @Override
    void delete(Serializable id) {
        deleteRestObject(grailsApplication, "taxon", id)
    }

    @Override
    Taxon save(Taxon taxon) {
        HttpResponse<String> resp = saveRestObject(grailsApplication, "taxon", taxon)

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
