import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;

//Manages the UI components for the Photo Manager application.
public class UserInterface {
    private DefaultTreeModel treeModel;
    private JTree photoTree;
    private JTextField searchField;
    private JComboBox<String> searchOptions;
    private JLabel searchLabel;
    private JLabel photoLabel;
    private JLabel titleLabel;
    private JLabel descriptionLabel;
    private JLabel dateLabel;
    private ContextMenu contextMenu;
    private JPanel topPanel;
    private JSplitPane splitPane;
    private JPanel buttonPanel;
    private JLabel tagLabel;

    public UserInterface() {
        initializeComponents();
        configureUI();
    }

    //Initializes UI components.
    private void initializeComponents() {
        treeModel = new DefaultTreeModel(new DefaultMutableTreeNode("All Photos"));
        photoTree = new JTree(treeModel);
        searchField = new JTextField();
        searchOptions = new JComboBox<>(new String[]{"Titles", "Descriptions", "Dates", "Tags"});
        searchLabel = new JLabel("Search for: ");
        photoLabel = new JLabel();
        titleLabel = new JLabel("Title: ");
        descriptionLabel = new JLabel("Description: ");
        dateLabel = new JLabel("Date: ");
        buttonPanel = new JPanel(new FlowLayout());
        tagLabel = new JLabel("Tags: ");
    }

    //Configures the UI layout and appearance.
    private void configureUI() {
        setComponentColors();
        layoutTopPanel();
        layoutMainComponents();
    }

    //Sets colors for UI components.
    private void setComponentColors() {
        photoTree.setBackground(new Color(149, 210, 179));
        searchOptions.setBackground(new Color(85, 173, 155));
        searchField.setBackground(new Color(149, 210, 179));

    }

    //Lays out the components in the top panel.
    private void layoutTopPanel() {
        topPanel = new JPanel(new BorderLayout());
        JPanel subTopPanel = new JPanel(new BorderLayout());

        subTopPanel.add(searchLabel, BorderLayout.WEST);
        subTopPanel.add(searchOptions, BorderLayout.CENTER);
        subTopPanel.setBackground(new Color(85, 173, 155));

        topPanel.add(subTopPanel, BorderLayout.WEST);
        topPanel.add(searchField, BorderLayout.CENTER);

        buttonPanel.setBackground(new Color(85, 173, 155));
        topPanel.setBackground(new Color(85, 173, 155));
        topPanel.add(buttonPanel, BorderLayout.EAST);
    }

    //Lays out the main components of the UI.
    private void layoutMainComponents() {

        JPanel infoPanel = new JPanel(new GridLayout(0, 1));
        infoPanel.add(titleLabel);
        infoPanel.add(descriptionLabel);
        infoPanel.add(dateLabel);
        infoPanel.add(tagLabel);
        infoPanel.setBackground(new Color(216, 239, 211));

        JPanel photoAndInfoPanel = new JPanel(new BorderLayout());
        photoAndInfoPanel.add(photoLabel, BorderLayout.CENTER);
        photoAndInfoPanel.add(infoPanel, BorderLayout.SOUTH);
        photoAndInfoPanel.setBackground(new Color(216, 239, 211));

        JScrollPane treeScroll = new JScrollPane(photoTree);
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeScroll, photoAndInfoPanel);
        splitPane.setDividerLocation(400);
        splitPane.setBorder(null);
    }

    //Initializes UI components with the context menu.
    public void initializeUIComponents(ContextMenu ContextMenu) {
        setContextMenu(ContextMenu);
    }

    //Getters
    public JPanel getTopPanel() {
        return topPanel;
    }

    public JSplitPane getSplitPane() {
        return splitPane;
    }

    public JTree getPhotoTree() {
        return photoTree;
    }

    public DefaultTreeModel getTreeModel() {
        return treeModel;
    }

    public JTextField getSearchField() {
        return searchField;
    }

    public JComboBox<String> getSearchOptions() {
        return searchOptions;
    }

    public JLabel getPhotoLabel() {
        return photoLabel;
    }

    public JLabel getTitleLabel() {
        return titleLabel;
    }

    public JLabel getDescriptionLabel() {
        return descriptionLabel;
    }

    public JLabel getDateLabel() {
        return dateLabel;
    }

    public JLabel getTagLabel() {
        return tagLabel;
    }

    public void setContextMenu(ContextMenu ContextMenu) {
        this.contextMenu = ContextMenu;
        photoTree.setComponentPopupMenu(ContextMenu);
    }


    public JPanel getButtonPanel() {
        return buttonPanel;
    }
}
