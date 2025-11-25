package com.example.inventory.model;

import java.util.Objects;

public class InventoryItem {
    private Long id;
    private String type;
    private String location;
    private String serialNumber;
    private String description;
    private boolean redistributable;

    public InventoryItem(Long id, String type, String location, String serialNumber, String description, boolean redistributable) {
        this.id = id;
        this.type = type;
        this.location = location;
        this.serialNumber = serialNumber;
        this.description = description;
        this.redistributable = redistributable;
    }

    public InventoryItem(String type, String location, String serialNumber, String description, boolean redistributable) {
        this(null, type, location, serialNumber, description, redistributable);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isRedistributable() {
        return redistributable;
    }

    public void setRedistributable(boolean redistributable) {
        this.redistributable = redistributable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InventoryItem that = (InventoryItem) o;
        return redistributable == that.redistributable && Objects.equals(id, that.id) && Objects.equals(type, that.type) && Objects.equals(location, that.location) && Objects.equals(serialNumber, that.serialNumber) && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, location, serialNumber, description, redistributable);
    }

    @Override
    public String toString() {
        return "InventoryItem{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", location='" + location + '\'' +
                ", serialNumber='" + serialNumber + '\'' +
                ", description='" + description + '\'' +
                ", redistributable=" + redistributable +
                '}';
    }
}
