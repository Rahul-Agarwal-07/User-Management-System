package com.cleanarch.adapter.in.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    TestRestTemplate restTemplate;

    @BeforeEach
    void setup() {

        String url = "http://localhost:" + port + "/users";

        Map<String,String> body = Map.of(
                "name", "Rahul",
                "email", "rahul@example.com",
                "phone", "9999999999",
                "password", "mypassword"
        );

        restTemplate.postForEntity(url, body, Map.class);
    }

    @Test
    void should_login_user_and_return_tokens()
    {
        String url = "http://localhost:" + port + "/auth/login";

        Map<String, String> body = Map.of(
                "email" , "rahul@example.com",
                "password", "mypassword",
                "deviceId", "chrome",
                "userAgent", "chrome windows"
        );

        ResponseEntity<Map> response = restTemplate.postForEntity(url, body, Map.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        Map responseBody = response.getBody();

        assertNotNull(responseBody.get("accessToken"));
        assertNotNull(responseBody.get("refreshToken"));
    }

    @Test
    void should_return_refresh_token_successfully()
    {
        String loginUrl = "http://localhost:" + port + "/auth/login";

        Map<String, String> loginBody = Map.of(
                "email" , "rahul@example.com",
                "password", "mypassword",
                "deviceId", "chrome",
                "userAgent", "chrome windows"
        );

        ResponseEntity<Map> loginResponse = restTemplate.postForEntity(loginUrl, loginBody, Map.class);

        String refreshToken = (String) loginResponse.getBody().get("refreshToken");

        String refreshUrl = "http://localhost:" + port + "/auth/refresh";

        Map<String, String> refreshBody = Map.of(
                "refreshToken", refreshToken
        );

        ResponseEntity<Map> refreshResponse = restTemplate.postForEntity(refreshUrl, refreshBody, Map.class);

        assertEquals(HttpStatus.OK, refreshResponse.getStatusCode());

        Map responseBody = refreshResponse.getBody();

        assertNotNull(responseBody.get("refreshToken"));
        assertNotNull(responseBody.get("accessToken"));
    }

    @Test
    void should_detect_refresh_token_reuse_attack()
    {
        String loginUrl = "http://localhost:" + port + "/auth/login";

        Map<String, String> loginBody = Map.of(
                "email" , "rahul@example.com",
                "password", "mypassword",
                "deviceId", "chrome",
                "userAgent", "chrome windows"
        );

        ResponseEntity<Map> loginResponse = restTemplate.postForEntity(loginUrl, loginBody, Map.class);

        String refreshToken = (String) loginResponse.getBody().get("refreshToken");

        String refreshUrl = "http://localhost:" + port + "/auth/refresh";

        Map<String, String> refreshBody = Map.of(
                "refreshToken", refreshToken
        );

        // first refresh should return OK
        ResponseEntity<Map> firstRefresh = restTemplate.postForEntity(refreshUrl, refreshBody, Map.class);
        assertEquals(HttpStatus.OK, firstRefresh.getStatusCode());


        // second refresh with previous body should detect reuse
        ResponseEntity<Map> secondRefresh = restTemplate.postForEntity(refreshUrl, refreshBody, Map.class);
        assertEquals(HttpStatus.FORBIDDEN, secondRefresh.getStatusCode());
    }

    @Test
    void should_reject_invalid_refresh_token()
    {
        String refreshUrl = "http://localhost:" + port + "/auth/refresh";

        Map<String, String> refreshBody = Map.of(
                "refreshToken", "invalid-token"
        );

        ResponseEntity<Map> refreshResponse = restTemplate.postForEntity(refreshUrl, refreshBody, Map.class);
        assertEquals(HttpStatus.UNAUTHORIZED, refreshResponse.getStatusCode());
    }

    @Test
    void should_logout_user_successfully()
    {
        String loginUrl = "http://localhost:" + port + "/auth/login";

        Map<String, String> loginBody = Map.of(
                "email" , "rahul@example.com",
                "password", "mypassword",
                "deviceId", "chrome",
                "userAgent", "chrome windows"
        );

        ResponseEntity<Map> loginResponse = restTemplate.postForEntity(loginUrl, loginBody, Map.class);

        String accessToken = (String) loginResponse.getBody().get("accessToken");

        String logoutUrl = "http://localhost:" + port + "/auth/logout";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Map> logoutResponse = restTemplate.postForEntity(logoutUrl, request, Map.class);
        assertEquals(HttpStatus.NO_CONTENT, logoutResponse.getStatusCode());
    }
}
