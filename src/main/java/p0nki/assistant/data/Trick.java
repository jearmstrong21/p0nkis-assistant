package p0nki.assistant.data;

public class Trick {

    private String name;
    private String ownerID;
    private TrickType type;
    private String code;
    private long createdAt;
    private long editedAt;

    public Trick() {

    }

    public Trick(String name, String ownerID, TrickType type, String code, long createdAt, long editedAt) {
        this.name = name;
        this.ownerID = ownerID;
        this.type = type;
        this.code = code;
        this.createdAt = createdAt;
        this.editedAt = editedAt;
    }

    public String getName() {
        return name;
    }

    public String getOwnerID() {
        return ownerID;
    }

    public TrickType getType() {
        return type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getEditedAt() {
        return editedAt;
    }

    public void setEditedAt(long editedAt) {
        this.editedAt = editedAt;
    }
}
