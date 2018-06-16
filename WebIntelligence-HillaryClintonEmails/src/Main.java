import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Main {

    // Global Variables
    // Global Variable of type int which stores the index of a for each loop
    private static int index;
    // Declaring a list which will store all unique terms in the collection
    private static ArrayList<String> uniqueTerms;
    // Global Variable which stores the Term x Document Weight Matrix as a 2D array
    private static double[][] termByDocWeightMatrix;

    public static void main(String[] args) throws IOException {
        // i) Declaring an ArrayList of emails and Initialising it to the output of the readEmails method
        System.out.println("Reading Emails from Database");
        ArrayList<Email> emails = EmailReader.readEmails();

        // ii) Calling the method which converts the email text to Lower Case
        System.out.println("Converting content of emails to lowercase, Removing stop-words and Stemming words");
        foldCase(emails);

        // iii) Calling the stop-word remover
        removeStopWords(emails);

        // iv) Calling the Stemmer method
        stemText(emails);

        // v) Calling the method which builds the Documents from the collection of Emails
        System.out.println("Constructing documents from emails between every 2 correspondents");
        ArrayList<Document> documents = createDocuments(emails);
        // Calling the method which calculates the term weight using tf-idf
        System.out.println("Calculating the term weights (tf-idf matrix)");
        calculateTermWeight(documents);

        // vi) Calling the method which finds the terms with the highest n% weight for each document
        System.out.println("Finding the words with the highest n% term weight in each document");
        findHighestNpercent(documents);

        // vii) Calling the method which finds the terms with the highest n% weight for each correspondent
        System.out.println("Finding the words with the highest n% term weight for each correspondent");
        ArrayList<Correspondent> correspondents = findHighestNpercentPerCorrespondent(emails);

        // D3) Calling the method which Saves the Generated Documents as a JSON file to disk
        JSONWriter.writeDocsToJSON(documents);

        // D3) Calling the method which Saves the List of Correspondents as a JSON file to disk
        JSONWriter.writeCorrespondentsToJSON(correspondents);

        //printEmails(emails);
        //printDocuments(documents);
    }


    // D2ii) Method which turns all the text inside the emails to lower case
    private static void foldCase(ArrayList<Email> emails) {
        // for each email in the list of emails
        for (Email email : emails) {
            // for each word in the list of words (text)
            for (int i = 0; i < email.getText().size(); i++) {
                // set the string 'lowerCase' to the lower case of the current word
                final String lowerCase = email.getText().get(i).toLowerCase();
                // set the current word to the value of lowerCase
                email.setText(lowerCase, i);
            }
        }
    }


    // D2iii) Method which removes the Stop Words from the emails
    private static void removeStopWords(ArrayList<Email> emails) throws IOException {
        // Initialising an array of stop words to the contents of
        String[] stopWords = Files.readAllLines(Paths.get("stopwords.txt")).toArray(new String[]{});

        // for each email in the list of emails
        for (Email email : emails) {
            // for each word in the list of words (text)
            for (int i = 0; i < email.getText().size(); i++) {
                // if the current word is a stop word
                if (isStopWord(email.getText().get(i), stopWords)) {
                    // remove the current word from the arraylist containing the text
                    email.getText().remove(i);
                    // and decrement the index since the rest of the words have shifted to the left
                    i--;
                }
            }
        }
    }
    // Method which checks if a word is as stop word
    private static boolean isStopWord(String word, String[] stopWords) {
        // loop through all the stop words
        for (int i = 0; i < stopWords.length; i++) {
            // if the word is a stop word return true
            if (word.equals(stopWords[i])) {
                return true;
            }
        }
        // if the word is not in the list of stop words, return false
        return false;
    }


    // D2iv) Method which applies Porter's Stemmer on the text of the emails
    private static void stemText(ArrayList<Email> emails) {
        // for each email in the list of emails
        for (Email email : emails) {
            // for each word in the list of words (text)
            for (int i = 0; i < email.getText().size(); i++) {
                // Create a new stemmer object
                Stemmer stemmer = new Stemmer();
                // loop which adds every character of the current word to the stemmer object
                for (int j = 0; j < email.getText().get(i).length(); j++) {
                    stemmer.add(email.getText().get(i).charAt(j));
                }
                // calling the stem method
                stemmer.stem();

                // setting the current word to the stemmed word
                email.setText(stemmer.toString(), i);
            }
        }
    }

    // D2v) Method which converts the list of Emails into a list of Documents (conversation thread between 2 people)
    private static ArrayList<Document> createDocuments(ArrayList<Email> emails) {
        // declaring a new ArrayList of documents
        ArrayList<Document> documents = new ArrayList<>();
        // for each email in the list of emails
        for (Email email : emails) {
            // if the email is already part of the ArrayList of documents
            if (documentExists(email, documents)) {
                // then merge the text of the email with that of the document
                documents.get(index).combineText(email.getText());
            }
            // if the email is not part of any document
            else {
                Document document = new Document(email.getTo(), email.getFrom(), email.getText());
                documents.add(document);
            }
        }
        // return the ArrayList of documents
        return documents;
    }
    // Method which checks if a document already exists for a conversation between 2 people
    private static boolean documentExists(Email email, ArrayList<Document> documents) {
        index = 0;
        for (Document document : documents) {
            if ((email.getTo().equals(document.getPersonA()) && email.getFrom().equals(document.getPersonB()))
                    || (email.getTo().equals(document.getPersonB()) && email.getFrom().equals(document.getPersonA()))) {
                return true;
            }
            index++;
        }
        return false;
    }

    // D2v) Method which calculates the weight of each term in the collection
    private static void calculateTermWeight(ArrayList<Document> documents) {
        // Initialise the list of all unique terms in the collection with the findUniqueTerms method
        uniqueTerms = findUniqueTerms(documents);

        // Declaring the Term x Document Frequency Matrix as a 2D array
        int[][] termByDocFreqMatrix = new int[uniqueTerms.size()][documents.size()];
        for (int i = 0; i < uniqueTerms.size(); i++) {
            for (int j = 0; j < documents.size(); j++) {
                // Initialising each element of the termByDocFreqMatrix using the calculateFrequency method
                termByDocFreqMatrix[i][j] = calculateFrequency(uniqueTerms.get(i), documents.get(j));
            }
        }

        // Initialising the Term x Document Weight Matrix as a 2D array
        termByDocWeightMatrix = new double[uniqueTerms.size()][documents.size()];
        // Calling the method which finds the highest term frequency of every document
        int[] maxFreq = findMaxFreqs(termByDocFreqMatrix);
        // Calling the method which finds the document frequency of every term in the collection
        int[] docFreq = findDocFreqs(termByDocFreqMatrix);

        // looping through the termByDocWeightMatrix to initialise every weight
        for (int i = 0; i < uniqueTerms.size(); i++) {
            /*
            for (int j = 0; j < documents.size(); j++) {
                // Initialising each element of the termByDocWeightMatrix using the formula:
                try {
                    //    tf-idf                =          (    tf(t_i,d_j)           / maxtf(d_j) ) *     log(   N             / df(t_i)   )
                    termByDocWeightMatrix[i][j] = (double) (termByDocFreqMatrix[i][j] / maxFreq[j]) * Math.log(documents.size() / docFreq[i]);
                } catch (RuntimeException e){
                    termByDocWeightMatrix[i][j] = 0.0;
                }
            }
            */
            for (int j = 0; j < documents.size(); j++) {
                // Initialising each element of the termByDocWeightMatrix using the formula:
                //    tf-idf                =          (    tf(t_i,d_j)          ) *     log(       N          / df(t_i)   )
                termByDocWeightMatrix[i][j] = (double) (termByDocFreqMatrix[i][j]) * Math.log(documents.size() / docFreq[i]);
            }
        }
    }

    // Method which returns an ArrayList containing all the unique terms in the collection
    private static ArrayList<String> findUniqueTerms(ArrayList<Document> documents) {
        ArrayList<String> uniqueTerms = new ArrayList<>();

        for (Document document : documents) {
            for (String s : document.getText()) {
                if (!isInList(s, uniqueTerms)) {
                    uniqueTerms.add(s);
                }
            }
        }

        return uniqueTerms;
    }
    // Method which determines if the term is already in the list of unique terms
    private static boolean isInList(String term, ArrayList<String> terms) {
        for (String s : terms) {
            if (s.equals(term)) {
                return true;
            }
        }
        return false;
    }

    // Method which calculates the number of times a term appears within a document
    private static int calculateFrequency(String term, Document document) {
        int count = 0;
        for (String s : document.getText()) {
            if (s.equals(term)) {
                count++;
            }
        }
        return count;
    }

    // Method which finds the highest term frequency of every document in the collection
    private static int[] findMaxFreqs(int[][] termByDocFreqMatrix) {
        // declaring an array to store the maximum term frequency of each document
        int maxFreq[] = new int[termByDocFreqMatrix[0].length];
        Arrays.fill(maxFreq, 0);

        // loop through the maxFreq array
        for (int j = 0; j < maxFreq.length; j++) {
            // loop through every term for every document to find the highest term frequency
            for (int i = 0; i < termByDocFreqMatrix[0].length; i++) {
                if (termByDocFreqMatrix[i][j] > maxFreq[j]) {
                    maxFreq[j] = termByDocFreqMatrix[i][j];
                }
            }
        }

        return maxFreq;
    }

    // Method which finds the document frequency of every term in the collection
    private static int[] findDocFreqs(int[][] termByDocFreqMatrix) {
        int docFreq[] = new int[termByDocFreqMatrix.length];
        Arrays.fill(docFreq, 0);

        // loop through the docFreq array
        for (int i = 0; i < docFreq.length; i++) {
            // loop through every document to check each term's frequency
            for (int j = 0; j < termByDocFreqMatrix[i].length; j++) {
                // if the term has a frequency > 0 for the current document
                if (termByDocFreqMatrix[i][j] > 0) {
                    // increment the docFreq for that term
                    docFreq[i] += 1;
                }
            }
        }

        return docFreq;
    }

    // Method which finds the terms in each document with the highest n% of weight
    private static void findHighestNpercent(ArrayList<Document> documents) {
        for (int j = 0; j < documents.size(); j++) {
            ArrayList<Double> wordWeights = new ArrayList<>();

            for (int i = 0; i < termByDocWeightMatrix.length; i++) {
                if (termByDocWeightMatrix[i][j] > 0){
                    wordWeights.add(termByDocWeightMatrix[i][j]);
                }
            }

            if (wordWeights.size() > 0) {
                double nPercent = 10;
                nPercent = 1 - (nPercent/100);

                Collections.sort(wordWeights);

                int cutoffIndex = (int) (wordWeights.size() * nPercent);
                double cutoff = wordWeights.get(cutoffIndex);

                for (int i = 0; i < termByDocWeightMatrix.length; i++) {
                    if (termByDocWeightMatrix[i][j] > cutoff) {
                        documents.get(j).addHighestTerms(new Keyword(uniqueTerms.get(i), termByDocWeightMatrix[i][j]));
                    }
                }
            }
        }
    }


    // D2vii - Method which finds the highest n% of terms used by each correspondent
    private static ArrayList<Correspondent> findHighestNpercentPerCorrespondent(ArrayList<Email> emails) {
        ArrayList<Correspondent> correspondents = new ArrayList<>();
        for (Email email : emails) {
            if (!isInCorrespondentsList(email.getFrom(), correspondents)){
                Correspondent correspondent = new Correspondent(email.getFrom(), email.getText());
                correspondents.add(correspondent);
            } else {
                correspondents.get(index).combineText(email.getText());
            }
        }

        for (Correspondent correspondent : correspondents) {

        }

        return correspondents;
    }
    // Method which determines if a correspondent is already in the list of correspondents
    private static boolean isInCorrespondentsList(String correspondentName, ArrayList<Correspondent> correspondents) {
        index = 0;
        for (Correspondent correspondent : correspondents) {
            if (correspondent.getCorrespondent().equals(correspondentName)) {
                return true;
            }
            index++;
        }
        return false;
    }



    // Method which prints the Collection of Emails
    public static void printEmails(ArrayList<Email> emails) {
        for (Email email : emails) {
            System.out.println(email + "\n");
        }
    }
    // Method which prints the Collection of Documents
    public static void printDocuments(ArrayList<Document> documents) {
        for (Document document : documents) {
            System.out.println(document + "\n");
        }
    }
}