package com.bank;

import java.util.*;

// ИСПРАВИТЬ ПОВТОР МЕНЮ!
//

public class BankingSystem {
    private List<Card> cards;
    private Database db;
    private Scanner sc;

    public BankingSystem(Database db) {
        this.sc = new Scanner(System.in);
        this.cards = new ArrayList<>();
        this.db = db;
        startPoint();
    }
    public void startPoint() {
        String input;
        while(true) {
            System.out.print("1. Create an account\n" +
                    "2. Log into account\n" +
                    "0. Exit\n");
            input = sc.nextLine();
            if(input.equals("1")) {
                Card newCard = generateNewCard();
                db.save(newCard);
                System.out.printf("Your card has been created\n" +
                        "Your card number:\n" +
                        "%s\n" +
                        "Your card PIN:\n" +
                        "%s\n", newCard.toString(), newCard.getPIN());
            } else if (input.equals("2")) {
                System.out.println("Enter your card number:");
                String number = sc.nextLine();
                System.out.println("Enter your PIN:");
                String pin = sc.nextLine();
                Card currentCard = db.check(number, pin);
                if(currentCard != null) {
                    System.out.println("You have successfully logged in!");
                    accountMenu(currentCard);
                }
                else
                    System.out.println("Wrong card number or PIN!");

            } else if(input.equals("0")) {
                System.out.println("Bye!");
                db.close();
                System.exit(0);
            } else if(input.equals("10"))
                db.getAll();
        }
    }

    public void accountMenu(Card card) {
        String input;
        while(true) {
            System.out.print("1. Balance\n" +
                    "2. Add income\n" +
                    "3. Do transfer\n" +
                    "4. Close account\n" +
                    "5. Log out\n" +
                    "0. Exit\n");
            input = sc.nextLine();
            if(input.equals("1"))
                System.out.printf("Balance: %d\n", db.getBalance(card.toString()));
            else if(input.equals("5"))
                startPoint();
            else if(input.equals("2")) {
                addIncome(card);
            }
            else if(input.equals("3")) {
                doTransfer(card);
            }
            else if(input.equals("4")) {
                close(card);
            }
            else if(input.equals("0")) {
                System.out.println("Bye!");
                db.close();
                System.exit(0);
            }
        }


    }
    private void doTransfer(Card card) {
        System.out.println("Transfer\n" +
                "Enter card number:");
        String receiverNum = sc.nextLine();
        int receiverCheckSum = receiverNum.charAt(receiverNum.length() - 1) - 48;
        if( receiverCheckSum != Card.LuhnChecker(receiverNum.substring(0, receiverNum.length() - 1))) {
            System.out.println(DBError.INCORRECT_CHECKSUM.toString());
            return;
        }
        if(!db.find(receiverNum)) {
            System.out.println(DBError.CARD_DOESNT_EXIST.toString());
            return;
        }
        if(card.toString().equals(receiverNum)) {
            System.out.println(DBError.SAME_ACCOUNT.toString());
            return;
        }
        System.out.println("Enter how much money you want to transfer:");
        int amount = sc.nextInt();
        System.out.println(db.doTransfer(card, receiverNum, amount));
    }
    private void close(Card card) {
        if(db.close(card))
            System.out.println("The account has been closed!");
        startPoint();
    }
    private void addIncome(Card card) {
        System.out.println("Enter income:");
        int income = sc.nextInt();
        if(db.addIncome(card.toString(), income))
            System.out.println("Income was added!");
    }
    public Card generateNewCard() {
        Card newCard = new Card();
        while(cards.contains(newCard))
            newCard = new Card();
        cards.add(newCard);

        return newCard;
    }

    public Card checkCard(String number, String pin) {
        for(Card i : cards) {
            if(i.toString().equals(number) && i.getPIN().equals(pin)) {
                return i;
            }
        }
        return null;
    }
}
