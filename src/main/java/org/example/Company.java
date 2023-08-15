package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.io.*;
import java.util.*;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;
import org.beryx.textio.TextTerminal;
import org.beryx.textio.web.RunnerData;
import java.sql.*;

/**
 * This is Company class through we are going to perform required operations from Company Table
 */

public class Company extends DBUtil {

    /**
     * The data members declared in this class as private
     */

    private int companyID;
    private String companyName;
    private Double stockPrice;
    private Double noOfStocks;

    /**
     * This function is used to fetch company name from a particular company ID
     */

    String fetchCompanyName(int companyId) {
        String res="";
        try (Connection conn = DBUtil.provideConnection()) {
            PreparedStatement ps = conn.prepareStatement("select * from company where c_id = ?");
            ps.setInt(1, companyId);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                res = rs.getString("c_name");
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return res;
    }

    /**
     * This function is used to fetch Stock Price of a particular stock
     */

    double fetchStockPrice(int companyId) {
        double res =0.0;
        try (Connection conn = DBUtil.provideConnection()) {
            PreparedStatement ps = conn.prepareStatement("select * from company where c_id = ?");
            ps.setInt(1, companyId);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                res = rs.getDouble("single_stock");
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return res;
    }


    /**
     * This function will display All company details
     */
        void printCompanyDetails() throws SQLException
        {
        final TextIO textIo;
        textIo = TextIoFactory.getTextIO();
        TextTerminal<?> textTerminal = textIo.getTextTerminal();
        TextIO textIO = TextIoFactory.getTextIO();
        new Main().accept(textIO, null);

        try(Connection conn = DBUtil.provideConnection()) {
            PreparedStatement ps = conn.prepareStatement("select * from company");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                textTerminal.println((" Company Name :         " + rs.getString("c_name").toUpperCase()));
                textTerminal.println(" Company ID :           " + rs.getInt("c_id"));
                textTerminal.println(" Available Stocks :     " + rs.getDouble("a_stock"));
                textTerminal.println(" Stock Price :          " + rs.getDouble("single_stock"));

            }
        }
    }

    /**
     * Getters and setters for achieving the Abstraction
     */

    public int getCompanyID() {
        return companyID;
    }

    public void setCompanyID(int companyID) {
        this.companyID = companyID;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Double getStockPrice() {
        return stockPrice;
    }

    public void setStockPrice(Double stockPrice) {
        this.stockPrice = stockPrice;
    }

    public Double getNoOfStocks() {
        return noOfStocks;
    }

    public void setNoOfStocks(Double noOfStocks) {
        this.noOfStocks = noOfStocks;
    }
}
