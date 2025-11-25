package com.example.inventory;

import com.example.inventory.dao.InventoryDao;
import com.example.inventory.db.DatabaseClient;
import com.example.inventory.model.InventoryItem;
import com.example.inventory.ui.InventoryFrame;
import javax.swing.SwingUtilities;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(App::createAndShowGui);
    }

    private static void createAndShowGui() {
        DatabaseClient databaseClient = new DatabaseClient();
        databaseClient.initializeSchema();
        InventoryDao inventoryDao = new InventoryDao(databaseClient);

        ensureSampleData(inventoryDao);
        InventoryFrame frame = new InventoryFrame(inventoryDao);
        frame.setVisible(true);
    }

    private static void ensureSampleData(InventoryDao inventoryDao) {
        if (!inventoryDao.findAll().isEmpty()) {
            return;
        }

        inventoryDao.create(new InventoryItem("Laptop", "HQ Storage", "ABC12345", "Starter inventory item", true));
        inventoryDao.create(new InventoryItem("Router", "IT Closet", "RTR-9988", "Example secondary item", false));
    }
}
