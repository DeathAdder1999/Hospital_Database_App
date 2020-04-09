package database;

import java.sql.*;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;

public class DBConnection
{
    private final static String URL ="jdbc:postgresql://comp421.cs.mcgill.ca:5432/cs421";
    private final static String USERNAME = "cs421g56";
    private final static String PASSWORD = "dbSQLHospital421";

    private Connection connection;
    private Statement statement;
    private boolean established = false;

    public void connect()
    {
        if(established)
        {
            System.out.println("Already connected!");
            return;
        }

        try
        {
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            statement = connection.createStatement();
            established = true;
        }
        catch(SQLException e)
        {
            System.out.println("ERROR! Failed to connect: " + e.getMessage() + "\n Code: " + e.getErrorCode() + " sqlState: " + e.getSQLState());
        }
    }

    public void disconnect()
    {
        try
        {
            statement.close();
            connection.close();
        }
        catch(Exception e)
        {
            //Do nothing
        }
    }

    public boolean isEstablished()
    {
        return established;
    }



}
