package org.example;

import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;
import org.beryx.textio.TextTerminal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This is OrderBook class used for operation in OrderBook Table
 */

public class OrderBook {

    private int serialNo;
    private int uID;
    private int cId;
    private int noOfStocks;
    private double priceOfStock;
    private String orderType;


    /**
     * This function will display complete history of all the orders from OrderBook table
     */
    void displayOrderBook() throws SQLException {
        final TextIO textIo;
        textIo = TextIoFactory.getTextIO();
        TextTerminal<?> textTerminal = textIo.getTextTerminal();
        TextIO textIO = TextIoFactory.getTextIO();
        new Main().accept(textIO, null);

        try(Connection conn = DBUtil.provideConnection()) {
            PreparedStatement ps = conn.prepareStatement("select * from orderbook");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                textTerminal.println(" Order ID         :" + rs.getInt(1));
                textTerminal.println(" User ID          :" + rs.getInt(2));
                textTerminal.println(" Company ID       :" + rs.getInt(3));
                textTerminal.println(" No Of Stocks     :" + rs.getInt(4));
                textTerminal.println(" Stock Price      :" + rs.getDouble(5));
                textTerminal.println(" Order Type       :" + rs.getString(6));

            }
        }
    }

    /**
     * Getters and setters for achieving the Abstraction
     */

    public int getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(int serialNo) {
        this.serialNo = serialNo;
    }

    public int getuID() {
        return uID;
    }

    public void setuID(int uID) {
        this.uID = uID;
    }

    public int getcId() {
        return cId;
    }

    public void setcId(int cId) {
        this.cId = cId;
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

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }
}
