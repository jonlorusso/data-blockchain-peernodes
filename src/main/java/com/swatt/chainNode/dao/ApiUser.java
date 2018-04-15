package com.swatt.chainNode.dao;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.CallableStatement;
/*  =============  DO NOT EDIT ANY OF THIS FILE (UNLESS YOU REALLY WANT TO)  ================= 
 * 
 *   THIS IS MANUALLY-GENERATED CODE WAS CREATED LIKE gerrySeidman.tools.sql.ExcelSqlCodegen
 * 
 *  =================================  EDIT ANY OF THIS FILE  ================================ 
 */
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.xml.bind.DatatypeConverter;

public class ApiUser {
    private String firstName;
    private String lastName;
    private String companyName;
    private int companyId;
    private String email;

    public final String getFirstName() {
        return firstName;
    }

    public final String getLastName() {
        return lastName;
    }

    public final String getEmail() {
        return email;
    }

    public final String getCompanyName() {
        return companyName;
    }

    private final int getCompanyId() {
        return companyId;
    }

    private static String getAuthProcedureName() {
        return "Auth";
    }

    public ApiUser(ResultSet rs) throws SQLException {
        email = rs.getString(1);
        firstName = rs.getString(2);
        lastName = rs.getString(3);
        companyId = rs.getInt(4);
        companyName = rs.getString(5);
    }

    private static String AUTH_QUERY = "CALL " + getAuthProcedureName() + "(?, ?)";

    public static ApiUser auth(Connection connection, String email, String pwHash) throws SQLException {
        CallableStatement cs = connection.prepareCall(AUTH_QUERY);

        String pwHashSeed = email + pwHash;
        String pwHashFinal = null;

        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
            md.update(pwHashSeed.getBytes());
            byte[] digest = md.digest();
            pwHashFinal = DatatypeConverter.printHexBinary(digest).toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        cs.setString(1, email);
        cs.setString(2, pwHashFinal);

        ResultSet rs = cs.executeQuery();

        if (rs.next())
            return new ApiUser(rs);
        else
            return null;
    }
}