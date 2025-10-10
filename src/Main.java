/* USER GUIDE
1. ADD PHOTOS
    a.  press Add photo button on the left
    b.  enter information about the photo
   (c.) double-click on the photo path on the left side of UI (JTree) in order to see preview

2. REMOVE PHOTO
    a.  left-click, then right-click on the photo you want to remove
    b.  press Remove photo

3. ADD TO COLLECTION
    a.  left-click, then right-click on the photo you want to add to collection
    b.  enter collection name

4. DELETE COLLECTION
    a.  left-click, then right-click on the collection you want to remove
   (b.) all photos got removed from collection and moved to Default one

5. SEARCH
    a.  select from combo menu what to search
    b.  type in content of search bar right next to combo menu
   (c.) tags support for '|' (OR) and ',' (AND) operator
    d.  press search on left side of UI
   (e.) in order to return to Default/Original tree, clear search bar and press Search button

~Marcin Golberg
*/

import javax.swing.*;
import java.awt.*;

//Main application class for the Photo Manager.
//Sets up the main frame and initializes UI components.
public class Main extends JFrame {
    private UserInterface userInterface;
    private EventHandler eventHandler;
    private ContextMenu contextMenu;

    public Main() {
        setTitle("Photo Manager");
        setSize(1050, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        //Initialize UI, Event Handler, and Context Menu
        userInterface = new UserInterface();
        eventHandler = new EventHandler(userInterface);
        contextMenu = new ContextMenu(eventHandler);

        //Initialize UI components and set context menu
        userInterface.initializeUIComponents(contextMenu);

        //Add UI components to the frame
        add(userInterface.getTopPanel(), BorderLayout.NORTH);
        add(userInterface.getSplitPane(), BorderLayout.CENTER);

        //Add event listeners and load photos
        eventHandler.addEventListeners();
        eventHandler.loadPhotos();

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main());
    }
}
