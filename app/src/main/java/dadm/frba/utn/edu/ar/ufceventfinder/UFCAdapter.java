package dadm.frba.utn.edu.ar.ufceventfinder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
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

import java.net.URL;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.util.Date;

public class UFCAdapter extends RecyclerView.Adapter<UFCAdapter.UFCEventViewHolder>  {

    private int mNumberItems;

    private JSONObject[] mUFCEventsData;

    public UFCAdapter(int numberOfItems){
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

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull UFCEventViewHolder holder, int i) {
        JSONObject ufcEvent = mUFCEventsData[i];

        String description = null;
        String location = null;
        String imageURL = null;
        String dateString = null;

        try {
            description = ufcEvent.getString("description");
            location = ufcEvent.getString("location");
            imageURL = ufcEvent.getString("imageURL");
            dateString = ufcEvent.getString("date");
        }catch (JSONException e){
            e.printStackTrace();
        }

        Instant instant = Instant.parse(dateString);
        Date realDate = DateTimeUtils.toDate(instant);



        holder.bind(imageURL,description,1,realDate);
    }

    @Override
    public int getItemCount() {
        if (mUFCEventsData == null) {
            return 0;
        }
        return mUFCEventsData.length;
    }

    //TODO: delete
    public void setUFCEventsData(JSONObject[] ufcEventsData) {
        mUFCEventsData = ufcEventsData;
        notifyDataSetChanged();
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

        public UFCEventViewHolder (@NonNull View itemView) {
            super(itemView);

            mEventImage = (SimpleDraweeView) itemView.findViewById(R.id.avatar);
            mEventDescription = (TextView) itemView.findViewById(R.id.tv_description);
            mEventDistance = (TextView) itemView.findViewById(R.id.tv_distance);
            mEventDate = (TextView) itemView.findViewById(R.id.tv_date);

        }

        void bind(String imageUrl, String description, int distance, Date date){

            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat simpleDate = new SimpleDateFormat("dd/MM/yyyy");
            String dateString = simpleDate.format(date);

            if (imageUrl != null && !imageUrl.equals("")){
                Uri uri = Uri.parse(imageUrl);
                mEventImage.setImageURI(uri);
            }else{
                Uri uri =  new Uri.Builder()
                        .scheme(UriUtil.LOCAL_RESOURCE_SCHEME)
                        .path(String.valueOf(R.mipmap.ic_launcher))
                        .build();
                mEventImage.setImageURI(uri);
            }
            mEventDescription.setText(description);
            mEventDistance.setText(String.valueOf(distance));
            mEventDate.setText(dateString);
        }
    }

}
