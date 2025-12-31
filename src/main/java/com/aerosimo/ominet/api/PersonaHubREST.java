/******************************************************************************
 * This piece of work is to enhance personahub project functionality.         *
 *                                                                            *
 * Author:    eomisore                                                        *
 * File:      PersonaHubREST.java                                                *
 * Created:   29/11/2025, 23:57                                               *
 * Modified:  30/11/2025, 00:02                                               *
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

package com.aerosimo.ominet.api;

import com.aerosimo.ominet.dao.impl.*;
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
import java.util.List;
import java.util.Objects;

@Path("/profile")
public class PersonaHubREST {

    private static final Logger log = LogManager.getLogger(PersonaHubREST.class);

    /* -------------------- Common Response Helpers -------------------- */
    private Response missingUsername() {
        log.error("Missing username parameter");
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new APIResponseDTO("unsuccessful", "username is required"))
                .build();
    }

    private Response badRequest(String message) {
        log.error("Bad request parameter: " + message);
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new APIResponseDTO("unsuccessful", message))
                .build();
    }

    private Response okOrBad(APIResponseDTO resp) {
        log.info("OK or Bad request parameter: " + resp);
        return "success".equalsIgnoreCase(resp.getStatus())
                ? Response.ok(resp).build()
                : Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
    }

    /* ======================= AVATAR ======================= */
    @POST
    @Path("/avatar/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadAvatar(
            @FormDataParam("username") String username,
            @FormDataParam("file") InputStream fileInputStream) {
        if (username == null || fileInputStream == null) return missingUsername();
        return okOrBad(PersonaDAO.saveImage(username, fileInputStream));
    }

    @POST
    @Path("/avatar/transfer")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadAvatarJson(ImageRequestDTO req) {
        log.info("Uploading avatar image for user {} ", req.getUsername());
        if (req == null || req.getUsername() == null || req.getAvatar() == null)
            return badRequest("missing required fields");
        try {
            String base64 = req.getAvatar().contains(",")
                    ? req.getAvatar().split(",", 2)[1]
                    : req.getAvatar();
            byte[] bytes = Base64.getDecoder().decode(base64);
            return okOrBad(PersonaDAO.saveImage(req.getUsername(), new ByteArrayInputStream(bytes)));
        } catch (IllegalArgumentException e) {
            log.error("Invalid Base64 format", e);
            return badRequest("invalid base64 image format");
        }
    }

    @GET
    @Path("/avatar/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAvatar(@PathParam("username") String username) {
        if (username == null || username.isEmpty()) return missingUsername();
        ImageResponseDTO resp = PersonaDAO.getImage(username);
        if (resp == null || resp.getAvatar() == null)
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new APIResponseDTO("unsuccessful", "no avatar found"))
                    .build();
        return Response.ok(resp).build();
    }

    @DELETE
    @Path("/avatar")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteAvatar(APIRequestDTO req) {
        if (req == null || req.getUsername() == null) return missingUsername();
        return okOrBad(PersonaDAO.removeImage(req.getUsername()));
    }

    /* ======================= ADDRESS ======================= */
    @POST
    @Path("/address")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response saveAddress(AddressRequestDTO req) {
        if (req == null || req.getUsername() == null) return missingUsername();
        return okOrBad(PersonaDAO.saveAddress(
                req.getUsername(), req.getFirstline(), req.getSecondline(), req.getThirdline(),
                req.getCity(), req.getPostcode(), req.getCountry()
        ));
    }

    @GET
    @Path("/address/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAddress(@PathParam("username") String username) {
        if (username == null || username.isEmpty()) return missingUsername();
        AddressResponseDTO resp = PersonaDAO.getAddress(username);
        if (resp == null || resp.getUsername() == null)
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new APIResponseDTO("unsuccessful", "no address found"))
                    .build();
        return Response.ok(resp).build();
    }

    @DELETE
    @Path("/address")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeAddress(APIRequestDTO req) {
        if (req == null || req.getUsername() == null) return missingUsername();
        return okOrBad(PersonaDAO.removeAddress(req.getUsername()));
    }

    /* ======================= CONTACT ======================= */
    @POST
    @Path("/contact")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response saveContact(List<ContactRequestDTO> reqList) {
        if (reqList == null || reqList.isEmpty())
            return badRequest("No contact records supplied");
        for (ContactRequestDTO r : reqList) {
            if (r.getUsername() == null || r.getChannel() == null || r.getAddress() == null)
                return badRequest("Missing required fields in one or more records");
        }
        return okOrBad(PersonaDAO.saveContacts(reqList));
    }

    @GET
    @Path("/contact/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getContact(@PathParam("username") String username) {
        if (username == null || username.isEmpty()) return missingUsername();
        List<ContactResponseDTO> list = PersonaDAO.getContact(username);
        if (list == null || list.isEmpty())
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new APIResponseDTO("unsuccessful", "no contact records found"))
                    .build();
        return Response.ok(list).build();
    }

    @DELETE
    @Path("/contact")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeContact(DeleteContactDTO req) {
        if (req == null || req.getUsername() == null) return missingUsername();
        return okOrBad(PersonaDAO.removeContact(req.getUsername(), req.getChannel()));
    }

    /* ======================= PERSON ======================= */
    @POST
    @Path("/person")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response savePerson(PersonRequestDTO req) {
        if (req == null || req.getUsername() == null) return missingUsername();
        return okOrBad(PersonaDAO.savePerson(
                req.getUsername(), req.getTitle(), req.getFirstName(),
                req.getMiddleName(),req.getLastName(), req.getGender(), req.getBirthday()
        ));
    }

    @GET
    @Path("/person/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPerson(@PathParam("username") String username) {
        if (username == null || username.isEmpty()) return missingUsername();
        PersonResponseDTO resp = PersonaDAO.getPerson(username);
        if (resp == null || resp.getUsername() == null)
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new APIResponseDTO("unsuccessful", "no person record found"))
                    .build();
        return Response.ok(resp).build();
    }

    @DELETE
    @Path("/person")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response removePerson(APIRequestDTO req) {
        if (req == null || req.getUsername() == null) return missingUsername();
        return okOrBad(PersonaDAO.removePerson(req.getUsername()));
    }

    /* ======================= METRICS ======================= */
    @GET
    @Path("/metrics/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMetrics(@PathParam("username") String username) {
        if (username == null || username.isEmpty()) return missingUsername();
        APIResponseDTO resp = PersonaDAO.getMetrics(username);
        return Response.ok(resp).build();
    }
}