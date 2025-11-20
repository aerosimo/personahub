/******************************************************************************
 * This piece of work is to enhance personahub project functionality.         *
 *                                                                            *
 * Author:    eomisore                                                        *
 * File:      Postmaster.java                                                 *
 * Created:   15/11/2025, 23:32                                               *
 * Modified:  15/11/2025, 23:32                                               *
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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Utility for sending email via Postmaster REST service.
 */
public class Postmaster {

    private static final Logger log = LogManager.getLogger(Postmaster.class);
    private static final String ENDPOINT_URL = "https://ominet.aerosimo.com:9443/postmaster/api/sendemail";
    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * Sends an email using Postmaster REST API.
     *
     * @param emailAddress recipient address
     * @param emailSubject subject line
     * @param emailMessage message body
     * @param emailFiles   optional attachments (as string reference)
     * @return status message from Postmaster
     */
    public static String sendEmail(String emailAddress, String emailSubject, String emailMessage, String emailFiles) {
        try {
            Map<String, Object> payload = Map.of(
                    "emailAddress", emailAddress,
                    "emailSubject", emailSubject,
                    "emailMessage", emailMessage,
                    "emailFiles", emailFiles);

            String jsonRequest = mapper.writeValueAsString(payload);

            HttpURLConnection conn = (HttpURLConnection) new URL(ENDPOINT_URL).openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(3000);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonRequest.getBytes(StandardCharsets.UTF_8));
            }

            int statusCode = conn.getResponseCode();
            if (statusCode == HttpURLConnection.HTTP_OK || statusCode == HttpURLConnection.HTTP_CREATED) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder responseBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        responseBuilder.append(line);
                    }
                    Map<String, String> result = mapper.readValue(responseBuilder.toString(), new TypeReference<>() {});
                    return result.getOrDefault("Status", "Message sent successfully");
                }
            } else {
                log.error("Postmaster REST failed with HTTP code {}", statusCode);
                return "Message not successful: HTTP " + statusCode;
            }

        } catch (Exception err) {
            log.error("Email Notification Service failed in {} with error: ", Postmaster.class.getName(), err);
            try {
                Spectre.recordError("EM-20007", err.getMessage(), Postmaster.class.getName());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return "Message not successful";
        }
    }
}