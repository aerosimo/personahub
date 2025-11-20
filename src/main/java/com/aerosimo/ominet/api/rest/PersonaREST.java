/******************************************************************************
 * This piece of work is to enhance personahub project functionality.         *
 *                                                                            *
 * Author:    eomisore                                                        *
 * File:      PersonaREST.java                                                *
 * Created:   20/11/2025, 11:00                                               *
 * Modified:  20/11/2025, 11:00                                               *
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

package com.aerosimo.ominet.api.rest;

import com.aerosimo.ominet.dao.impl.APIResponseDTO;
import com.aerosimo.ominet.dao.impl.ImageRequestDTO;
import com.aerosimo.ominet.dao.impl.ImageResponseDTO;
import com.aerosimo.ominet.dao.mapper.PersonaDAO;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.media.multipart.FormDataParam;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;

@Path("/profile")
public class PersonaREST {

    private static final Logger log = LogManager.getLogger(PersonaREST.class);

    @POST
    @Path("/avatarupload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadAvatar(
            @FormDataParam("username") String username,
            @FormDataParam("file") InputStream fileInputStream) {
        if (fileInputStream == null || username == null) {
            log.error("Missing required fields for upload avatar");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Missing required fields").build();
        }
        APIResponseDTO result = PersonaDAO.saveImage(username, fileInputStream);
        if ("success".equalsIgnoreCase(result.getStatus())) {
            return Response.ok(result).build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new APIResponseDTO("unsuccessful")).build();
        }
    }

    @POST
    @Path("/avatartransfer")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadAvatarJson(ImageRequestDTO dto) {
        if (dto == null || dto.getUsername() == null || dto.getAvatar() == null) {
            log.error("Missing required fields for avatar transfer");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new APIResponseDTO("missing required fields")).build();
        }
        try {
            // Strip metadata if present (e.g., "data:image/png;base64,")
            String base64Data = dto.getAvatar().contains(",")
                    ? dto.getAvatar().split(",")[1]
                    : dto.getAvatar();
            byte[] imageBytes = Base64.getDecoder().decode(base64Data);
            InputStream avatarStream = new ByteArrayInputStream(imageBytes);
            APIResponseDTO result = PersonaDAO.saveImage(dto.getUsername(), avatarStream);
            if ("success".equalsIgnoreCase(result.getStatus())) {
                return Response.ok(result).build();
            } else {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new APIResponseDTO("unsuccessful")).build();
            }
        } catch (IllegalArgumentException e) {
            log.error("Invalid base64 image format");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new APIResponseDTO("invalid base64 image format")).build();
        }
    }

    @DELETE
    @Path("/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteAvatar(@PathParam("username") String username) {
        if (username == null || username.isEmpty()) {
            log.error("username is required for deletion");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new APIResponseDTO("email is required for deletion")).build();
        }
        APIResponseDTO result = PersonaDAO.removeImage(username);
        if ("success".equalsIgnoreCase(result.getStatus())) {
            return Response.ok(result).build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new APIResponseDTO("unsuccessful")).build();
        }
    }

    @GET
    @Path("/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAvatar(@PathParam("username") String username) {
        log.info("Received avatar fetch request for {}", username);
        if (username == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new APIResponseDTO("username is required")).build();
        }
        ImageResponseDTO imageDTO = PersonaDAO.getImage(username);
        if (imageDTO == null || imageDTO.getAvatar() == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new APIResponseDTO("no image found")).build();
        }
        return Response.ok(imageDTO).build();
    }

}