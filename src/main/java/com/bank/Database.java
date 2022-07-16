package com.bank;

import org.sqlite.SQLiteDataSource;
import java.sql.*;

public class Database {
    public static Connection con;

    public Database(String filename) {
       con = connect(filename);
    }

    public Connection connect(String filename) {
        String url = "jdbc:sqlite:" + filename;

        Connection con = null;
        Statement statement = null;
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(url);
        try  {
            Class.forName("org.sqlite.JDBC");
            con = dataSource.getConnection();
            statement = con.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS card(" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "number TEXT NOT NULL," +
                    "pin TEXT NOT NULL," +
                    "balance INT DEFAULT 0)");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return con;
    }
    public boolean close(Card card) {
        String sql = "DELETE FROM card WHERE number = ? and pin = ?";
        try {
            con.setAutoCommit(false);
            try (PreparedStatement deleteQuery = con.prepareStatement(sql)){
                deleteQuery.setString(1, card.toString());
                deleteQuery.setString(2, card.getPIN());
                deleteQuery.executeUpdate();

                con.commit();
            } catch (SQLException e) {
                System.out.println("Something went wrong!");
            }
        } catch (SQLException e) {
            System.out.println("Something went wrong!");
        }
        return true;
    }
    public boolean addIncome(String number, int amount) {
        String sql = "UPDATE card SET balance = balance + ? WHERE number = ?;";
        try {
            con.setAutoCommit(false);
            try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
                preparedStatement.setInt(1, amount);
                preparedStatement.setString(2, number);
                preparedStatement.executeUpdate();
                con.commit();
                return true;
            } catch (SQLException e) {
                System.out.println("Something went wrong!");
                e.printStackTrace();
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Something went wrong!");
            e.printStackTrace();
            return false;
        }
    }
    public void getAll() {
        String query = "SELECT * FROM card";
        try (Statement statement = con.createStatement()){
            ResultSet set = statement.executeQuery(query);
            while(set.next())
                System.out.println(set.getString("number") + " " + set.getString("pin") + " " + set.getInt("balance"));
        } catch (SQLException e){
            e.printStackTrace();
        }
    }
    public void save(Card card) {
        String sql = "INSERT INTO card(number, pin) VALUES (?, ?)";
        try(PreparedStatement query = con.prepareStatement(sql)) {
            query.setString(1, card.toString());
            query.setString(2, card.getPIN());
            query.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public Card check(String number, String pin) {
        String sql = "SELECT * from card WHERE number = ?;";
        try (PreparedStatement query = con.prepareStatement(sql)) {
            query.setString(1, number);
            ResultSet res = query.executeQuery();
            if(res.next()) {
                String actualPin = res.getString("pin");
                if (actualPin != null && actualPin.equals(pin))
                    return new Card(number, actualPin, res.getInt("balance"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    private boolean withDraw(Card card, int amount) {
        String sql = "UPDATE card SET balance = balance - ? WHERE number = ?";

        try(PreparedStatement query = con.prepareStatement(sql)) {
            query.setInt(1, amount);
            query.setString(2, card.toString());
            query.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public String doTransfer(Card sender, String receiverNum, int amount) {
        String selectCard = "Select * FROM card WHERE number = ?";
        Card receiverCard = null;
        try {
            con.setAutoCommit(false);
            try (PreparedStatement query = con.prepareStatement(selectCard)){
                query.setString(1, sender.toString());
                ResultSet senderSet = query.executeQuery();
                query.setString(1, receiverNum);
                ResultSet receiverSet = query.executeQuery();

                if(getBalance(sender.toString()) - amount < 0)
                    return DBError.NOT_ENOUGH_MONEY.toString();
                if(!withDraw(sender, amount) || !addIncome(receiverNum, amount))
                    return DBError.DB_ERROR.toString();
                con.commit();
                return "Success!";
            } catch (SQLException e) {
                e.printStackTrace();
                return DBError.DB_ERROR.toString();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return DBError.DB_ERROR.toString();
        }

    }
    public int getBalance(String number) {
        String sql = "SELECT balance FROM card WHERE number = ?";
        int balance = 0;
        try(PreparedStatement query = con.prepareStatement(sql)) {
            query.setString(1, number);
            ResultSet set = query.executeQuery();

            if(set.next()) {
                return set.getInt("balance");
            }
        } catch (SQLException e) {
            System.out.println("Something went wrong!");
            e.printStackTrace();
            return -1;
        }
        return -1;
    }
    public boolean find(String number) {
        String sql = "SELECT number, pin FROM card WHERE number = ?";

        try(PreparedStatement query = con.prepareStatement(sql)) {
            query.setString(1, number);
            ResultSet set = query.executeQuery();

            if(set.next()) {
                if ( number.equals(set.getString("number"))) {
                    return true;
                }
            }
        } catch (SQLException e) {
            System.out.println("Something went wrong!");
            e.printStackTrace();
        }
        return false;
    }

    public int getBalance(Card card) {
        String sql = "SELECT balance FROM card WHERE number = ? and pin = ?";
        try (PreparedStatement query = con.prepareStatement(sql)) {
            query.setString(1, card.toString());
            query.setString(2, card.getPIN());
            ResultSet set = query.executeQuery();
            if(set.next()) {
                return set.getInt("balance");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public void close() {
        try {
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
