public class Keyword {

    private String term;
    private double weight;

    public Keyword (String term, double weight) {
        this.term = term;
        this.weight = weight;
    }

    public String getTerm() {
        return term;
    }

    public double getWeight() {
        return weight;
    }

}