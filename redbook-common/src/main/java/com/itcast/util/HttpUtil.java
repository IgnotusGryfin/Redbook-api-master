package com.itcast.util;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * http自定义请求
 */
public class HttpUtil {

    /**
     * get请求
     * @param url
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public static String get(String url) throws IOException, InterruptedException {
        // 1.创建http客户端
        HttpClient httpClient = HttpClient.newHttpClient();
        // 2.创建url
        URI uri = URI.create(url);
        // 3.发送请求
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        // 4.获取结果
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            return null;
        }
        return response.body();
    }

    /**
     * post请求
     * @param url
     * @param data
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public static Object post(String url, Object data) throws IOException, InterruptedException {
        // 1.创建http客户端
        HttpClient httpClient = HttpClient.newHttpClient();
        // 2.创建url
        URI uri = URI.create(url);
        // 3.构造json
        String json = new Gson().toJson(data);
        // 4.发送请求
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        // 5.获取结果
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            return null;
        }
        return response.body();
    }
}
