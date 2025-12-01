/******************************************************************************
 * This piece of work is to enhance personahub project functionality.         *
 *                                                                            *
 * Author:    eomisore                                                        *
 * File:      AuthCoreFilter.java                                             *
 * Created:   30/11/2025, 21:32                                               *
 * Modified:  30/11/2025, 21:32                                               *
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

import com.aerosimo.ominet.dao.impl.APIResponseDTO;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

@Provider
@Priority(Priorities.AUTHENTICATION)

public class AuthCoreFilter implements ContainerRequestFilter {

    private static final Logger log = LogManager.getLogger(AuthCoreFilter.class.getName());

    @Override
    public void filter(ContainerRequestContext ctx) throws IOException {
        String authHeader = ctx.getHeaderString(HttpHeaders.AUTHORIZATION);
        log.info("AuthCoreRequestFilter invoked for {} with header {}",
                ctx.getUriInfo().getPath(), authHeader);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            abort(ctx, "Missing or invalid Authorization header");
            return;
        }

        String token = authHeader.substring("Bearer ".length()).trim();
        boolean valid = AuthCore.validateToken(token);

        if (!valid) {
            abort(ctx, "Invalid or expired token");
        }
        // else continue to resource
    }

    private void abort(ContainerRequestContext ctx, String message) {
        APIResponseDTO apiResp = new APIResponseDTO("unauthorized", message);
        ctx.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                .type(MediaType.APPLICATION_JSON)
                .entity(apiResp)
                .build());
    }
}