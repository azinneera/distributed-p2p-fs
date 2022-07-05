package com.project.dfs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Objects;

@Service
public class Client {
    public static String portNumber;

    @Value("${server.port}")
    public void setPortNumber(String ownerPortNumber) {
        portNumber = ownerPortNumber;
    }

    @PostConstruct
    public void init() {
    }


    RestTemplate restTemplate = new RestTemplate();

    public void getFile(int portNumber, String fileName) {
        try {
            HttpHeaders headers = new HttpHeaders();
            URI url = UriComponentsBuilder.fromUriString("http://localhost:" + portNumber + "/downloadFile")
                    .queryParam("fileName", fileName).build().toUri();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_OCTET_STREAM));
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, byte[].class);
            Node.log(Node.INFO, "Requested file downloaded successfully.");

            Path filePath = Utils.getNodesDir().resolve(String.valueOf(Client.portNumber)).resolve(fileName + ".txt");
            Files.write(filePath, Objects.requireNonNull(response.getBody()));
            Node.log(Node.INFO, "Requested file downloaded successfully.");
            Utils.nowEpoch("DOWNLOAD SUCCESSFUL");
        } catch (Exception e) {
            Node.log(Node.ERROR, e.getStackTrace());
        }
    }
}
