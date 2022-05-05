package ru.netology;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Main {
    public static final String REMOTE_SERVICE_URI = "https://api.nasa.gov/planetary/apod?api_key=rjs4KMdwVP5VnrIkEGluYtRxTWt8zeN8XIZhzK8b";
    public static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws IOException {
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)
                        .setSocketTimeout(30000)
                        .setRedirectsEnabled(false)
                        .build())
                .build();
        HttpGet request = new HttpGet(REMOTE_SERVICE_URI);
        CloseableHttpResponse response = httpClient.execute(request);
        AnswerNasa answerNasa = mapper.readValue(response.getEntity().getContent(), new TypeReference<AnswerNasa>() {
        });
        CloseableHttpResponse responsePic = httpClient.execute(new HttpGet(answerNasa.getUrl()));
        byte[] body = responsePic.getEntity().getContent().readAllBytes();
        String url = answerNasa.getUrl();
        String name = url.substring(url.lastIndexOf('/') + 1, url.length());
        try (FileOutputStream stream = new FileOutputStream(name);
             BufferedOutputStream bufStream = new BufferedOutputStream(stream)) {
            bufStream.write(body);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}