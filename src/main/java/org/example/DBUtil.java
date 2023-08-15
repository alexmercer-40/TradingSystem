package org.example;
import java.sql.*;

/**
 * This Class basically is used to make connection to database
 */

public class DBUtil {

    /**
     * This function will make connection to the database and return the  connection object
     */
    public static Connection provideConnection() {

        Connection conn=null;
        /**
         * It searches if the JDBC drivers are present in the Dependencies or not
         * If not present throw the corresponding SQL exception
         */
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        /**
         * This String url is the local address of the database
         */
        String url="jdbc:mysql://localhost:3306/TradingSystem";

        /**
         * Now it try to make connection with the database with user id and password
         * otherwise throw the exception
         */

        try {
            conn= DriverManager.getConnection(url,"root","root");
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        /**
         * Return the connection object
         */
        return conn;
    }
}
