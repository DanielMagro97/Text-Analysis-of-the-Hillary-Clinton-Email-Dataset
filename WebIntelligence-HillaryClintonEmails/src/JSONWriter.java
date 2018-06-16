import java.io.FileWriter;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class JSONWriter {

    public static void writeDocsToJSON(ArrayList<Document> documents) {

        JSONArray documentsJSON = new JSONArray();

        for (Document document : documents) {
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("personA", document.getPersonA());

            jsonObject.put("personB", document.getPersonB());

            JSONArray jsonTextArray = new JSONArray();
            jsonTextArray.addAll(document.getText());
            jsonObject.put("text", jsonTextArray);

            JSONArray jsonHighestNArray = new JSONArray();
            for (Keyword keyword : document.getHighestNPercentTerms()) {
                //jsonHighestNArray.add(keyword.toString());
                JSONObject jsonKeyword = new JSONObject();
                jsonKeyword.put("keyword", keyword.getTerm());
                jsonKeyword.put("weight", keyword.getWeight());
                jsonHighestNArray.add(jsonKeyword);
            }
            jsonObject.put("highestNPercentTerms", jsonHighestNArray);

            documentsJSON.add(jsonObject);
        }

        // writing the JSONObject into a file(info.json)
        try {
            FileWriter fileWriter = new FileWriter("documents.json");
            fileWriter.write(documentsJSON.toJSONString());
            fileWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeCorrespondentsToJSON(ArrayList<Correspondent> correspondents) {

        JSONArray documentsJSON = new JSONArray();

        for (Correspondent correspondent : correspondents) {
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("correspondent", correspondent.getCorrespondent());

            documentsJSON.add(jsonObject);
        }

        // writing the JSONObject into a file(info.json)
        try {
            FileWriter fileWriter = new FileWriter("correspondents.json");
            fileWriter.write(documentsJSON.toJSONString());
            fileWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
