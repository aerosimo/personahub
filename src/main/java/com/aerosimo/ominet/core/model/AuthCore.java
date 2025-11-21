/******************************************************************************
 * This piece of work is to enhance personahub project functionality.         *
 *                                                                            *
 * Author:    eomisore                                                        *
 * File:      AuthCore.java                                                   *
 * Created:   21/11/2025, 01:00                                               *
 * Modified:  21/11/2025, 01:00                                               *
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

package com.aerosimo.ominet.core.model;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.aerosimo.ominet.dao.impl.APIResponseDTO;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.concurrent.TimeUnit;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;


public class AuthCore {

    private static final String AUTHCORE_BASE_URL = "https://ominet.aerosimo.com:9443/authcore/api/auth/validate";
    private static final String HMAC_SECRET = "SuperSecretSharedKey123";

    private static final OkHttpClient http = new OkHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();

    // Cache: token → boolean (valid/invalid)
    private static final Cache<String, Boolean> tokenCache = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .maximumSize(5000)
            .build();

    /**
     * Validate token using cache + HMAC
     */
    public static boolean validateToken(String token) {

        Boolean cached = tokenCache.getIfPresent(token);
        if (cached != null) {
            return cached;
        }

        try {
            // build request JSON
            Map<String, String> requestPayload = new HashMap<>();
            requestPayload.put("token", token);

            String json = mapper.writeValueAsString(requestPayload);

            Request request = new Request.Builder()
                    .url(AUTHCORE_BASE_URL)
                    .addHeader("Content-Type", "application/json")
                    // .addHeader("X-Service-Signature", generateHmac(json))
                    .post(RequestBody.create(json, MediaType.parse("application/json")))
                    .build();

            Response response = http.newCall(request).execute();
            String body = response.body().string();

            APIResponseDTO dto = mapper.readValue(body, APIResponseDTO.class);

            boolean valid = "successful".equalsIgnoreCase(dto.getStatus());

            tokenCache.put(token, valid);

            return valid;

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * HMAC SHA256 service-to-service signature
     */
    private static String generateHmac(String data) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec keySpec = new SecretKeySpec(HMAC_SECRET.getBytes(), "HmacSHA256");
        sha256_HMAC.init(keySpec);
        byte[] hash = sha256_HMAC.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(hash);
    }
}