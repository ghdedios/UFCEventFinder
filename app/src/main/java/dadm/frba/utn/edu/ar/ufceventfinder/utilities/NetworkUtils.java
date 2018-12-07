package dadm.frba.utn.edu.ar.ufceventfinder.utilities;

import android.net.Uri;

import java.net.MalformedURLException;
import java.net.URL;

public class NetworkUtils {
    final static String UFC_EVENT_URL = "http://ufc-data-api.ufc.com/api/v3/iphone/events";

    /**
     * Builds URL from static route defined in UFC_EVENT_URL. If a search query is needed,
     * you only have to add the appendQueryParameter() between buildUpon() and build()
     */

    public static URL buildUrl() {
        Uri builtUri = Uri.parse(UFC_EVENT_URL).buildUpon().build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    
}
