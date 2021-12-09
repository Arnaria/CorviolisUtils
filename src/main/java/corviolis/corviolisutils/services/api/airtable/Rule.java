package corviolis.corviolisutils.services.api.airtable;

import com.google.gson.annotations.SerializedName;

public class Rule {

    private String id;
    private String createdTime;
    private String created;

    @SerializedName("Title")
    private String title;
    @SerializedName("Description")
    private String description;

    public void setId(String id) {
        this.id = id;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public String getCreated() {
        return created;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
