package dadm.frba.utn.edu.ar.ufceventfinder;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.common.util.UriUtil;
import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONException;
import org.json.JSONObject;
import org.threeten.bp.DateTimeUtils;
import org.threeten.bp.Instant;

import java.text.SimpleDateFormat;
import java.util.Date;

import static android.support.v4.widget.TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM;

public class UFCAdapter extends RecyclerView.Adapter<UFCAdapter.UFCEventViewHolder> {

    private int mNumberItems;

    private JSONObject[] mUFCEventsData;

    private Location mUserLocation;

    public UFCAdapter(int numberOfItems) {
        mNumberItems = numberOfItems;
    }


    @NonNull
    @Override
    public UFCEventViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.ufc_event_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        UFCEventViewHolder viewHolder = new UFCEventViewHolder(view);

        mUserLocation = new Location("User");

        //Get user location and save it in mUserLocation;


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull UFCEventViewHolder holder, int i) {
        JSONObject ufcEvent = mUFCEventsData[i];

        String description = null;
        String location = null;
        String imageURL = null;
        String dateString = null;
        double latitude = 0;
        double longitude = 0;

        try {
            description = ufcEvent.getString("description");
            location = ufcEvent.getString("location");
            imageURL = ufcEvent.getString("imageURL");
            dateString = ufcEvent.getString("date");
            latitude = ufcEvent.getDouble("latitude");
            longitude = ufcEvent.getDouble("longitude");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Parse latitude and longitude in an Location object
        Location eventLocation = new Location("event location");
        eventLocation.setLatitude(latitude);
        eventLocation.setLongitude(longitude);
        eventLocation.setProvider(location);

        //Parse String timestamp date into Date object
        Instant instant = Instant.parse(dateString);
        Date realDate = DateTimeUtils.toDate(instant);

        holder.bind(imageURL, description, realDate, eventLocation);
    }


    @Override
    public int getItemCount() {
        if (mUFCEventsData == null) {
            return 0;
        }
        return mUFCEventsData.length;
    }

    public void setUFCEventsData(JSONObject[] ufcEventsData) {
        mUFCEventsData = ufcEventsData;
        notifyDataSetChanged();
    }

    public void setUserLocation(Location userLocation) {
        mUserLocation = userLocation;
    }

    public class UFCEventViewHolder extends RecyclerView.ViewHolder {

        //Will display the UFC Event Image
        SimpleDraweeView mEventImage;

        //Will display the UFC Event Description
        TextView mEventDescription;

        //Will display the UFC Event Distance (if it's defined )
        TextView mEventDistance;

        //Will display the UFC Event Date
        TextView mEventDate;

        //Will contain the view for context use
        View mView;

        public UFCEventViewHolder(@NonNull View itemView) {
            super(itemView);

            mEventImage = (SimpleDraweeView) itemView.findViewById(R.id.avatar);
            mEventDescription = (TextView) itemView.findViewById(R.id.tv_description);
            mEventDistance = (TextView) itemView.findViewById(R.id.tv_distance);
            mEventDate = (TextView) itemView.findViewById(R.id.tv_date);
            mView = itemView;

            TextViewCompat.setAutoSizeTextTypeWithDefaults(mEventDistance, AUTO_SIZE_TEXT_TYPE_UNIFORM);
            TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(mEventDescription, 8, 18, 1, TypedValue.COMPLEX_UNIT_DIP);

        }

        void bind(String imageUrl, String description, Date date, Location eventLocation) {
            String distanceAsString = "";
            String locationName = eventLocation.getProvider();
            if (!locationName.equals("null")) {
                //Ask if Location Permission is granted
                if (ContextCompat.checkSelfPermission(mView.getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    //Permission granted, now calculate the real distance
                    distanceAsString = getDistanceFromUserAsString(eventLocation);
                    if (distanceAsString != null) {
                        distanceAsString = distanceAsString.concat(" Km");
                    } else {
                        //Show the location city name
                        distanceAsString = locationName;
                    }
                } else {
                    //Show the location city name
                    distanceAsString = locationName;
                }
            }


            //Format Date object to hardcoded Argentinian date format dd/MM/yyyy
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat simpleDate = new SimpleDateFormat("dd/MM/yyyy");
            String dateString = simpleDate.format(date);

            //If there is an image URL, show it. If there isn't, show a local image
            if (imageUrl != null && !imageUrl.equals("")) {
                Uri uri = Uri.parse(imageUrl);
                mEventImage.setImageURI(uri);
            } else {
                Uri uri = new Uri.Builder()
                        .scheme(UriUtil.LOCAL_RESOURCE_SCHEME)
                        .path(String.valueOf(R.mipmap.ic_launcher))
                        .build();
                mEventImage.setImageURI(uri);
            }
            mEventDescription.setText(description);
            mEventDistance.setText(distanceAsString);
            mEventDate.setText(dateString);
        }
    }

    private String getDistanceFromUserAsString(Location locationAsString) {

        if (mUserLocation == null || mUserLocation.toString() == "" || mUserLocation.getLatitude() == 0) {
            return null;
        } else {
            float distanceInKm = mUserLocation.distanceTo(locationAsString) / 1000;
            return String.valueOf(distanceInKm);
        }
    }
}
