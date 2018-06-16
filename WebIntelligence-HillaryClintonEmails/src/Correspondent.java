import java.util.ArrayList;

public class Correspondent {
    // Strings which hold the 2 correspondents
    private String correspondent;
    // ArrayList of Strings which stores all the emails sent by the correspondent
    private ArrayList<String> text;
    // ArrayList of Strings which stores the terms with the Highest nPercent of weight
    private ArrayList<Keyword> highestNPercentTerms;

    public Correspondent (String correspondent, ArrayList<String> text){
        this.correspondent = correspondent;
        this.text = text;
        this.highestNPercentTerms = new ArrayList<Keyword>();
    }

    public String getCorrespondent() {
        return correspondent;
    }

    public ArrayList<String> getText() {
        return text;
    }

    // Method which merges the object's ArrayList and the ArrayList passed as a parameter
    public void combineText(ArrayList<String> text) {
        this.text.addAll(text);
    }

    public ArrayList<Keyword> getHighestNPercentTerms() {
        return highestNPercentTerms;
    }

    // Method which adds keywords with highest nPercent to the highestNPercentTerms ArrayList
    public void addHighestTerms(Keyword keyword){
        highestNPercentTerms.add(keyword);
    }

}
