package com.example.inventory;

import com.example.inventory.dao.InventoryDao;
import com.example.inventory.db.DatabaseClient;
import com.example.inventory.model.InventoryItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.util.List;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(App::createAndShowGui);
    }

    private static void createAndShowGui() {
        DatabaseClient databaseClient = new DatabaseClient();
        databaseClient.initializeSchema();
        InventoryDao inventoryDao = new InventoryDao(databaseClient);

        ensureSampleData(inventoryDao);
        List<InventoryItem> items = inventoryDao.findAll();

        JFrame frame = new JFrame("Inventory App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        String message = "Inventory app connected to database. Items in stock: " + items.size();
        JLabel label = new JLabel(message, JLabel.CENTER);
        frame.add(label, BorderLayout.CENTER);

        frame.setSize(480, 320);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void ensureSampleData(InventoryDao inventoryDao) {
        if (!inventoryDao.findAll().isEmpty()) {
            return;
        }

        inventoryDao.create(new InventoryItem("Sample Widget", 10, "Starter inventory item"));
        inventoryDao.create(new InventoryItem("Refill Kit", 5, "Example secondary item"));
    }
}
