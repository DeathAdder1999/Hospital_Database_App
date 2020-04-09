package com.company;

import database.DBConnection;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class App
{
    private Scanner scanner = new Scanner(System.in);
    private DBConnection connection = new DBConnection();

    public void start()
    {
        try
        {
            DriverManager.registerDriver(new org.postgresql.Driver());
        }
        catch (Exception e)
        {
            System.out.println("FATAL ERROR: " + e.getMessage());
            System.out.println("Exiting...");

            quit(-1);
            return;
        }

        System.out.println("Welcome to Hospital Database App");

        int connectionAttempts = 0;

        while(!connection.isEstablished())
        {
            if(connectionAttempts >= 3)
            {
                System.out.println("It seems that connection cannot be established, do you want to quit? (Y/N)");

                String answer = scanner.nextLine();

                while(answer.isEmpty())
                {
                    System.out.println("Please enter a valid answer:");
                    answer = scanner.nextLine();
                }

                if(!answer.toLowerCase().equals("y"))
                {
                    quit(0);
                }
            }


            System.out.println("Trying to connect...");
            connection.connect();

            connectionAttempts++;
        }

        System.out.println("Connection was successfully established");

        String userInput = scanner.nextLine();

        //Main loop
        while(true)
        {
            printOptions();
            handleUserInput(userInput);
        }

    }

    public void printOptions()
    {

        System.out.println("Please choose one of the following options by typing corresponding number.");
        System.out.println("1. Get total number of patients of a hospital");
        System.out.println("2. Get staff member by eid of a hospital");
        System.out.println("3. Add a patient to the hospital");
        System.out.println("4. Get full info of the patients with a particular disease");
        System.out.println("5. Place Holder");
        System.out.println("6. Place Holder");
        System.out.println("7. Quit");
    }

    public void handleUserInput(String userInput)
    {
        //TODO implement
    }


    public void quit(int status)
    {
        connection.disconnect();

        //Exit
        System.exit(status);
    }


}
