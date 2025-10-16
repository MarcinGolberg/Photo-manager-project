# Photo Manager Project

A desktop application for organizing and managing digital photos, developed using Java Swing. This project provides an intuitive graphical user interface to help users categorize, search, and view their photo library efficiently.



---

## Features

* **Photo Ingestion:** Add new photos to the library with associated metadata such as title, description, date, and tags.
* **Photo Collections:** Organize photos into user-defined collections for better management.
* **Image Preview:** Double-click any photo entry to display a preview and its corresponding metadata.
* **Robust Search Functionality:** Locate photos by title, description, date, or tags.
* **Advanced Tag Search:** Utilize logical operators for more precise search queries:
    * `,` for **AND** (e.g., `travel,summer`)
    * `|` for **OR** (e.g., `paris|london`)
* **Data Persistence:** Photo metadata and collection structures are automatically saved and loaded between application sessions.
* **Context Menu:** Right-click on photos or collections to access a context-sensitive menu for quick actions.

---

## Getting Started

Follow these instructions to compile and run the project on your local machine.

### Prerequisites

* Java Development Kit (JDK) 8 or a more recent version must be installed.

### Installation and Execution

1.  **Clone the repository** (or download the source files) to your local machine.
2.  **Create a `photos` directory** in the root of the project folder. The application uses this directory to store photo metadata files.
    ```bash
    mkdir photos
    ```
3.  **Compile the Java source files.** Open a terminal or command prompt, navigate to the `src` directory, and execute the following command:
    ```bash
    javac *.java
    ```
4.  **Run the application.** From within the `src` directory, run the `Main` class:
    ```bash
    java Main
    ```

---

## Usage Guide

### 1. Add a Photo
* Click the **Add Photo** button.
* Complete the form with the photo's title, description, tags, and date.
* Click **Select File** to choose an image from your file system.
* Click **OK** to add the photo to the library.
* To view a preview, **double-click** the photo's path in the tree view on the left.

### 2. Remove a Photo
* In the tree view, **left-click** and then **right-click** the photo you wish to delete.
* Select **Remove Photo** from the context menu.

### 3. Create or Add to a Collection
* **Left-click** and then **right-click** a photo.
* Select **Add to Collection**.
* Enter the name of a new or existing collection.

### 4. Delete a Collection
* **Left-click** and then **right-click** a collection name in the tree view.
* Select **Remove Collection**.
* Note: All photos from the deleted collection will be moved to the "Default" view.

### 5. Search for Photos
* Select a search criterion from the dropdown menu (Titles, Descriptions, Dates, Tags).
* Enter your query into the search bar.
* Click the **Search** button.
* To restore the full library view, clear the search bar and click **Search** again.

---

## Project Structure

The project's source code is organized into the following classes:

* `Main.java`: The primary entry point for the application. It initializes the main window and components.
* `UserInterface.java`: Manages the construction, layout, and styling of all GUI components.
* `EventHandler.java`: Contains the core application logic, handling all user interactions and events.
* `Photo.java`: A data model class representing a single photo and its metadata.
* `PhotoCollection.java`: A class for managing a collection of `Photo` objects.
* `ContextMenu.java`: Defines the right-click popup menu for actions on photos and collections.
* `collections.dat`: A binary file that is automatically generated to persist collection data.

---

## Author

* **Marcin Golberg**
