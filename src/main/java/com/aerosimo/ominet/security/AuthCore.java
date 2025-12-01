/******************************************************************************
 * This piece of work is to enhance personahub project functionality.         *
 *                                                                            *
 * Author:    eomisore                                                        *
 * File:      AuthCore.java                                                   *
 * Created:   30/11/2025, 21:34                                               *
 * Modified:  30/11/2025, 21:34                                               *
 *                                                                            *
 * Copyright (c)  2025.  Aerosimo Ltd                                         *
 *                                                                            *
 * Permission is hereby granted, free of charge, to any person obtaining a    *
 * copy of this software and associated documentation files (the "Software"), *
 * to deal in the Software without restriction, including without limitation  *
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,   *
 * and/or sell copies of the Software, and to permit persons to whom the      *
 * Software is furnished to do so, subject to the following conditions:       *
 *                                                                            *
 * The above copyright notice and this permission notice shall be included    *
 * in all copies or substantial portions of the Software.                     *
 *                                                                            *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,            *
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES            *
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND                   *
 * NONINFINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT                 *
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,               *
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING               *
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE                 *
 * OR OTHER DEALINGS IN THE SOFTWARE.                                         *
 *                                                                            *
 ******************************************************************************/

package com.aerosimo.ominet.security;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.aerosimo.ominet.dao.impl.APIResponseDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.TimeUnit;
import java.util.HashMap;
import java.util.Map;

public class AuthCore {

    private static final Logger log = LogManager.getLogger(AuthCore.class.getName());

    private static final String AUTHCORE_BASE_URL =
            "https://ominet.aerosimo.com:9443/authcore/api/auth/validate";

    private static final MediaType JSON = MediaType.parse("application/json");

    // Dedicated HTTP Client with sensible timeouts
    private static final OkHttpClient http = new OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(6, TimeUnit.SECONDS)
            .writeTimeout(6, TimeUnit.SECONDS)
            .build();

    // ObjectMapper that won't crash if API adds new fields
    private static final ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    // Cache: token → boolean (valid / invalid)
    private static final Cache<String, Boolean> tokenCache = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .maximumSize(5000)
            .build();

    public static boolean validateToken(String token) {
        log.info("Token to consider is {}", token);
        if (token == null || token.trim().isEmpty()) {
            log.error("Invalid token");
            return false;
        }
        // 1. Check cache
        Boolean cached = tokenCache.getIfPresent(token);
        if (cached != null) {
            return cached;
        }
        try {
            // Build JSON payload
            Map<String, String> payload = new HashMap<>();
            payload.put("token", token);
            String json = mapper.writeValueAsString(payload);
            log.info("Sending request {} to server endpoint {}",json, AUTHCORE_BASE_URL);
            Request request = new Request.Builder()
                    .url(AUTHCORE_BASE_URL)
                    .post(RequestBody.create(json, JSON))
                    .addHeader("Content-Type", "application/json")
                    .build();
            try (Response response = http.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    System.err.println("AuthCore HTTP error: " + response.code());
                    tokenCache.put(token, false);
                    return false;
                }
                if (response.body() == null) {
                    System.err.println("AuthCore returned empty body");
                    tokenCache.put(token, false);
                    return false;
                }
                String body = response.body().string();
                log.info("Response body is {}", body);
                APIResponseDTO dto = mapper.readValue(body, APIResponseDTO.class);
                boolean valid =
                        "success".equalsIgnoreCase(dto.getStatus()) ||
                                "successful".equalsIgnoreCase(dto.getStatus());
                tokenCache.put(token, valid);
                return valid;
            }
        } catch (Exception ex) {
            System.err.println("AuthCore token validation failed: " + ex.getMessage());
            return false;
        }
    }
}