import java.util.ArrayList;

public class Email {

    private String to;
    private String from;
    private ArrayList<String> text;

    public Email (String to, String from, ArrayList<String> text){
        this.to = to;
        this.from = from;
        this.text = text;
    }

    public String getTo() {
        return to;
    }

    public String getFrom() {
        return from;
    }

    public ArrayList<String> getText() {
        return text;
    }

    public void setText(String text, int index) {
        this.text.set(index, text);
    }

    @Override
    public String toString() {
        String textString = "";
        for (String s:text) {
            textString += (s + " ");
        }
        return "TO:\t" + to +
                "\nFROM:\t" + from +
                "\nTEXT:\t" + textString;
    }
}
