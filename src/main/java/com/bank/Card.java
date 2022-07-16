package com.bank;

import java.util.Random;

public class Card {
    final private String number;
    final private String pin;
    private int balance;

    public Card() {
        StringBuilder str = new StringBuilder();
        str.append("400000");
        str.append(generateNumber(9));
        str.append(LuhnAlgorithm(str.toString()));
        this.number = str.toString();
        this.pin = generateNumber(4);
        this.balance = 0;
    }
    public Card(String number, String pin, int balance) {
        this.number = number;
        this.pin = pin;
        this.balance = balance;
    }

    private String generateNumber(int n) {
        Random rd = new Random();
        StringBuilder number = new StringBuilder();
        for(int i = 0; i < n; i++) {
            number.append(String.valueOf(rd.nextInt(10)));
        }
        return number.toString();
    }

    public String LuhnAlgorithm(String num) {
        int[] arr = toArray(num);
        int sum = 0;

        for(int i = 0; i < arr.length; i++) {
            if ((i + 1) % 2 != 0)
                arr[i] *= 2;
            if(arr[i] > 9)
                arr[i] -= 9;
            sum += arr[i];
        }
        int x = 0;
        while(sum % 10 != 0) {
            sum++;
            x++;
        }
        return String.valueOf(x);
    }
    public static int LuhnChecker(String num) {
        int[] arr = toArray(num);
        int sum = 0;

        for(int i = 0; i < arr.length; i++) {
            if ((i + 1) % 2 != 0)
                arr[i] *= 2;
            if(arr[i] > 9)
                arr[i] -= 9;
            sum += arr[i];
        }
        int x = 0;
        while(sum % 10 != 0) {
            sum++;
            x++;
        }
        return x;
    }



    private static int[] toArray(String number) {
        int[] arr = new int[number.length()];
        for(int i = 0; i < number.length(); i++) {
            arr[i] = number.charAt(i) - 48;
        }
        return arr;
    }
    public String getPIN() {
        return this.pin;
    }
    public void setBalance(int i) {
        this.balance += i;
    }
    public int getBalance() {
        return this.balance;
    }

    public int getChecksum() {
        return this.number.charAt(number.length() - 1) - 48;
    }
    @Override
    public String toString() {
        return number;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return card.toString().equals(number);
    }

}
