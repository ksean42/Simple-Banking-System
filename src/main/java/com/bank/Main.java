package com.bank;

public class Main {
    public static void main(String[] args) {
        String filename = null;
        try {
            filename = args[1];
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Please, specify db filename using -fileName argument");
            System.exit(1);
        }

        Database db = new Database(filename);
        BankingSystem bankingSystem = new BankingSystem(db);
        db.close();
    }
}