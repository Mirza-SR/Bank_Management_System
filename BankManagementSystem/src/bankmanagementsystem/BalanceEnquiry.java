package bankmanagementsystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class BalanceEnquiry extends JFrame implements ActionListener {

    JButton back;
    String pinchange;

    BalanceEnquiry(String pinnumber) {
        this.pinchange = pinnumber;
        setLayout(null);

        // Set background image
        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("icons/atm.jpg"));
        Image i2 = i1.getImage().getScaledInstance(900, 900, Image.SCALE_DEFAULT);
        ImageIcon i3 = new ImageIcon(i2);
        JLabel image = new JLabel(i3);
        image.setBounds(0, 0, 900, 900);
        add(image);

        // Back button
        back = new JButton("Back");
        back.setBounds(355, 520, 150, 30);
        back.addActionListener(this);
        image.add(back);

        // Database connection and balance calculation
        Conn c = new Conn();
        int balance = 0; // Initializing balance to 0
        try {
            // Query to get all transactions for the provided pin number
            ResultSet rs = c.s.executeQuery("SELECT * FROM bank WHERE pin = '" + pinnumber + "'");

            boolean transactionsExist = false; // Flag to check if any transactions exist

            // Loop through result set to calculate balance
            while (rs.next()) {
                transactionsExist = true; // There are transactions for this pin
                if (rs.getString("type").equals("Deposit")) {
                    balance += Integer.parseInt(rs.getString("amount"));
                } else {
                    balance -= Integer.parseInt(rs.getString("amount"));
                }
            }

            // If no transactions exist, balance will stay at 0
            if (!transactionsExist) {
                balance = 0;
            }

            // Display the balance
            JLabel text = new JLabel("Your Current Account balance is TK " + balance);
            text.setForeground(Color.WHITE);
            text.setBounds(170, 300, 400, 30);
            image.add(text);

        } catch (Exception e) {
            e.printStackTrace(); // Print error to console if there is an issue
        }

        // Set JFrame properties
        setSize(900, 900);
        setLocation(300, 0);
        setUndecorated(true); // Optional, for borderless window
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        setVisible(false);
        new Transactions(pinchange).setVisible(true); // Navigate to Transactions screen
    }

    public static void main(String args[]) {
        new BalanceEnquiry(""); // Pass a valid pin number to test
    }
}
