package bonsaiapp


import bonsaiapp.dto.UserDTO
import com.fasterxml.jackson.core.type.TypeReference
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus

class UserService extends BaseService  {

    static String REST_URL_ROOT = "user"

    def grailsApplication

    User get(Serializable id) {
        String json = getRestJsonObject(grailsApplication, REST_URL_ROOT, id)
        JsonToObject.fromJson(json, new TypeReference<User>(){})
    }

    ResultPage pageList(Map args) {
        ResultPage resultPage = new ResultPage()

        args['sort'] = args['sort'] ?: 'userName'
        def (String jsonList, Long totalElements) = getRestJsonList(grailsApplication, REST_URL_ROOT, args)
        List<User> userList = JsonToObject.fromJson(jsonList, new TypeReference<List<User>>(){})

        resultPage.results = userList
        resultPage.pageModel = [userCount: totalElements]
        resultPage
    }

    Long count() {
        getRestCount(grailsApplication, REST_URL_ROOT)
    }

    void delete(Serializable id) {
        deleteRestObject(grailsApplication, REST_URL_ROOT, id)
    }

    User save(User user) {
        HttpResponse<String> resp = saveRestObject(grailsApplication, REST_URL_ROOT, user)

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
