package com.example.inventory;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(App::createAndShowGui);
    }

    private static void createAndShowGui() {
        JFrame frame = new JFrame("Inventory App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JLabel label = new JLabel("Welcome to the inventory desktop app starter", JLabel.CENTER);
        frame.add(label, BorderLayout.CENTER);

        frame.setSize(480, 320);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
