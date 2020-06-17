package p0nki.easycommandtestbot.data;

public class StarboardReaction {

    private String reaction;
    private boolean isEmoji;

    public StarboardReaction(String reaction, boolean isEmoji) {
        this.reaction = reaction;
        this.isEmoji = isEmoji;
    }

    public StarboardReaction() {

    }

    public String getReaction() {
        return reaction;
    }

    public boolean isEmoji() {
        return isEmoji;
    }

    public void print() {
        System.out.println(reaction);
        System.out.println(isEmoji);
    }
}
