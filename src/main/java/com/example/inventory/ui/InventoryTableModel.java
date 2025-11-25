package com.example.inventory.ui;

import com.example.inventory.model.InventoryItem;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class InventoryTableModel extends AbstractTableModel {
    private final List<InventoryItem> items = new ArrayList<>();
    private final String[] columns = {"ID", "Name", "Quantity", "Description"};

    public void setItems(List<InventoryItem> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        fireTableDataChanged();
    }

    public InventoryItem getItemAt(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= items.size()) {
            return null;
        }
        return items.get(rowIndex);
    }

    @Override
    public int getRowCount() {
        return items.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        InventoryItem item = items.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> item.getId();
            case 1 -> item.getName();
            case 2 -> item.getQuantity();
            case 3 -> item.getDescription();
            default -> "";
        };
    }
}
