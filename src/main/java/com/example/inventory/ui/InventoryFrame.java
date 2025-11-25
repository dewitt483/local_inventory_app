package com.example.inventory.ui;

import com.example.inventory.dao.InventoryDao;
import com.example.inventory.model.InventoryItem;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
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
    private final JTextField searchField;
    private final JComboBox<String> redistributableFilter;

    public InventoryFrame(InventoryDao inventoryDao) {
        this.inventoryDao = inventoryDao;
        this.tableModel = new InventoryTableModel();
        this.table = new JTable(tableModel);
        this.searchField = new JTextField(20);
        this.redistributableFilter = new JComboBox<>(new String[]{"Both", "Redistributable", "Not Redistributable"});

        setTitle("Inventory Manager");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(createFilterPanel(), BorderLayout.NORTH);
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

    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> refreshItems());

        panel.add(new JLabel("Serial contains:"));
        panel.add(searchField);
        panel.add(new JLabel("Redistributable:"));
        panel.add(redistributableFilter);
        panel.add(searchButton);

        return panel;
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
            Boolean redistributable = switch (redistributableFilter.getSelectedIndex()) {
                case 1 -> true;
                case 2 -> false;
                default -> null;
            };
            tableModel.setItems(inventoryDao.findFiltered(searchField.getText(), redistributable));
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
                "Delete '" + selected.getType() + "'?", "Confirm delete", JOptionPane.YES_NO_OPTION);
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
        JTextField typeField = new JTextField(existing != null ? existing.getType() : "", 20);
        JTextField locationField = new JTextField(existing != null ? existing.getLocation() : "", 20);
        JTextField serialField = new JTextField(existing != null ? existing.getSerialNumber() : "", 20);
        JTextArea descriptionArea = new JTextArea(existing != null ? existing.getDescription() : "", 4, 20);
        JCheckBox redistributableCheckbox = new JCheckBox("Redistributable", existing != null && existing.isRedistributable());

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.LINE_END;

        gbc.gridx = 0;
        gbc.gridy = 0;
        form.add(new JLabel("Type:"), gbc);

        gbc.gridy = 1;
        form.add(new JLabel("Location:"), gbc);

        gbc.gridy = 2;
        form.add(new JLabel("Serial Number:"), gbc);

        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.FIRST_LINE_END;
        form.add(new JLabel("Description:"), gbc);

        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.LINE_END;
        form.add(new JLabel("Redistributable:"), gbc);

        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        form.add(typeField, gbc);

        gbc.gridy = 1;
        form.add(locationField, gbc);

        gbc.gridy = 2;
        form.add(serialField, gbc);

        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.BOTH;
        form.add(new JScrollPane(descriptionArea), gbc);

        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE;
        form.add(redistributableCheckbox, gbc);

        while (true) {
            int result = JOptionPane.showConfirmDialog(this, form, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result != JOptionPane.OK_OPTION) {
                return Optional.empty();
            }

            String deviceType = typeField.getText().trim();
            if (deviceType.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Type is required.", "Validation error", JOptionPane.ERROR_MESSAGE);
                continue;
            }

            String location = locationField.getText().trim();
            if (location.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Location is required.", "Validation error", JOptionPane.ERROR_MESSAGE);
                continue;
            }

            String serialNumber = serialField.getText().trim();
            if (serialNumber.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Serial number is required.", "Validation error", JOptionPane.ERROR_MESSAGE);
                continue;
            }

            String description = descriptionArea.getText().trim();
            boolean redistributable = redistributableCheckbox.isSelected();
            InventoryItem item = new InventoryItem(existing != null ? existing.getId() : null, deviceType, location, serialNumber, description, redistributable);
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
