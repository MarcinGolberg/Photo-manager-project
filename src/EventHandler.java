import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

//Handles all event-related actions for the application.
public class EventHandler {
    private UserInterface ui;
    private PhotoCollection currentCollection;
    private Map<String, PhotoCollection> collections = new HashMap<>();
    private static final String COLLECTIONS_FILE = "collections.dat";

    public EventHandler(UserInterface ui) {
        this.ui = ui;
        loadCollections(); //Load existing photo collections from file
        if (!collections.containsKey("Default")) {
            collections.put("Default", new PhotoCollection("Default"));
        }
        this.currentCollection = collections.get("Default");
        updateTree(); //Update the UI tree with current collections
    }

    //Adds event listeners for various UI components.
    public void addEventListeners() {
        addSearchButtonListener();
        addAddPhotoButtonListener();
        addPhotoTreeMouseListener();
    }

    //Adds a listener for the search button.
    private void addSearchButtonListener() {
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(this::performSearch); //Perform search on click
        styleButton(searchButton);
        ui.getButtonPanel().add(searchButton); //Add to UI
    }

    //Adds a listener for the add photo button.
    private void addAddPhotoButtonListener() {
        JButton addButton = new JButton("Add Photo");
        addButton.addActionListener(this::addPhoto); //Add photo on click
        styleButton(addButton);
        ui.getButtonPanel().add(addButton); //Add to UI
    }

    //Adds a mouse listener to the photo tree for handling context menus and double-click actions.
    private void addPhotoTreeMouseListener() {
        ui.getPhotoTree().addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleMousePressed(e);
            }
        });
    }

    //Handles mouse pressed events on the photo tree.
    private void handleMousePressed(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        int row = ui.getPhotoTree().getRowForLocation(x, y);
        if (row != -1) {
            ui.getPhotoTree().setSelectionRow(row);
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) ui.getPhotoTree().getLastSelectedPathComponent();
            if (node != null) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    showContextMenu(node, e);
                } else if (e.getClickCount() == 2 && !e.isConsumed()) {
                    handleDoubleClick(node);
                }
            }
        }
    }

    //Handles double-click events on a photo node.
    private void handleDoubleClick(DefaultMutableTreeNode node) {
        Object userObject = node.getUserObject();
        if (userObject instanceof Photo) {
            Photo photo = (Photo) userObject;
            updatePhotoPreview(photo);
            updatePhotoDetails(photo);
        }
    }

    //Displays the context menu for a node.
    private void showContextMenu(DefaultMutableTreeNode node, MouseEvent e) {
        Object userObject = node.getUserObject();
        JPopupMenu contextMenu = new JPopupMenu();

        if (userObject instanceof Photo) {
            JMenuItem removePhoto = new JMenuItem("Remove Photo");
            removePhoto.addActionListener(event -> deletePhoto(event));
            contextMenu.add(removePhoto);

            JMenuItem addToCollection = new JMenuItem("Add to Collection");
            addToCollection.addActionListener(event -> addToCollection(event));
            contextMenu.add(addToCollection);
        } else if (userObject instanceof String) {
            JMenuItem removeCollection = new JMenuItem("Remove Collection");
            removeCollection.addActionListener(event -> deleteCollection(event));
            contextMenu.add(removeCollection);
        }

        contextMenu.show(ui.getPhotoTree(), e.getX(), e.getY());
    }

    //Deletes a photo from the collection.
    public void deletePhoto(ActionEvent e) {
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) ui.getPhotoTree().getLastSelectedPathComponent();
        if (selectedNode != null && selectedNode.getUserObject() instanceof Photo) {
            Photo photo = (Photo) selectedNode.getUserObject();
            for (PhotoCollection collection : collections.values()) {
                collection.removePhoto(photo);
            }
            ui.getTreeModel().removeNodeFromParent(selectedNode);
            clearPhotoInfo();
            deletePhotoFile(photo);
            saveCollections();
            updateTree();
            ui.getPhotoTree().updateUI();
        }
    }

    //Deletes a photo collection.
    public void deleteCollection(ActionEvent e) {
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) ui.getPhotoTree().getLastSelectedPathComponent();
        if (selectedNode != null && selectedNode.getUserObject() instanceof String) {
            String collectionName = (String) selectedNode.getUserObject();
            if (!collectionName.equals("Default")) {
                PhotoCollection collectionToRemove = collections.get(collectionName);
                PhotoCollection defaultCollection = collections.get("Default");
                for (Photo photo : collectionToRemove.getPhotos()) {
                    defaultCollection.addPhoto(photo);
                }
                collections.remove(collectionName);
                ui.getTreeModel().removeNodeFromParent(selectedNode);
                saveCollections();
                updateTree();
                ui.getPhotoTree().updateUI();
            } else {
                JOptionPane.showMessageDialog(ui.getPhotoTree(), "Cannot delete the Default collection", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    //Adds a photo to a specified collection.
    public void addToCollection(ActionEvent e) {
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) ui.getPhotoTree().getLastSelectedPathComponent();
        if (selectedNode != null && selectedNode.getUserObject() instanceof Photo) {
            Photo selectedPhoto = (Photo) selectedNode.getUserObject();
            String collectionName = JOptionPane.showInputDialog(ui.getPhotoTree(), "Enter Collection Name:");
            if (collectionName != null && !collectionName.trim().isEmpty()) {
                PhotoCollection collection = collections.get(collectionName);
                if (collection == null) {
                    collection = new PhotoCollection(collectionName);
                    collections.put(collectionName, collection);
                }
                collection.addPhoto(selectedPhoto);
                collections.get("Default").removePhoto(selectedPhoto);
                updateTree();
                saveCollections();
                ui.getPhotoTree().updateUI();
                updatePhotoPreview(selectedPhoto);
            }
        }
    }

    //Performs a search based on the query entered in the search field.
    private void performSearch(ActionEvent e) {
        String query = ui.getSearchField().getText().trim().toLowerCase();
        clearPhotoInfo();
        if (query.isEmpty()) {
            updateTree();
            ui.getPhotoTree().setModel(ui.getTreeModel());
            ui.getPhotoTree().updateUI();
            expandAllNodes();
            return;
        }
        DefaultMutableTreeNode searchRoot = new DefaultMutableTreeNode("Search Results");
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) ui.getTreeModel().getRoot();
        treeNodeSearch(root, searchRoot, query, (String) ui.getSearchOptions().getSelectedItem());
        DefaultTreeModel searchModel = new DefaultTreeModel(searchRoot);
        ui.getPhotoTree().setModel(searchModel);
        expandAllNodes();
    }

    //Recursively searches through the photo tree nodes.
    private void treeNodeSearch(DefaultMutableTreeNode currentNode, DefaultMutableTreeNode searchRoot, String query, String criteria) {
        if (currentNode.getUserObject() instanceof Photo) {
            Photo photo = (Photo) currentNode.getUserObject();
            if (matchesSearchCriteria(photo, query, criteria)) {
                searchRoot.add(new DefaultMutableTreeNode(photo));
            }
        }
        for (int i = 0; i < currentNode.getChildCount(); i++) {
            treeNodeSearch((DefaultMutableTreeNode) currentNode.getChildAt(i), searchRoot, query, criteria);
        }
    }

    //Checks if a photo matches the search criteria.
    private boolean matchesSearchCriteria(Photo photo, String query, String criteria) {
        switch (criteria) {
            case "Titles":
                return photo.getTitle().toLowerCase().contains(query);
            case "Descriptions":
                return photo.getDescription().toLowerCase().contains(query);
            case "Dates":
                return new SimpleDateFormat("dd/MM/yyyy").format(photo.getDate()).equals(query);
            case "Tags":
                String[] orTags = query.split("\\|");
                for (String orTagGroup : orTags) {
                    String[] andTags = orTagGroup.split(",");
                    boolean allMatch = true;
                    for (String andTag : andTags) {
                        if (!photo.getTags().contains(andTag.trim().toLowerCase())) {
                            allMatch = false;
                            break;
                        }
                    }
                    if (allMatch) {
                        return true;
                    }
                }
                return false;
            default:
                return false;
        }
    }




    //Adds a new photo to the collection.
    private void addPhoto(ActionEvent e) {
        JTextField titleField = new JTextField(10);
        JTextField descField = new JTextField(10);
        JTextField tagsField = new JTextField(10);
        JTextField dateField = new JTextField(10);
        JTextField filePathField = new JTextField(10);
        JButton fileChooserButton = new JButton("Select File");
        JFileChooser fileChooser = new JFileChooser();

        //Limit file chooser to just photos and folders
        fileChooser.setFileFilter(new FileNameExtensionFilter("Photos only", "png", "jpg"));

        styleTextField(titleField);
        styleTextField(descField);
        styleTextField(tagsField);
        styleTextField(dateField);
        styleTextField(filePathField);
        styleButton(fileChooserButton);

        fileChooserButton.addActionListener(ev -> {
            int fileChoice = fileChooser.showOpenDialog(null);
            if (fileChoice == JFileChooser.APPROVE_OPTION) {
                filePathField.setText(fileChooser.getSelectedFile().getAbsolutePath());
            }
        });

        JPanel panel = new JPanel(new GridLayout(0, 1));
        JLabel titleLabel = new JLabel("Title");
        styleLabel(titleLabel);
        panel.add(titleLabel);
        panel.add(titleField);
        JLabel descLabel = new JLabel("Description");
        styleLabel(descLabel);
        panel.add(descLabel);
        panel.add(descField);
        JLabel tagsLabel = new JLabel("Tags (separated by , )");
        styleLabel(tagsLabel);
        panel.add(tagsLabel);
        panel.add(tagsField);
        JLabel dateLabel = new JLabel("Date (DD/MM/YYYY)");
        styleLabel(dateLabel);
        panel.add(dateLabel);
        panel.add(dateField);
        JLabel filePathLabel = new JLabel("File Path");
        styleLabel(filePathLabel);
        panel.add(filePathLabel);
        panel.add(filePathField);
        panel.add(fileChooserButton);

        panel.setBackground(new Color(149, 210, 179));


        int result = JOptionPane.showConfirmDialog(null, panel, "Add a new Photo",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION && !filePathField.getText().isEmpty()) {
            try {
                if (!dateField.getText().matches("\\d{2}/\\d{2}/\\d{4}")) {
                    throw new IllegalArgumentException("Invalid date format. Please use 'dd/MM/yyyy'.");
                }
                Date photoDate = new SimpleDateFormat("dd/MM/yyyy").parse(dateField.getText());
                Photo newPhoto = new Photo(titleField.getText(), descField.getText(), photoDate, filePathField.getText());
                for (String tag : tagsField.getText().split(",")) {
                    newPhoto.addTag(tag.trim());
                }
                currentCollection.addPhoto(newPhoto);
                savePhotoToFile(newPhoto);
                saveCollections();
                updateTree();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Invalid date format. Please use 'dd/MM/yyyy'.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    //Updates the photo preview.
    private void updatePhotoPreview(Photo photo) {
        try {
            ImageIcon imageIcon = new ImageIcon(photo.getFilePath());
            Image image = imageIcon.getImage().getScaledInstance(620, -1, Image.SCALE_SMOOTH);
            ui.getPhotoLabel().setIcon(new ImageIcon(image));
            ui.getPhotoLabel().revalidate();
            ui.getPhotoLabel().repaint();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading image: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    //Updates the photo details.
    private void updatePhotoDetails(Photo photo) {
        ui.getTitleLabel().setText("Title: " + photo.getTitle());
        ui.getDescriptionLabel().setText("Description: " + photo.getDescription());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        ui.getDateLabel().setText("Date: " + dateFormat.format(photo.getDate()));
        ui.getTagLabel().setText("Tags: " + String.join(", ", photo.getTags()));
    }

    //Saves the photo object to a file.
    private void savePhotoToFile(Photo photo) {
        String filename = "photos/" + photo.getTitle().replace(" ", "_").replace(":", "_").replace("/", "_") + ".dat";

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(photo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Deletes the photo file.
    private void deletePhotoFile(Photo photo) {
        String filename = "photos/" + photo.getTitle().replace(" ", "_").replace(":", "_").replace("/", "_") + ".dat";

        File file = new File(filename);
        if (file.exists()) {
            file.delete();
        }
    }

    //Clears the photo information displayed in the UI.
    private void clearPhotoInfo() {
        ui.getPhotoLabel().setIcon(null);
        ui.getTitleLabel().setText("Title: ");
        ui.getDescriptionLabel().setText("Description: ");
        ui.getDateLabel().setText("Date: ");
        ui.getTagLabel().setText("Tags: ");
    }


    //Loads photos from files.
    public void loadPhotos() {
        new Thread(() -> {
            File folder = new File("photos");
            File[] files = folder.listFiles((dir, name) -> name.endsWith(".dat"));
            if (files != null) {
                Arrays.sort(files);
                for (File file : files) {
                    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                        Photo photo = (Photo) ois.readObject();
                        SwingUtilities.invokeLater(() -> {
                            if (!containsPhoto(photo)) {
                                addPhotoToCollection(photo);
                            }
                        });
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    //Checks if a photo is already in the collection.
    private boolean containsPhoto(Photo photo) {
        for (PhotoCollection collection : collections.values()) {
            for (Photo existingPhoto : collection.getPhotos()) {
                if (existingPhoto.getFilePath().equals(photo.getFilePath())) {
                    return true;
                }
            }
        }
        return false;
    }

    //Adds a photo to the collection and updates the UI.
    private void addPhotoToCollection(Photo photo) {
        PhotoCollection collection = collections.get("Default");
        if (collection == null) {
            collection = new PhotoCollection("Default");
            collections.put("Default", collection);
        }
        collection.addPhoto(photo);
        updatePhotoNodes(photo);
    }

    //Updates the photo nodes in the tree.
    private void updatePhotoNodes(Photo photo) {
        DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) ui.getTreeModel().getRoot();
        DefaultMutableTreeNode photoNode = new DefaultMutableTreeNode(photo);
        ui.getTreeModel().insertNodeInto(photoNode, rootNode, rootNode.getChildCount());
        ui.getPhotoTree().updateUI();
    }

    //Styles a button with a specific color scheme.
    private void styleButton(JButton button) {
        button.setBackground(new Color(149, 210, 179));
    }

    //Styles a button with a specific color scheme.
    private void styleTextField(JTextField textField) {
        textField.setBackground(new Color(216, 239, 211));
    }

    private void styleLabel(JLabel label) {
        label.setBackground(new Color(149, 210, 179));
    }

    //Saves the photo collections to a file.
    private void saveCollections() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(COLLECTIONS_FILE))) {
            oos.writeObject(collections);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Loads the photo collections from a file.
    private void loadCollections() {
        File file = new File(COLLECTIONS_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(COLLECTIONS_FILE))) {
                Object obj = ois.readObject();
                if (obj instanceof Map) {
                    collections = (Map<String, PhotoCollection>) obj;
                } else {
                    collections = new HashMap<>();
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                collections = new HashMap<>();
            }
        } else {
            System.out.println("Collections file does not exist.");
        }
    }


    //Updates the photo tree to reflect changes in collections.
    private void updateTree() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Default");

        //Add photos to the root node
        collections.getOrDefault("Default", new PhotoCollection("Default"))
                .getPhotos()
                .stream()
                .map(DefaultMutableTreeNode::new)
                .forEach(root::add);

        //Add other collections as children of the root node
        collections.entrySet()
                .stream()
                .filter(entry -> !entry.getKey().equals("Default"))
                .forEach(entry -> {
                    DefaultMutableTreeNode collectionNode = new DefaultMutableTreeNode(entry.getKey());
                    entry.getValue().getPhotos()
                            .stream()
                            .map(DefaultMutableTreeNode::new)
                            .forEach(collectionNode::add);
                    root.add(collectionNode);
                });

        ui.getTreeModel().setRoot(root);
        ui.getTreeModel().reload();  //Apply changes to the tree
        expandAllNodes(); //Expand all nodes to show changes
    }

    //Expands all nodes in the photo tree.
    private void expandAllNodes() {
        for (int i = 0; i < ui.getPhotoTree().getRowCount(); i++) {
            ui.getPhotoTree().expandRow(i);
        }
    }
}
