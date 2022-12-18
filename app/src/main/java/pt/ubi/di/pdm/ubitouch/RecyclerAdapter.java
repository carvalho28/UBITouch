package pt.ubi.di.pdm.ubitouch;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import com.squareup.picasso.Picasso;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private final Context context;
    private final ArrayList<Event> listRecyclerView;

    public RecyclerAdapter(Context context, ArrayList<Event> posts){
        this.context = context;
        this.listRecyclerView = posts;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        // private TextView Username; // first name + last name
        private TextView Title;
        private TextView Description;
        private TextView EventDate;
        private TextView EventHour;
        private ImageView UserImage;
        private TextView verifiedFlag;
        private TextView unverifiedFLag;

        public ItemViewHolder(@NonNull View itemView){
            super(itemView);
            Title = itemView.findViewById(R.id.postTitle);
            Description = itemView.findViewById(R.id.postDescription);
            EventDate = itemView.findViewById(R.id.postDate);
            UserImage = itemView.findViewById(R.id.postUserImage);
            EventHour = itemView.findViewById(R.id.postTime);
            verifiedFlag = itemView.findViewById(R.id.verifiedFlag);
            unverifiedFLag = itemView.findViewById(R.id.unverifiedFlag);

            UserImage.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Event event = listRecyclerView.get(position);

            if (view.getId() == R.id.postUserImage) {
                Log.d("Diogo", "Click" + position);
            }
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(
        R.layout.activity_post, parent, false);
        return new ItemViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        Event posts = (Event) listRecyclerView.get(position);
        itemViewHolder.Title.setText(posts.getTitle());
        itemViewHolder.EventDate.setText(posts.getEventDate());
        Picasso.get().load(posts.getImage()).into(itemViewHolder.UserImage);
        itemViewHolder.Description.setText(posts.getDescription());
        if (itemViewHolder.Description.length() > 0)
            itemViewHolder.Description.setVisibility(View.VISIBLE);
        itemViewHolder.EventHour.setText(posts.getEventHour());
        if(itemViewHolder.EventHour.length() > 0)
            itemViewHolder.EventHour.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return listRecyclerView.size();
    }
}