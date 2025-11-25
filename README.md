# Local Inventory App

A minimal Swing-based desktop client for tracking inventory items backed by a local SQLite database.

## Prerequisites

- Java 17 or later
- Maven 3.8+ for build/run tasks
- SQLite available on your system (no server required; the application writes a local `inventory.db` file)

## Configuration

Database configuration is read from `src/main/resources/application.properties`:

- `db.url` (default: `jdbc:sqlite:inventory.db`): JDBC connection string for the SQLite database file. Change the filename or absolute path to relocate the data file.
- `db.user` / `db.password`: Optional credentials for alternate JDBC URLs. These are ignored for the default SQLite configuration.

## Setup and Running

1. Clone the repository and move into the project directory.
2. Build the project:
   ```bash
   mvn clean package
   ```
3. Run the GUI:
   ```bash
   mvn exec:java
   ```
   The application launches a Swing window titled **Inventory Manager** with a table of items and Add/Edit/Delete/Refresh controls. Closing the window terminates the JVM.

### Database initialization and seed data

- On startup the app creates the `inventory_items` table if it does not exist and stores data in the SQLite file referenced by `db.url`.
- If the table is empty, two sample rows are inserted automatically:
  - `Sample Widget` (quantity 10, description "Starter inventory item")
  - `Refill Kit` (quantity 5, description "Example secondary item")
- No external migration tool is required; schema creation and seed insertion run every launch before the UI appears.

### Development tips

- Refresh the table with the **Refresh** button if you modify data outside the UI.
- To reset the database during local development, delete the `inventory.db` file (or point `db.url` at a new filename) and restart the app to recreate the schema and seed data.

## Screenshot

Example UI layout showing the inventory table and controls:

![Inventory Manager screenshot](docs/images/inventory-ui.svg)
