import java.util.ArrayList;

public class Document {
    // Strings which hold the 2 correspondents
    private String personA, personB;
    // ArrayList of Strings which stores all the text in the correspondences
    private ArrayList<String> text;
    // ArrayList of Strings which stores the terms with the Highest nPercent of weight
    private ArrayList<Keyword> highestNPercentTerms;

    public Document (String personA, String personB, ArrayList<String> text){
        this.personA = personA;
        this.personB = personB;
        this.text = text;
        highestNPercentTerms = new ArrayList<Keyword>();
    }

    public String getPersonA() {
        return personA;
    }

    public String getPersonB() {
        return personB;
    }

    public ArrayList<String> getText() {
        return text;
    }

    public ArrayList<Keyword> getHighestNPercentTerms() {
        return highestNPercentTerms;
    }

    // Method which merges the object's ArrayList and the ArrayList passed as a parameter
    public void combineText(ArrayList<String> text) {
        this.text.addAll(text);
    }

    // Method which adds keywords with highest nPercent to the highestNPercentTerms ArrayList
    public void addHighestTerms(Keyword keyword){
        highestNPercentTerms.add(keyword);
    }

    @Override
    public String toString() {
        String textString = "";
        for (String s:text) {
            textString += (s + " ");
        }
        return "Person A:\t" + personA +
                "\nPerson B:\t" + personB +
                "\nTEXT:\t" + textString;
    }

}

