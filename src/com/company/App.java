package com.company;

import database.DBConnection;
import utils.Utils;

import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class App {
    private Scanner scanner = new Scanner(System.in);
    private DBConnection connection = new DBConnection();

    public void start() {
        try {
            DriverManager.registerDriver(new org.postgresql.Driver());
        } catch (Exception e) {
            System.out.println("FATAL ERROR: " + e.getMessage());
            System.out.println("Exiting...");

            quit(-1);
            return;
        }

        System.out.println("Welcome to Hospital Database App");

        int connectionAttempts = 0;

        while (!connection.isEstablished()) {
            if (connectionAttempts >= 3) {
                System.out.println("It seems that connection cannot be established, do you want to quit? (Y/N)");

                String answer = scanner.nextLine();

                while (answer.isEmpty()) {
                    System.out.println("Please enter a valid answer:");
                    answer = scanner.nextLine();
                }

                if (answer.toLowerCase().equals("y")) {
                    quit(0);
                }
            }


            System.out.println("Trying to connect...");
            connection.connect();

            connectionAttempts++;
        }

        System.out.println("Connection was successfully established");

        String userInput = "";

        //Main loop
        while (true) {
            printOptions();

            userInput = scanner.nextLine();

            handleUserInput(userInput);
        }

    }

    public void printOptions()
    {
        System.out.println("\nPlease choose one of the following options by typing the corresponding number.");
        System.out.println("1. Get the total capacity of a hospital.");
        System.out.println("2. Get patient by pid");
        System.out.println("3. Get staff member by eid.");
        System.out.println("4. Add a patient to the hospital.");
        System.out.println("5. Get information about the patients with a particular disease.");
        System.out.println("7. Quit");
    }

    public void handleUserInput(String userInput) {
        int input = -1;

        if (!Utils.isInteger(userInput))
        {
            System.out.println("Invalid input! Please choose a one of the options provided");
            return;
        }

        input = Integer.parseInt(userInput);

        try {
            switch (input) {
                case 1:
                    getNumOfPatients();
                    break;
                case 2:
                    getPatient();
                    break;
                case 3:
                    getStaffMember();
                    break;
                case 4:
                    addPatient();
                    break;
                case 5:
                    getPatientsWithDisease();
                    break;
                case 7:
                    quit(0);
                    break;
                default:
                    System.out.println("Invalid input! Please choose a one of the options provided");
                    break;
            }
        } catch (Exception e) {
            System.out.println("Unexpected error occurred while processing your request!");
        }
    }

    private void getPatient() throws SQLException
    {
        String pidRaw = "";

        while (!Utils.isInteger(pidRaw)) {
            System.out.println("Please enter an pid (Patient id): ");
            pidRaw = scanner.nextLine();
        }

        System.out.println("What information do you want to see? Name/Address/Registration Date/Phone Number/Insurance/status - enter (Y/Y/Y/Y/Y/Y or any other combination)");

        //Process modifications
        String modifications = scanner.nextLine();
        boolean[] mods = parseQueryMods(modifications, 6);

        while (mods == null)
        {
            System.out.println("Invalid Input. Please for each field indicate Y or N (ex: Y/Y/Y)");
            modifications = scanner.nextLine();
            mods = parseQueryMods(modifications, 6);
        }

        System.out.println("Processing request...");

        int pid = Integer.parseInt(pidRaw);

        String sql = "SELECT * FROM patient WHERE pid=" + pid;
        ResultSet result = connection.getQuery(sql);

        if (!result.next())
        {
            System.out.println("No staff member with eid: " + pid + " found.");
            return;
        }

        String toDisplay = "";

        if (mods[0])
        {
            String name = result.getString("name");
            toDisplay += "name: " + name + " | ";
        }

        if (mods[1])
        {
            String address = result.getString("address");
            toDisplay += "address: " + address + " | ";
        }

        if (mods[2])
        {
            Date registration_date = result.getDate("registration_date");
            String date = registration_date == null ? "null" : registration_date.toString();
            toDisplay += "Registration Date: " + date + " | ";
        }

        if (mods[3])
        {
            String phoneNum = result.getString("phone_no");
            toDisplay += "Personal Days: " + phoneNum + " | ";
        }

        if(mods[4])
        {
            String insurance = result.getString("insurance_prov");
            toDisplay += "Insurance Provider: " + insurance + " | ";
        }

        if(mods[5])
        {
            boolean status = result.getBoolean("status");
            toDisplay += "Status: " + status + " | ";
        }

        System.out.println(toDisplay);
    }

    private void getNumOfPatients() throws SQLException
    {
        String hidRaw = "";

        while (!Utils.isInteger(hidRaw)) {
            System.out.println("Please enter a hid (Hospital id):");
            hidRaw = scanner.nextLine();
        }

        System.out.println("Processing request...");
        int hid = Integer.parseInt(hidRaw);

        //Make sure that the hospital with the given id exists
        if (!recordExists("hospital", "hid", hid))
        {
            System.out.println("Hospital with id " + hid + " does not exist!");
            return;
        }

        int capacity = getHospitalCapacity(hid);

        System.out.println("Capacity of hospital with id: " + hid + " : " + capacity);
    }

    private void getStaffMember() throws SQLException
    {
        String eidRaw = "";

        while (!Utils.isInteger(eidRaw)) {
            System.out.println("Please enter an eid (Employee id): ");
            eidRaw = scanner.nextLine();
        }

        System.out.println("What information do you want to see? Name/Address/Hire Date/Personal Days - enter (Y/Y/Y/Y or any other combination)");

        //Process modifications
        String modifications = scanner.nextLine();
        boolean[] mods = parseQueryMods(modifications, 4);

        while (mods == null) {
            System.out.println("Invalid Input. Please for each field indicate Y or N (ex: Y/Y/Y)");
            modifications = scanner.nextLine();
            mods = parseQueryMods(modifications, 4);
        }

        System.out.println("Processing request...");
        int eid = Integer.parseInt(eidRaw);

        String sql = "SELECT * FROM staff WHERE eid=" + eid;
        ResultSet result = connection.getQuery(sql);

        if (!result.next())
        {
            System.out.println("No staff member with eid: " + eid + " found.");
            return;
        }

        String toDisplay = "";

        if (mods[0]) {
            String name = result.getString("name");
            toDisplay += "name: " + name + " | ";
        }

        if (mods[1]) {
            String address = result.getString("address");
            toDisplay += "address: " + address + " | ";
        }

        if (mods[2]) {
            Date hireDate = result.getDate("hire_date");
            String date = hireDate == null ? "null" : hireDate.toString();
            toDisplay += "Hire Date: " + date + " | ";
        }

        if (mods[3]) {
            int personalDays = result.getInt("personal_days");
            toDisplay += "Personal Days: " + personalDays + " | ";
        }

        System.out.println(toDisplay);
    }

    public void addPatient() throws SQLException
    {
        System.out.println("Enter pid:");
        String pidRaw = scanner.nextLine();
        String name = "";
        String address = "";
        String registrationDate = "";
        String phoneNum = "";
        String insuranceProvider = "";
        String statusRaw = "invalid_value";

        while(!Utils.isInteger(pidRaw))
        {
            System.out.println("Please enter a valid pid!");
            pidRaw = scanner.nextLine();
        }

        int pid = Integer.parseInt(pidRaw);

        while(recordExists("patient", "pid", pid))
        {
            int suggestedID = suggestID("patient", "pid");
            System.out.println("Patient with provided pid exists, try " + suggestedID);

            do {
                System.out.println("Please enter a valid pid!");
                pidRaw = scanner.nextLine();
            }while(!Utils.isInteger(pidRaw));

            pid = Integer.parseInt(pidRaw);
        }

        while(name.isEmpty())
        {
            System.out.println("Please enter patient's full name:");
            name = scanner.nextLine();
        }

        while(address.isEmpty())
        {
            System.out.println("Please enter patient's address:");
            address = scanner.nextLine();
        }

        while(!Utils.isDate(registrationDate))
        {
            System.out.println("Please enter patient's registration date:");
            registrationDate = scanner.nextLine();
        }

        while(!Utils.isPhoneNumber(phoneNum))
        {
            System.out.println("Please enter patient's phone number: ");
            phoneNum = scanner.nextLine();
        }

        System.out.println("Please enter insurance provide if any:");
        insuranceProvider = scanner.nextLine();

        while(!Utils.isBoolean(statusRaw.toLowerCase()))
        {
            System.out.println("Please provide status (True/False)");
            statusRaw = scanner.nextLine();
        }

        boolean status = Boolean.parseBoolean(statusRaw.toLowerCase());

        String attributes;
        String values;

        if(!insuranceProvider.isEmpty())
        {
            attributes = "(pid, name, address, registration_date, phone_no, insurance_prov, status)";
            values = "(" + pid + ", " + name + ", " + address + ", " + registrationDate + ", " + phoneNum + ", " + insuranceProvider + ", " + status + ")";
        }
        else
        {
            attributes = "(pid, name, address, registration_date, phone_no, status)";
            values = "( '" + pid + "', '" + name + "', '" + address + "', '" + registrationDate + "', '" + phoneNum +  "', '" + status + "')";
        }

        insert("patient", attributes, values);
    }

    private void getPatientsWithDisease() throws SQLException
    {
        String disease = "";

        while(disease.isEmpty())
        {
            System.out.println("Please enter the disease name:");
            disease = scanner.nextLine();
        }

        String sql = "SELECT COUNT(*) AS cases FROM (has INNER JOIN patient ON has.pid = patient.pid) WHERE has.name=" + "'" + disease + "'";
        ResultSet countResult = connection.getQuery(sql);

        int numCases = 0;

        if(countResult.next())
        {
            numCases = countResult.getInt("cases");
        }

        System.out.println("Total number of " + disease + " cases: " + numCases);

        sql = "SELECT has.pid, patient.name, patient.address FROM (has INNER JOIN patient ON has.pid = patient.pid) WHERE has.name=" + "'" + disease + "'";
        String displayResult = "";
        ResultSet getResult = connection.getQuery(sql);

        while(getResult.next())
        {
            String pid = "pid : " + getResult.getInt("pid") + " | ";
            String name = "name: " + getResult.getString("name") + " | ";
            String address = "address" + getResult.getString("address") + " | ";
            displayResult += pid + name + address + "\n";
        }

        System.out.println(displayResult);
    }

    /*
        Checks whether the string is of type Boolean/Boolean/Boolean/Boolean/Boolean where True - Y and False - N
     */
    private boolean[] parseQueryMods(String mods, int numMods) {
        boolean[] result = new boolean[numMods];
        String[] tokens = mods.split("/");

        if (tokens.length != numMods) {
            return null;
        }

        for (int i = 0; i < numMods; i++) {
            if (tokens[i].toLowerCase().equals("y")) {
                result[i] = true;
                continue;
            }

            if (tokens[i].toLowerCase().equals("n")) {
                result[i] = false;
                continue;
            }

            //Invalid token
            return null;
        }

        return result;
    }

    public void quit(int status) {
        System.out.println("Exiting...");
        connection.disconnect();

        //Exit
        System.exit(status);
    }

    /*
        Returns a number which represents the total capacity of a hospital
        Note: doesn't check if the given hid exists.
     */
    private int getHospitalCapacity(int hid) throws SQLException {
        String sql = "SELECT SUM(capacity) AS total_capacity FROM room WHERE hid=" + hid;

        ResultSet result = connection.getQuery(sql);

        int capacity = -1;

        if (result.next())
        {
            capacity = result.getInt("total_capacity");
        }

        result.close();

        return capacity;
    }

    private boolean recordExists(String tableName, String pk, int id) throws SQLException
    {
        String sql = "SELECT * FROM " + tableName + " WHERE " + pk + "=" + id;
        ResultSet result = connection.getQuery(sql);

        boolean exists = result.next();

        result.close();

        return exists;
    }

    public int suggestID(String tableName, String pk) throws SQLException
    {
        int id = 0;
        String sql = "SELECT " + pk +" from "+tableName+" WHERE "+pk+ " >= ALL(SELECT "+pk+" FROM "+tableName+")";
        ResultSet result = connection.getQuery(sql);

        if(result.next())
        {
            id = Integer.valueOf(result.getString(pk));
        }

        result.close();
        return ++id;
    }

    private void insert(String tableName, String attributes, String values)
    {
        String sql = "INSERT INTO " + tableName + " " + attributes + " VALUES " + values;

        if(connection.insertQuery(sql))
        {
            System.out.println("Patient successfully added!");
            return;
        }

        System.out.println("Failed to add patient");
    }
}
