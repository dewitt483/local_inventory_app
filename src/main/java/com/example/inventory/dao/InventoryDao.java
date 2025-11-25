package com.example.inventory.dao;

import com.example.inventory.db.DatabaseClient;
import com.example.inventory.model.InventoryItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InventoryDao {
    private final DatabaseClient databaseClient;

    public InventoryDao(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    public InventoryItem create(InventoryItem item) {
        String sql = "INSERT INTO inventory_items (name, quantity, description) VALUES (?, ?, ?)";
        try (Connection connection = databaseClient.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, item.getName());
            statement.setInt(2, item.getQuantity());
            statement.setString(3, item.getDescription());
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    item.setId(keys.getLong(1));
                }
            }
            return item;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to create inventory item", e);
        }
    }

    public Optional<InventoryItem> findById(long id) {
        String sql = "SELECT id, name, quantity, description FROM inventory_items WHERE id = ?";
        try (Connection connection = databaseClient.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to load inventory item", e);
        }
    }

    public List<InventoryItem> findAll() {
        String sql = "SELECT id, name, quantity, description FROM inventory_items ORDER BY name";
        List<InventoryItem> items = new ArrayList<>();
        try (Connection connection = databaseClient.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                items.add(mapRow(rs));
            }
            return items;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to list inventory items", e);
        }
    }

    public boolean update(InventoryItem item) {
        if (item.getId() == null) {
            throw new IllegalArgumentException("Cannot update item without an id");
        }

        String sql = "UPDATE inventory_items SET name = ?, quantity = ?, description = ? WHERE id = ?";
        try (Connection connection = databaseClient.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, item.getName());
            statement.setInt(2, item.getQuantity());
            statement.setString(3, item.getDescription());
            statement.setLong(4, item.getId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to update inventory item", e);
        }
    }

    public boolean delete(long id) {
        String sql = "DELETE FROM inventory_items WHERE id = ?";
        try (Connection connection = databaseClient.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to delete inventory item", e);
        }
    }

    private InventoryItem mapRow(ResultSet rs) throws SQLException {
        long id = rs.getLong("id");
        String name = rs.getString("name");
        int quantity = rs.getInt("quantity");
        String description = rs.getString("description");
        return new InventoryItem(id, name, quantity, description);
    }
}
