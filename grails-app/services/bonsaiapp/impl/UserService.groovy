package bonsaiapp.impl

import bonsaiapp.Copy
import bonsaiapp.IUserService
import bonsaiapp.InputCleaner
import bonsaiapp.JsonToObject
import bonsaiapp.ResultPage
import bonsaiapp.User
import bonsaiapp.dto.UserDTO
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

class UserService extends BaseService implements IUserService  {

    def grailsApplication

    @Override
    User get(Serializable id) {
        String json = getRestJsonObject(grailsApplication, "user", id)
        JsonToObject.fromJson(json, new TypeReference<User>(){})
    }

    ResultPage pageList(Map args) {
        ResultPage resultPage = new ResultPage()

        args['sort'] = args['sort'] ?: 'userName'
        def (String jsonList, Long totalElements) = getRestJsonList(grailsApplication, "user", args)
        List<User> userList = JsonToObject.fromJson(jsonList, new TypeReference<List<User>>(){})

        resultPage.results = userList
        resultPage.pageModel = [userCount: totalElements]
        resultPage
    }

    @Override
    Long count() {
        getRestCount(grailsApplication, "user")
    }

    @Override
    void delete(Serializable id) {
        deleteRestObject(grailsApplication, "user", id)
    }

    @Override
    User save(User user) {
        HttpResponse<String> resp = saveRestObject(grailsApplication, "user", user)

        if (resp.getStatus() == HttpStatus.OK) {
            //TODO refactor: how much do we need the DTO if we end up resorting to parsing raw json?
            UserDTO savedUserDTO = JsonToObject.fromJson(resp.body(), new TypeReference<UserDTO>(){})
            Copy.copy(savedUserDTO, user)
            user.setProperty("id", savedUserDTO.getId())
            user

        } else {
            null
        }
    }

}
