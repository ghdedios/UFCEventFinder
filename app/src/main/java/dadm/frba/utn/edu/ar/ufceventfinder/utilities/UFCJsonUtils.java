package dadm.frba.utn.edu.ar.ufceventfinder.utilities;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public final class UFCJsonUtils {

    public static JSONObject[] getSimpleEventsStringsFromJson(Context context, String eventsJsonStr)
            throws JSONException {

        // The description is the "base_title" element of the json
        final String UFC_description = "base_title";

        // The event location name, from which you will have to calculate the distance
        final String UFC_location = "location";

        // The event image URL/s are these elements of the json. We will need both.
        final String UFC_image_URL = "feature_image";
        final String UFC_secondary_image_URL = "secondary_feature_image";

        JSONObject[] parsedUFCEventsData = null;

        //TODO: Delete this comment if the instantiation of JSONArray works fine
//        JSONObject eventsJson = new JSONObject(eventsJsonStr);

        JSONArray ufcEventsJson = new JSONArray(eventsJsonStr);

        parsedUFCEventsData = new JSONObject[ufcEventsJson.length()];

        for (int i = 0; i < ufcEventsJson.length(); i++) {
            String description;
            String location;
            String imageURL;
            String secondImageURL;

            /* Get the JSON object representing the event */
            JSONObject singleUFCEvent = ufcEventsJson.getJSONObject(i);

            /* Get every data needed from the four JSON atributes*/
            try {
                description = singleUFCEvent.getString(UFC_description);
            }catch (JSONException e){
                description = "No Description Provided";
                e.printStackTrace();
            }

            try {
                location = singleUFCEvent.getString(UFC_location);
            }catch(JSONException e){
                location = "null";
                e.printStackTrace();
            }

            try {
                imageURL = singleUFCEvent.getString(UFC_image_URL);
            } catch (JSONException e){
                imageURL = null;
                e.printStackTrace();
            }
            try {
                secondImageURL = singleUFCEvent.getString(UFC_secondary_image_URL);
            }catch (JSONException e){
                secondImageURL = null;
                e.printStackTrace();
            }

            JSONObject ufcSingleEventJson = new JSONObject();
            try{
                ufcSingleEventJson.put("description",description);
                ufcSingleEventJson.put("location",location);
                if(imageURL != null){
                    ufcSingleEventJson.put("imageURL",imageURL);
                }else if(secondImageURL != null){
                    ufcSingleEventJson.put(imageURL,secondImageURL);
                }else{
                    ufcSingleEventJson.put("imageURL","null");
                }
            }catch (JSONException e){
                e.printStackTrace();
                throw new RuntimeException(e);
            }

            parsedUFCEventsData[i] = ufcSingleEventJson;
        }

        return parsedUFCEventsData;
    }

}
