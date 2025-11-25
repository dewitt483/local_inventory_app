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
        String sql = "INSERT INTO inventory_items (type, location, serial_number, description, redistributable) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = databaseClient.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, item.getType());
            statement.setString(2, item.getLocation());
            statement.setString(3, item.getSerialNumber());
            statement.setString(4, item.getDescription());
            statement.setInt(5, item.isRedistributable() ? 1 : 0);
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
        String sql = "SELECT id, type, location, serial_number, description, redistributable FROM inventory_items WHERE id = ?";
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
        return findFiltered(null, null);
    }

    public List<InventoryItem> findFiltered(String serialQuery, Boolean redistributable) {
        StringBuilder sql = new StringBuilder("SELECT id, type, location, serial_number, description, redistributable FROM inventory_items");
        List<Object> params = new ArrayList<>();
        List<String> clauses = new ArrayList<>();

        if (serialQuery != null && !serialQuery.isBlank()) {
            clauses.add("serial_number LIKE ?");
            params.add("%" + serialQuery.trim() + "%");
        }

        if (redistributable != null) {
            clauses.add("redistributable = ?");
            params.add(redistributable ? 1 : 0);
        }

        if (!clauses.isEmpty()) {
            sql.append(" WHERE ").append(String.join(" AND ", clauses));
        }

        sql.append(" ORDER BY id DESC");

        List<InventoryItem> items = new ArrayList<>();
        try (Connection connection = databaseClient.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                statement.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    items.add(mapRow(rs));
                }
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

        String sql = "UPDATE inventory_items SET type = ?, location = ?, serial_number = ?, description = ?, redistributable = ? WHERE id = ?";
        try (Connection connection = databaseClient.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, item.getType());
            statement.setString(2, item.getLocation());
            statement.setString(3, item.getSerialNumber());
            statement.setString(4, item.getDescription());
            statement.setInt(5, item.isRedistributable() ? 1 : 0);
            statement.setLong(6, item.getId());
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
        String type = rs.getString("type");
        String location = rs.getString("location");
        String serialNumber = rs.getString("serial_number");
        String description = rs.getString("description");
        boolean redistributable = rs.getInt("redistributable") == 1;
        return new InventoryItem(id, type, location, serialNumber, description, redistributable);
    }
}
