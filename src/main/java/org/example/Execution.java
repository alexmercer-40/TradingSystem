package org.example;

import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;
import org.beryx.textio.TextTerminal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This is OrderBook class used for operation in Execution Table
 */
public class Execution {
    private int executionID;
    private int uID;
    private int cID;
    private int noOfStocks;
    private double priceOfStock;
    private String status;

    /**
     * This function will display details of all the pending orders from execution table
     */
    void displayExecutionTable() throws SQLException {
        final TextIO textIo;
        textIo = TextIoFactory.getTextIO();
        TextTerminal<?> textTerminal = textIo.getTextTerminal();
        TextIO textIO = TextIoFactory.getTextIO();
        new Main().accept(textIO, null);

        try(Connection conn = DBUtil.provideConnection()) {
            PreparedStatement ps = conn.prepareStatement("select * from exchange");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                textTerminal.println();

                textTerminal.println(" Pending Order ID :      " + rs.getInt(1));
                textTerminal.println(" User ID          :      " + rs.getInt(2));
                textTerminal.println(" Company ID       :      " + rs.getInt(3));
                textTerminal.println(" No Of Stocks     :      " + rs.getInt(4));
                textTerminal.println(" Stock Price      :      " + rs.getDouble(5));
                textTerminal.println(" Order Type       :      " + rs.getString(6));
                textTerminal.println();

            }
        }
    }

    /**
     * Getters and setters for achieving the Abstraction
     */
    public int getExecutionID() {
        return executionID;
    }

    public void setExecutionID(int executionID) {
        this.executionID = executionID;
    }

    public int getuID() {
        return uID;
    }

    public void setuID(int uID) {
        this.uID = uID;
    }

    public int getcID() {
        return cID;
    }

    public void setcID(int cID) {
        this.cID = cID;
    }

    public int getNoOfStocks() {
        return noOfStocks;
    }

    public void setNoOfStocks(int noOfStocks) {
        this.noOfStocks = noOfStocks;
    }

    public double getPriceOfStock() {
        return priceOfStock;
    }

    public void setPriceOfStock(double priceOfStock) {
        this.priceOfStock = priceOfStock;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
