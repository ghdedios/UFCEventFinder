package dadm.frba.utn.edu.ar.ufceventfinder.utilities;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public final class UFCJsonUtils {

    public static JSONObject[] getSimpleEventsJsonsFromWholeJson(Context context, String eventsJsonStr)
            throws JSONException {

        // The description is the "base_title" element of the json
        final String UFC_description = "base_title";

        // The event location name, from which you will have to calculate the distance
        final String UFC_location = "location";
        final String UFC_latitude = "latitude";
        final String UFC_longitude = "longitude";

        // The event image URL/s are these elements of the json. We will need both.
        final String UFC_image_URL = "feature_image";
        final String UFC_secondary_image_URL = "secondary_feature_image";

        //The event date
        final String UFC_date = "event_date";


        JSONObject[] parsedUFCEventsData;

        JSONArray ufcEventsJson = new JSONArray(eventsJsonStr);

        parsedUFCEventsData = new JSONObject[ufcEventsJson.length()];

        for (int i = 0; i < ufcEventsJson.length(); i++) {
            String description;
            String location;
            String imageURL;
            String secondImageURL;
            String date;
            double latitude = 0;
            double longitude = 0;


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
                if(singleUFCEvent.has(UFC_longitude) && singleUFCEvent.has(UFC_latitude)){
                    latitude = singleUFCEvent.getDouble(UFC_latitude);
                    longitude = singleUFCEvent.getDouble(UFC_longitude);
                }
            }catch(JSONException e){
                location = "null";
                latitude = 0;
                longitude = 0;
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

            try {
                date = singleUFCEvent.getString(UFC_date);
            }catch(JSONException e){
                date = "null";
                e.printStackTrace();
            }



            JSONObject ufcSingleEventJson = new JSONObject();
            try{
                ufcSingleEventJson.put("description",description);
                ufcSingleEventJson.put("location",location);
                ufcSingleEventJson.put("longitude",longitude);
                ufcSingleEventJson.put("latitude",latitude);
                if(imageURL != null){
                    ufcSingleEventJson.put("imageURL",imageURL);
                }else if(secondImageURL != null){
                    ufcSingleEventJson.put(imageURL,secondImageURL);
                }else{
                    ufcSingleEventJson.put("imageURL","null");
                }
                ufcSingleEventJson.put("date",date);
            }catch (JSONException e){
                e.printStackTrace();
                throw new RuntimeException(e);
            }
            parsedUFCEventsData[i] = ufcSingleEventJson;
        }
        return parsedUFCEventsData;
    }
}
