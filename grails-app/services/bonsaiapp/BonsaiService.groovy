package bonsaiapp

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.client.BlockingHttpClient
import io.micronaut.http.client.HttpClient

import javax.naming.directory.SearchResult

public class BonsaiService implements IBonsaiService {

    def grailsApplication


    @Override
    public Bonsai get(Serializable id) {
        def queryUrl = grailsApplication.config.bonsaiws.baseurl

        BlockingHttpClient client = HttpClient.create(queryUrl.toURL()).toBlocking()

        HttpRequest request = HttpRequest.GET("/bonsai/bonsai/${id}")
        HttpResponse<String> resp = client.exchange(request, String)
        client.close()

        String json = resp.body()
        ObjectMapper objectMapper = new ObjectMapper()
        objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        objectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        Bonsai searchResult = objectMapper.readValue(json, Bonsai)
        searchResult
    }

    @Override
    public List<Bonsai> list(Map args) {
        return null;
    }

    @Override
    public Long count() {
        return null;
    }

    @Override
    public void delete(Serializable id) {

    }

    @Override
    public Bonsai save(Bonsai bonsai) {
        return null;
    }
}
