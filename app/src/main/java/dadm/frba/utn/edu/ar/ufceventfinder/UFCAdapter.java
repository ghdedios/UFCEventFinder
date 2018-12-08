package dadm.frba.utn.edu.ar.ufceventfinder;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

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

        try {
            description = ufcEvent.getString("description");
            location = ufcEvent.getString("location");
            imageURL = ufcEvent.getString("imageURL");
            //TODO:add DATE
        }catch (JSONException e){
            e.printStackTrace();
        }


        //TODO: add DATE
        holder.bind(imageURL,description,1);
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

        //Will display de UFC Event Description
        TextView mEventDescription;

        //Will display de UFC Event Distance (if it's defined)
        TextView mEventDistance;

        public UFCEventViewHolder (@NonNull View itemView) {
            super(itemView);

            mEventImage = (SimpleDraweeView) itemView.findViewById(R.id.avatar);
            mEventDescription = (TextView) itemView.findViewById(R.id.tv_description);
            mEventDistance = (TextView) itemView.findViewById(R.id.tv_distance);
        }

        void bind(String imageUrl, String description, int distance){

            if (imageUrl != null){
                Uri uri = Uri.parse(imageUrl);
                mEventImage.setImageURI(uri);
            }
            mEventDescription.setText(description);
            mEventDistance.setText(String.valueOf(distance));
        }
    }

}
