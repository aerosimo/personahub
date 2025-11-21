/******************************************************************************
 * This piece of work is to enhance personahub project functionality.         *
 *                                                                            *
 * Author:    eomisore                                                        *
 * File:      PersonaDAO.java                                                 *
 * Created:   15/11/2025, 23:39                                               *
 * Modified:  15/11/2025, 23:39                                               *
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

package com.aerosimo.ominet.dao.mapper;

import com.aerosimo.ominet.core.config.Connect;
import com.aerosimo.ominet.core.model.Spectre;
import com.aerosimo.ominet.dao.impl.*;
import oracle.jdbc.OracleTypes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class PersonaDAO {

    private static final Logger log = LogManager.getLogger(PersonaDAO.class.getName());

    public static APIResponseDTO saveAddress(String username, String firstline, String secondline,
                                             String thirdline, String city, String postcode, String country) {
        log.info("Preparing to create or update user address");
        String response;
        String sql = "{call identification_pkg.saveAddress(?,?,?,?,?,?,?,?)}";
        try (Connection con = Connect.dbase();
             CallableStatement stmt = con.prepareCall(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, firstline);
            stmt.setString(3, secondline);
            stmt.setString(4, thirdline);
            stmt.setString(5, city);
            stmt.setString(6, postcode);
            stmt.setString(7, country);
            stmt.registerOutParameter(8, OracleTypes.VARCHAR);
            stmt.execute();
            response = stmt.getString(8);
            if(response.equalsIgnoreCase("success")){
                response = "success";
                return new APIResponseDTO(response,"address detail saved successfully");
            } else {
                response = "unsuccessful";
                return new APIResponseDTO(response,"address detail not saved");
            }
        } catch (SQLException err) {
            log.error("Error in identification_pkg (SAVE ADDRESS)", err);
            try {
                Spectre.recordError("TE-20001", "Error in identification_pkg (SAVE ADDRESS): " + err.getMessage(), PersonaDAO.class.getName());
                response = "internal server error";
                return new APIResponseDTO("error",response);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static APIResponseDTO saveContact(String username, String channel, String address, String consent) {
        log.info("Preparing to create or update user Contact details");
        String response;
        String sql = "{call identification_pkg.saveContact(?,?,?,?,?)}";
        try (Connection con = Connect.dbase();
             CallableStatement stmt = con.prepareCall(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, channel);
            stmt.setString(3, address);
            stmt.setString(4, consent);
            stmt.registerOutParameter(5, OracleTypes.VARCHAR);
            stmt.execute();
            response = stmt.getString(5);
            if(response.equalsIgnoreCase("success")){
                response = "success";
                return new APIResponseDTO(response,"contact detail saved successfully");
            } else {
                response = "unsuccessful";
                return new APIResponseDTO(response,"contact detail not saved");
            }
        } catch (SQLException err) {
            log.error("Error in identification_pkg (SAVE CONTACT)", err);
            try {
                Spectre.recordError("TE-20001", "Error in identification_pkg (SAVE CONTACT): " + err.getMessage(), PersonaDAO.class.getName());
                response = "internal server error";
                return new APIResponseDTO("error",response);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static APIResponseDTO saveImage(String uname, InputStream avatarStream) {
        log.info("Preparing to create or update user avatar");
        String response;
        String sql = "{call identification_pkg.saveImage(?,?,?)}";
        try (Connection con = Connect.dbase();
             CallableStatement stmt = con.prepareCall(sql)) {
            stmt.setString(1, uname);
            stmt.setBlob(2, avatarStream);
            stmt.registerOutParameter(3, OracleTypes.VARCHAR);
            stmt.execute();
            response = stmt.getString(3);
            if(response.equalsIgnoreCase("success")){
                response = "success";
                return new APIResponseDTO(response,"image saved successfully");
            } else {
                response = "unsuccessful";
                return new APIResponseDTO(response,"image not saved");
            }
        } catch (SQLException err) {
            log.error("Error in identification_pkg (SAVE IMAGE)", err);
            try {
                Spectre.recordError("TE-20001", "Error in identification_pkg (SAVE IMAGE): " + err.getMessage(), PersonaDAO.class.getName());
                response = "internal server error";
                return new APIResponseDTO("error",response);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static APIResponseDTO savePerson(String username, String title, String firstName,
                                             String middleName, String lastName, String gender, String birthday) {
        log.info("Preparing to create or update user persons record");
        String response;
        String sql = "{call identification_pkg.savePerson(?,?,?,?,?,?,?,?)}";
        try (Connection con = Connect.dbase();
             CallableStatement stmt = con.prepareCall(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, title);
            stmt.setString(3, firstName);
            stmt.setString(4, middleName);
            stmt.setString(5, lastName);
            stmt.setString(6, gender);
            stmt.setString(7, birthday);
            stmt.registerOutParameter(8, OracleTypes.VARCHAR);
            stmt.execute();
            response = stmt.getString(8);
            if(response.equalsIgnoreCase("success")){
                response = "success";
                return new APIResponseDTO(response,"person detail saved successfully");
            } else {
                response = "unsuccessful";
                return new APIResponseDTO(response,"person detail not saved successfully");
            }
        } catch (SQLException err) {
            log.error("Error in identification_pkg (SAVE PERSON)", err);
            try {
                Spectre.recordError("TE-20001", "Error in identification_pkg (SAVE PERSON): " + err.getMessage(), PersonaDAO.class.getName());
                response = "internal server error";
                return new APIResponseDTO("error",response);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static AddressResponseDTO getAddress(String username) {
        log.info("Preparing to retrieve user Address details");
        AddressResponseDTO response = null;
        String sql = "{call identification_pkg.getAddress(?,?)}";
        try (Connection con = Connect.dbase();
             CallableStatement stmt = con.prepareCall(sql)) {
            stmt.setString(1, username);
            stmt.registerOutParameter(2, OracleTypes.CURSOR);
            stmt.execute();
            try (ResultSet rs = (ResultSet) stmt.getObject(2)) {
                if (rs != null && rs.next()) {
                    response = new AddressResponseDTO();
                    response.setUsername("username");
                    response.setFirstline("firstline");
                    response.setSecondline("secondline");
                    response.setThirdline("thirdline");
                    response.setCity("city");
                    response.setPostcode("postcode");
                    response.setCountry("country");
                    response.setModifiedBy("modifiedBy");
                    response.setModifiedDate("modifiedDate");
                }
            }
        } catch (SQLException err) {
            log.error("Error in identification_pkg (GET ADDRESS)", err);
            try {
                Spectre.recordError("TE-20001", err.getMessage(), PersonaDAO.class.getName());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return response;
    }

    public static List<ContactResponseDTO> getContact(String username) {
        log.info("Preparing to retrieve user Contact details");
        List<ContactResponseDTO> response =  new ArrayList<>();
        String sql = "{call identification_pkg.getContact(?,?)}";
        try (Connection con = Connect.dbase();
             CallableStatement stmt = con.prepareCall(sql)) {
            stmt.setString(1, username);
            stmt.registerOutParameter(2, OracleTypes.CURSOR);
            stmt.execute();
            try (ResultSet rs = (ResultSet) stmt.getObject(2)) {
                while (rs != null && rs.next()) {
                    ContactResponseDTO contact = new ContactResponseDTO();
                    contact.setUsername(rs.getString("username"));
                    contact.setChannel(rs.getString("channel"));
                    contact.setAddress(rs.getString("address"));
                    contact.setConsent(rs.getString("consent"));
                    contact.setModifiedBy(rs.getString("modifiedBy"));
                    contact.setModifiedDate(rs.getString("modifiedDate"));
                    response.add(contact);
                }
            }
        } catch (SQLException err) {
            log.error("Error in identification_pkg (GET CONTACT)", err);
            try {
                Spectre.recordError("TE-20001", err.getMessage(), PersonaDAO.class.getName());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return response;
    }

    public static ImageResponseDTO getImage(String username) {
        log.info("Preparing to retrieve user Avatar details");
        ImageResponseDTO response = null;
        String sql = "{call identification_pkg.getImage(?,?,?)}";
        try (Connection con = Connect.dbase();
             CallableStatement stmt = con.prepareCall(sql)) {
            stmt.setString(1, username);
            stmt.registerOutParameter(2, OracleTypes.CURSOR);
            stmt.execute();
            try (ResultSet rs = (ResultSet) stmt.getObject(2)) {
                if (rs != null && rs.next()) {
                    response = new ImageResponseDTO();
                    response.setUsername(rs.getString("username"));
                    response.setModifiedBy(rs.getString("modifiedBy"));
                    response.setModifiedDate(rs.getString("modifiedDate"));
                    Blob blob = rs.getBlob("avatar");
                    if (blob != null) {
                        byte[] bytes = blob.getBytes(1, (int) blob.length());
                        blob.free();
                        String base64 = Base64.getEncoder().encodeToString(bytes);
                        response.setAvatar("data:image/png;base64," + base64);
                    } else {
                        response.setAvatar(null);
                    }
                }
            }
        } catch (SQLException err) {
            log.error("Error in identification_pkg (GET IMAGE)", err);
            try {
                Spectre.recordError("TE-20001", err.getMessage(), PersonaDAO.class.getName());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return response;
    }

    public static PersonResponseDTO getPerson(String username) {
        log.info("Preparing to retrieve user Person details");
        PersonResponseDTO response = null;
        String sql = "{call identification_pkg.getPerson(?,?)}";
        try (Connection con = Connect.dbase();
             CallableStatement stmt = con.prepareCall(sql)) {
            stmt.setString(1, username);
            stmt.registerOutParameter(2, OracleTypes.CURSOR);
            stmt.execute();
            try (ResultSet rs = (ResultSet) stmt.getObject(2)) {
                if (rs != null && rs.next()) {
                    response = new PersonResponseDTO();
                    response.setUsername("username");
                    response.setTitle("title");
                    response.setFirstName("firstName");
                    response.setMiddleName("middleName");
                    response.setLastName("lastName");
                    response.setZodiacSign("zodiacSign");
                    response.setGender("gender");
                    response.setBirthday("birthday");
                    response.setAge("age");
                    response.setModifiedBy("modifiedBy");
                    response.setModifiedDate("modifiedDate");
                }
            }
        } catch (SQLException err) {
            log.error("Error in identification_pkg (GET PERSON)", err);
            try {
                Spectre.recordError("TE-20001", err.getMessage(), PersonaDAO.class.getName());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return response;
    }

    public static APIResponseDTO removeAddress(String username) {
        log.info("Preparing to remove user Address details");
        String response;
        String sql = "{call identification_pkg.removeAddress(?,?)}";
        try (Connection con = Connect.dbase();
             CallableStatement stmt = con.prepareCall(sql)) {
            stmt.setString(1, username);
            stmt.registerOutParameter(2, OracleTypes.VARCHAR);
            stmt.execute();
            response = stmt.getString(2);
            if (response.equalsIgnoreCase("success")) {
                response = "success";
                return new APIResponseDTO(response,"address detail removed successfully");
            } else {
                response = "unsuccessful";
                return new APIResponseDTO(response,"address detail not removed successfully");
            }
        } catch (SQLException err) {
            log.error("Error in identification_pkg (DELETE ADDRESS)", err);
            try {
                Spectre.recordError("TE-20001", "Error in identification_pkg (DELETE ADDRESS): " + err.getMessage(), PersonaDAO.class.getName());
                response = "internal server error";
                return new APIResponseDTO("error",response);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static APIResponseDTO removeContact(String username, String channel) {
        log.info("Preparing to remove user Contact details");
        String response;
        String sql = "{call identification_pkg.removeContact(?,?,?)}";
        try (Connection con = Connect.dbase();
             CallableStatement stmt = con.prepareCall(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, channel);
            stmt.registerOutParameter(3, OracleTypes.VARCHAR);
            stmt.execute();
            response = stmt.getString(3);
            if (response.equalsIgnoreCase("success")) {
                response = "success";
                return new APIResponseDTO(response,"contact detail removed successfully");
            } else {
                response = "unsuccessful";
                return new APIResponseDTO(response,"contact detail not removed successfully");
            }
        } catch (SQLException err) {
            log.error("Error in identification_pkg (DELETE CONTACT)", err);
            try {
                Spectre.recordError("TE-20001", "Error in identification_pkg (DELETE CONTACT): " + err.getMessage(), PersonaDAO.class.getName());
                response = "internal server error";
                return new APIResponseDTO("error",response);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static APIResponseDTO removeImage(String username) {
        log.info("Preparing to remove user Avatar details");
        String response;
        String sql = "{call identification_pkg.removeImage(?,?)}";
        try (Connection con = Connect.dbase();
             CallableStatement stmt = con.prepareCall(sql)) {
            stmt.setString(1, username);
            stmt.registerOutParameter(2, OracleTypes.VARCHAR);
            stmt.execute();
            response = stmt.getString(2);
            if (response.equalsIgnoreCase("success")) {
                response = "success";
                return new APIResponseDTO(response,"image removed successfully");
            } else {
                response = "unsuccessful";
                return new APIResponseDTO(response,"image not removed successfully");
            }
        } catch (SQLException err) {
            log.error("Error in identification_pkg (DELETE IMAGE)", err);
            try {
                Spectre.recordError("TE-20001", "Error in identification_pkg (DELETE IMAGE): " + err.getMessage(), PersonaDAO.class.getName());
                response = "internal server error";
                return new APIResponseDTO("error",response);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static APIResponseDTO removePerson(String username) {
        log.info("Preparing to remove user Person details");
        String response;
        String sql = "{call identification_pkg.removePerson(?,?)}";
        try (Connection con = Connect.dbase();
             CallableStatement stmt = con.prepareCall(sql)) {
            stmt.setString(1, username);
            stmt.registerOutParameter(2, OracleTypes.VARCHAR);
            stmt.execute();
            response = stmt.getString(2);
            if (response.equalsIgnoreCase("success")) {
                response = "success";
                return new APIResponseDTO(response,"person detail removed successfully");
            } else {
                response = "unsuccessful";
                return new APIResponseDTO(response,"person detail not removed successfully");
            }
        } catch (SQLException err) {
            log.error("Error in identification_pkg (DELETE PERSON)", err);
            try {
                Spectre.recordError("TE-20001", "Error in identification_pkg (DELETE PERSON): " + err.getMessage(), PersonaDAO.class.getName());
                response = "internal server error";
                return new APIResponseDTO("error",response);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}