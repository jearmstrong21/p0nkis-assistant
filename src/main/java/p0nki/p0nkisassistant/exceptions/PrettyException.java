package p0nki.p0nkisassistant.exceptions;

public class PrettyException extends RuntimeException {

    private String title, description;

    public PrettyException(String title, String description) {
//        super(null, new LiteralMessage("if you see this die"));
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

}
