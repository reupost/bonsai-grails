package bonsaiapp

import bonsaiapp.dto.PicDTO
import bonsaiapp.impl.BaseService
import com.fasterxml.jackson.core.type.TypeReference
import grails.converters.JSON
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.BlockingHttpClient
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.multipart.MultipartBody

class PicService extends BaseService implements IPicService {

    static String REST_URL_ROOT = "pic"

    def grailsApplication

    @Override
    Pic get(Serializable id) {
        String json = getRestJsonObject(grailsApplication, REST_URL_ROOT, id)
        JsonToObject.fromJson(json, new TypeReference<Pic>(){})
    }

    //note: not part of interface
    byte[] getImg(Serializable id) {
        def queryUrl = grailsApplication.config.bonsaiws.baseurl

        BlockingHttpClient client = HttpClient.create((queryUrl as String).toURL()).toBlocking()

        HttpRequest requestImg = HttpRequest.GET("${REST_URL_ROOT}/image/${id}")
        HttpResponse<byte[]> respImg = client.exchange(requestImg, byte[])
        client.close()

        byte[] img = respImg.body()
        img
    }

    //note: not part of interface
    byte[] getImgThumb(Serializable id) {
        def queryUrl = grailsApplication.config.bonsaiws.baseurl

        BlockingHttpClient client = HttpClient.create((queryUrl as String).toURL()).toBlocking()

        HttpRequest requestImg = HttpRequest.GET("${REST_URL_ROOT}/thumb/${id}")
        HttpResponse<byte[]> respImg = client.exchange(requestImg, byte[])
        client.close()

        byte[] img = respImg.body()
        img
    }

    ResultPage pageList(Map args) {
        ResultPage resultPage = new ResultPage()

        args['sort'] = args['sort'] ?: 'entityType'
        def (String jsonList, Long totalElements) = getRestJsonList(grailsApplication, REST_URL_ROOT, args)
        List<Pic> picList = JsonToObject.fromJson(jsonList, new TypeReference<List<Pic>>(){})

        resultPage.results = picList
        resultPage.pageModel = [picCount: totalElements]
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
    Pic save(Pic pic) {
        HttpResponse<String> resp = saveRestObject(grailsApplication, REST_URL_ROOT, pic)

        if (resp.getStatus() == HttpStatus.OK) {
            //TODO refactor: how much do we need the DTO if we end up resorting to parsing raw json?
            PicDTO savedPicDTO = JsonToObject.fromJson(resp.body(), new TypeReference<PicDTO>(){})
            Copy.copy(savedPicDTO, pic)
            pic.setProperty("id", savedPicDTO.getId())
            pic
        } else {
            null
        }
    }

    Pic saveWithImg(Pic pic, imgFile) {

        String pString = pic as JSON

        def queryUrl = grailsApplication.config.bonsaiws.baseurl

        BlockingHttpClient client = HttpClient.create((queryUrl as String).toURL()).toBlocking()

        MultipartBody.Builder requestBody = MultipartBody.builder()
            .addPart("p", pString)
        if (imgFile != null) {
            requestBody.addPart("file", imgFile.filename, imgFile.getBytes())
        }

        HttpRequest request
        //TODO DTO needed? work on spring side
        if (pic.getProperty("id") == null) {
            //create
            request = HttpRequest.POST(REST_URL_ROOT, requestBody)
        } else {
            //edit
            request = HttpRequest.POST(REST_URL_ROOT, requestBody)
        }
        request.header("Content-Type", "multipart/form-data")
        HttpResponse<String> resp = client.exchange(request, String)

        client.close()

        if (resp.getStatus() == HttpStatus.OK) {
            //TODO refactor: how much do we need the DTO if we end up resorting to parsing raw json?
            PicDTO savedPicDTO = JsonToObject.fromJson(resp.body(), new TypeReference<PicDTO>(){})
            Copy.copy(savedPicDTO, pic)
            pic.setProperty("id", savedPicDTO.getId())
            pic
        } else {
            null
        }
    }
}
