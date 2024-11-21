package com.garciajops.redirecturlshortner;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

public class Main implements RequestHandler<Map<String, Object>, Map<String, Object>> {
    private final S3Client s3Client = S3Client.builder().build();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Map<String, Object> handleRequest(Map<String, Object> input, Context context) {
        Map<String, Object> response = new HashMap<>();
        String pathParameters = input.get("rawPath").toString();
        String shortUrlCode = pathParameters.replace("/", "");

        if (shortUrlCode == null || shortUrlCode.isEmpty()) {
            throw new IllegalArgumentException("Invalid input: 'shortUrlCode' is required");
        }

        GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket("garciajops-url-shortener-storage").key(shortUrlCode + ".json").build();

        InputStream s3ObjecStream;
        try {
            s3ObjecStream = s3Client.getObject(getObjectRequest);
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving shortened URL from S3: " + e.getMessage(), e);
        }

        UrlData urlData;
        try {
            urlData = objectMapper.readValue(s3ObjecStream,UrlData.class);
        } catch (Exception e) {
            throw new RuntimeException("Error parsing JSON from S3: " + e.getMessage(), e);
        }

        long currentTimeSeconds = System.currentTimeMillis() / 1000;
        if (currentTimeSeconds > urlData.getExpirationTime()) {
            response.put("statusCode", 410);
            response.put("body", "This URL has expired");
            return response;
        }

        response.put("statusCode", 302);
        Map<String, String> headers = new HashMap<>();
        headers.put("Location", urlData.getOriginalUrl());
        response.put("headers", headers);
        return response;
    }

}
