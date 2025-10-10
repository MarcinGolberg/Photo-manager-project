import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

//represents a collection of photos.
public class PhotoCollection implements Serializable {
    private String name;
    private List<Photo> photos;

    public PhotoCollection(String name) {
        this.name = name;
        this.photos = new ArrayList<>();
    }

    public void addPhoto(Photo photo) {
        photos.add(photo);
    }

    public void removePhoto(Photo photo) {
        photos.remove(photo);
    }

    public List<Photo> getPhotos() {
        return photos;
    }
}
