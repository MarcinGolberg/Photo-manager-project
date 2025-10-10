import javax.swing.*;

// Context menu for action selection?
public class ContextMenu extends JPopupMenu {
    JMenuItem removePhoto;
    JMenuItem addToCollection;
    JMenuItem removeCollection;

    public ContextMenu(EventHandler eventHandler) {
        removePhoto = new JMenuItem("Remove Photo");
        removePhoto.addActionListener(eventHandler::deletePhoto);
        addToCollection = new JMenuItem("Add to Collection");
        addToCollection.addActionListener(eventHandler::addToCollection);
        removeCollection = new JMenuItem("Remove Collection");
        removeCollection.addActionListener(eventHandler::deleteCollection);
    }
}
