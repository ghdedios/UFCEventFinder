package dadm.frba.utn.edu.ar.ufceventfinder;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.net.URL;

public class UFCAdapter extends RecyclerView.Adapter<UFCAdapter.UFCEventViewHolder>  {

    @NonNull
    @Override
    public UFCEventViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull UFCEventViewHolder numberViewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
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
            Uri uri = Uri.parse(imageUrl);
            mEventImage.setImageURI(uri);
            mEventDescription.setText(description);
            mEventDistance.setText(String.valueOf(distance));
        }
    }

}
