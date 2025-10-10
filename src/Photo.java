import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class Photo implements Serializable {
    private String title;
    private Set<String> tags;
    private String description;
    private Date date;
    private String filePath;

    public Photo(String title, String description, Date date, String filePath) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.filePath = filePath;
        this.tags = new HashSet<>();
    }

    public void addTag(String tag) {
        tags.add(tag);
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Date getDate() {
        return date;
    }

    public String getFilePath() {
        return filePath;
    }

    public Set<String> getTags() {
        return tags;
    }

    @Override
    public String toString() {
        return filePath;
    }
}
