package com.bank;

public enum DBError {
    NOT_ENOUGH_MONEY("Not enough money!"),
    SAME_ACCOUNT("You can't transfer money to the same account!"),
    INCORRECT_CHECKSUM("Probably you made a mistake in the card number. Please try again!"),
    CARD_DOESNT_EXIST("Such a card does not exist."),
    DB_ERROR("Something went wrong!");

    String error;

    DBError(String error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return this.error;
    }
}
