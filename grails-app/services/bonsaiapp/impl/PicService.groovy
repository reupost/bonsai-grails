package bonsaiapp

import bonsaiapp.dto.BonsaiDTO
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
import javassist.NotFoundException

class PicService extends BaseService implements IPicService {

    def grailsApplication

    @Override
    Pic get(Serializable id) {
        String json = getRestJsonObject(grailsApplication, "pic", id)
        JsonToObject.fromJson(json, new TypeReference<Pic>(){})
    }

    //note: not part of interface
    byte[] getImg(Serializable id) {
        def queryUrl = grailsApplication.config.bonsaiws.baseurl

        BlockingHttpClient client = HttpClient.create((queryUrl as String).toURL()).toBlocking()

        HttpRequest requestImg = HttpRequest.GET("pic/image/${id}")
        HttpResponse<byte[]> respImg = client.exchange(requestImg, byte[])
        client.close()

        byte[] img = respImg.body()
        img
    }

    //note: not part of interface
    byte[] getImgThumb(Serializable id) {
        def queryUrl = grailsApplication.config.bonsaiws.baseurl

        BlockingHttpClient client = HttpClient.create((queryUrl as String).toURL()).toBlocking()

        HttpRequest requestImg = HttpRequest.GET("pic/thumb/${id}")
        HttpResponse<byte[]> respImg = client.exchange(requestImg, byte[])
        client.close()

        byte[] img = respImg.body()
        img
    }

    ResultPage pageList(Map args) {
        ResultPage resultPage = new ResultPage()

        args['sort'] = args['sort'] ?: 'entityType'
        def (String jsonList, Long totalElements) = getRestJsonList(grailsApplication, "pic", args)
        List<Pic> picList = JsonToObject.fromJson(jsonList, new TypeReference<List<Pic>>(){})

        resultPage.results = picList
        resultPage.pageModel = [picCount: totalElements]
        resultPage
    }

    @Override
    Long count() {
        getRestCount(grailsApplication, "pic")
    }

    @Override
    void delete(Serializable id) {
        deleteRestObject(grailsApplication, "pic", id)
    }

    @Override
    Pic save(Pic pic) {
        HttpResponse<String> resp = saveRestObject(grailsApplication, "pic", pic)

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
            request = HttpRequest.POST("pic", requestBody)
        } else {
            //edit
            request = HttpRequest.POST("pic", requestBody)
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
