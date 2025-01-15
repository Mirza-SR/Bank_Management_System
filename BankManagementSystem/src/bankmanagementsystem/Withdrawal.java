package bankmanagementsystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.sql.*;

public class Withdrawal extends JFrame implements ActionListener {

    JTextField amount;
    JButton withdraw, back;
    String pinnumber;

    Withdrawal(String pinnumber) {
        this.pinnumber = pinnumber;
        setLayout(null);

        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("icons/atm.jpg"));
        Image i2 = i1.getImage().getScaledInstance(900, 900, Image.SCALE_DEFAULT);
        ImageIcon i3 = new ImageIcon(i2);
        JLabel image = new JLabel(i3);
        image.setBounds(0, 0, 900, 900);
        add(image);

        JLabel text = new JLabel("Enter the amount you want to withdraw");
        text.setForeground(Color.WHITE);
        text.setFont(new Font("System", Font.BOLD, 16));
        text.setBounds(170, 300, 400, 20);
        image.add(text);

        amount = new JTextField();
        amount.setFont(new Font("Raleway", Font.BOLD, 22));
        amount.setBounds(170, 350, 320, 25);
        image.add(amount);

        withdraw = new JButton("Withdraw");
        withdraw.setBounds(355, 485, 150, 30);
        withdraw.addActionListener(this);
        image.add(withdraw);

        back = new JButton("Back");
        back.setBounds(355, 520, 150, 30);
        back.addActionListener(this);
        image.add(back);

        setSize(900, 900);
        setLocation(300, 0);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == withdraw) {
            String number = amount.getText();
            Date date = new Date();

            if (number.equals("")) {
                JOptionPane.showMessageDialog(null, "Please enter the amount you want to withdraw");
            } else {
                try {
                    // Validate the withdrawal amount
                    int withdrawalAmount = Integer.parseInt(number);
                    if (withdrawalAmount <= 0) {
                        JOptionPane.showMessageDialog(null, "Please enter a valid amount greater than 0");
                        return;
                    }

                    // Connect to the database
                    Conn conn = new Conn();

                    // Calculate the current balance
                    String balanceQuery = "SELECT * FROM bank WHERE pin = ?";
                    PreparedStatement balanceStmt = conn.c.prepareStatement(balanceQuery);
                    balanceStmt.setString(1, pinnumber);
                    ResultSet rs = balanceStmt.executeQuery();

                    int balance = 0;
                    while (rs.next()) {
                        String type = rs.getString("type");
                        int amount = Integer.parseInt(rs.getString("amount"));

                        if (type.equalsIgnoreCase("Deposit")) {
                            balance += amount;
                        } else if (type.equalsIgnoreCase("Withdrawal")) {
                            balance -= amount;
                        }
                    }

                    // Check if the balance is sufficient
                    if (withdrawalAmount > balance) {
                        JOptionPane.showMessageDialog(null, "Insufficient Balance");
                        return;
                    }

                    // Format the date and insert the withdrawal into the database
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String formattedDate = sdf.format(date);

                    String query = "INSERT INTO bank (pin, date, type, amount) VALUES (?, ?, ?, ?)";
                    PreparedStatement pstmt = conn.c.prepareStatement(query);
                    pstmt.setString(1, pinnumber);
                    pstmt.setString(2, formattedDate);
                    pstmt.setString(3, "Withdrawal");
                    pstmt.setInt(4, withdrawalAmount);
                    pstmt.executeUpdate();

                    JOptionPane.showMessageDialog(null, "TK " + number + " withdrawn successfully");
                    setVisible(false);
                    new Transactions(pinnumber).setVisible(true);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Please enter a numeric amount");
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "Error occurred: " + e.getMessage());
                }
            }
        } else if (ae.getSource() == back) {
            setVisible(false);
            new Transactions(pinnumber).setVisible(true);
        }
    }

    public static void main(String args[]) {
        new Withdrawal("");
    }
}
