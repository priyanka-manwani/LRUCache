package com.lrucache;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LRUCacheIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate template;

    @Before
    public void setup(){
        putEntry("1","400");
        putEntry("2","800");
    }

    @Test
    public void testGetEntry() throws Exception {
        ResponseEntity<String> response= getEntry(1);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response.getBody());
        JsonNode value = root.path("value");
        assertThat(value.asText(), equalTo("400"));
    }


    @Test
    public void testEvictEntry() throws Exception {
        ResponseEntity<String> getResponse = getEntry(1);
        assertThat(getResponse.getStatusCode(), equalTo(HttpStatus.OK));

        ResponseEntity<String> evictResponse = putEntry("3","1200");
        assertThat(evictResponse.getStatusCode(), equalTo(HttpStatus.OK));

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(evictResponse.getBody());
        JsonNode value = root.path("value");
        assertThat(value.asText(), equalTo("800"));

        ResponseEntity<String> getAfterEvictionResponse = getEntry(2); //should be a miss
        assertThat(getAfterEvictionResponse.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
    }


    private ResponseEntity<String > getEntry(int key) throws Exception {
        String url = "http://localhost:" + port + "/api/v1/get/{key}";

        Map<String, String> uriParams = new HashMap<>();
        uriParams.put("key", String.valueOf(key));

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);

        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = template.exchange(
                builder.buildAndExpand(uriParams).toString(),
                HttpMethod.GET,
                entity,
                String.class);

        return response;
    }

    private ResponseEntity<String> putEntry(String key, String value) {
        String url = "http://localhost:" + port + "/api/v1/put/{key}";

        Map<String, String> uriParams = new HashMap<>();
        uriParams.put("key", key);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("value", value);

        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = template.exchange(
                builder.buildAndExpand(uriParams).toString(),
                HttpMethod.PUT,
                entity,
                String.class);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
        return response;
    }
}
