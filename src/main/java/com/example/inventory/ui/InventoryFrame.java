package com.example.inventory.ui;

import com.example.inventory.dao.InventoryDao;
import com.example.inventory.model.InventoryItem;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Optional;

public class InventoryFrame extends JFrame {
    private final InventoryDao inventoryDao;
    private final InventoryTableModel tableModel;
    private final JTable table;

    public InventoryFrame(InventoryDao inventoryDao) {
        this.inventoryDao = inventoryDao;
        this.tableModel = new InventoryTableModel();
        this.table = new JTable(tableModel);

        setTitle("Inventory Manager");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                // Ensure JVM exits when frame is closed
                System.exit(0);
            }
        });

        refreshItems();
        setSize(720, 480);
        setLocationRelativeTo(null);
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton addButton = new JButton("Add");
        addButton.addActionListener(e -> handleAdd());

        JButton editButton = new JButton("Edit");
        editButton.addActionListener(e -> handleEdit());

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> handleDelete());

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshItems());

        panel.add(addButton);
        panel.add(editButton);
        panel.add(deleteButton);
        panel.add(refreshButton);

        return panel;
    }

    private void refreshItems() {
        try {
            tableModel.setItems(inventoryDao.findAll());
        } catch (Exception e) {
            showError("Unable to load items", e);
        }
    }

    private void handleAdd() {
        Optional<InventoryItem> result = showItemDialog("Add Inventory Item", null);
        result.ifPresent(item -> {
            try {
                inventoryDao.create(item);
                refreshItems();
            } catch (Exception e) {
                showError("Unable to create item", e);
            }
        });
    }

    private void handleEdit() {
        InventoryItem selected = getSelectedItem();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Select an item to edit.", "No selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Optional<InventoryItem> result = showItemDialog("Edit Inventory Item", selected);
        result.ifPresent(updated -> {
            updated.setId(selected.getId());
            try {
                if (!inventoryDao.update(updated)) {
                    JOptionPane.showMessageDialog(this, "Item no longer exists.", "Update failed", JOptionPane.ERROR_MESSAGE);
                }
                refreshItems();
            } catch (Exception e) {
                showError("Unable to update item", e);
            }
        });
    }

    private void handleDelete() {
        InventoryItem selected = getSelectedItem();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Select an item to delete.", "No selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int result = JOptionPane.showConfirmDialog(this,
                "Delete '" + selected.getName() + "'?", "Confirm delete", JOptionPane.YES_NO_OPTION);
        if (result != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            if (!inventoryDao.delete(selected.getId())) {
                JOptionPane.showMessageDialog(this, "Item no longer exists.", "Delete failed", JOptionPane.ERROR_MESSAGE);
            }
            refreshItems();
        } catch (Exception e) {
            showError("Unable to delete item", e);
        }
    }

    private Optional<InventoryItem> showItemDialog(String title, InventoryItem existing) {
        JTextField nameField = new JTextField(existing != null ? existing.getName() : "", 20);
        JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(existing != null ? existing.getQuantity() : 0, 0, Integer.MAX_VALUE, 1));
        JTextArea descriptionArea = new JTextArea(existing != null ? existing.getDescription() : "", 4, 20);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.LINE_END;

        gbc.gridx = 0;
        gbc.gridy = 0;
        form.add(new JLabel("Name:"), gbc);

        gbc.gridy = 1;
        form.add(new JLabel("Quantity:"), gbc);

        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.FIRST_LINE_END;
        form.add(new JLabel("Description:"), gbc);

        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        form.add(nameField, gbc);

        gbc.gridy = 1;
        form.add(quantitySpinner, gbc);

        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.BOTH;
        form.add(new JScrollPane(descriptionArea), gbc);

        while (true) {
            int result = JOptionPane.showConfirmDialog(this, form, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result != JOptionPane.OK_OPTION) {
                return Optional.empty();
            }

            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name is required.", "Validation error", JOptionPane.ERROR_MESSAGE);
                continue;
            }

            int quantity = ((Number) quantitySpinner.getValue()).intValue();
            if (quantity < 0) {
                JOptionPane.showMessageDialog(this, "Quantity must be zero or greater.", "Validation error", JOptionPane.ERROR_MESSAGE);
                continue;
            }

            String description = descriptionArea.getText().trim();
            InventoryItem item = new InventoryItem(existing != null ? existing.getId() : null, name, quantity, description);
            return Optional.of(item);
        }
    }

    private InventoryItem getSelectedItem() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            return null;
        }
        return tableModel.getItemAt(selectedRow);
    }

    private void showError(String message, Exception e) {
        JOptionPane.showMessageDialog(this, message + "\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
