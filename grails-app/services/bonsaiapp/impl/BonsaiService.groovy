package bonsaiapp.impl

import bonsaiapp.Bonsai
import bonsaiapp.Copy
import bonsaiapp.IBonsaiService
import bonsaiapp.ITaxonService
import bonsaiapp.InputCleaner
import bonsaiapp.JsonToObject
import bonsaiapp.ResultPage
import bonsaiapp.Taxon
import bonsaiapp.dto.BonsaiDTO
import com.fasterxml.jackson.core.type.TypeReference
import grails.core.GrailsApplication
import groovy.json.JsonBuilder
import groovy.json.JsonParserType
import groovy.json.JsonSlurper
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.BlockingHttpClient
import io.micronaut.http.client.HttpClient
import javassist.NotFoundException

class BonsaiService extends BaseService implements IBonsaiService {

    def grailsApplication

    def ITaxonService taxonService

    @Override
    Bonsai get(Serializable id) {
        String json = getRestJsonObject(grailsApplication, "bonsai", id)
        JsonToObject.fromJson(json, new TypeReference<Bonsai>(){})
    }

    ResultPage pageList(Map args) {
        ResultPage resultPage = new ResultPage()

        args['sort'] = args['sort'] ?: 'tag'
        if (args['sort'] == 'taxon') args['sort'] = 'taxon.fullName'
        def (String jsonList, Long totalElements) = getRestJsonList(grailsApplication, "bonsai", args)
        List<Bonsai> bonsaiList = JsonToObject.fromJson(jsonList, new TypeReference<List<Bonsai>>(){})

        resultPage.results = bonsaiList
        resultPage.pageModel = [bonsaiCount: totalElements]
        resultPage
    }

    @Override
    Long count() {
        getRestCount(grailsApplication, "bonsai")
    }

    @Override
    void delete(Serializable id) {
        deleteRestObject(grailsApplication, "bonsai", id)
    }

    @Override
    Bonsai save(Bonsai bonsai) {
        HttpResponse<String> resp = saveRestObject(grailsApplication, "bonsai", bonsai)

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
