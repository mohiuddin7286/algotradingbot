package org.example;

import java.sql.*;

public class Main {

    public static void main(String[] args) throws Exception {

        String url = "jdbc:mysql://localhost:3306/myjdbc";
        String uname = "root";
        String pass = "@mohi#12";

        int userid = 4;
        String user = "Mohi";
        int empid = 8026;

        String query = "INSERT INTO faculty VALUES (?,?,?)";

        // Load driver
        Class.forName("com.mysql.cj.jdbc.Driver");

        // Create connection
        Connection con = DriverManager.getConnection(url, uname, pass);

        // Prepared statement
        PreparedStatement st = con.prepareStatement(query);
        st.setInt(1, userid);
        st.setString(2, user);
        st.setInt(3, empid);

        int count = st.executeUpdate();

        System.out.println(count + " row inserted");

        st.close();
        con.close();
    }
}