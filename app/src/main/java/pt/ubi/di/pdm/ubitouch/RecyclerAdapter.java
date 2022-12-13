package pt.ubi.di.pdm.ubitouch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import com.squareup.picasso.Picasso;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private final Context context;
    private final ArrayList<PostActivity> listRecyclerView;

    public RecyclerAdapter(Context context, ArrayList<PostActivity> posts){
        this.context = context;
        this.listRecyclerView = posts;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder{
        // private TextView Username; // first name + last name
        private TextView Title;
        private TextView Description;
        private TextView Date;
        private TextView EventDate;
        private TextView EventHour;
        private ImageView UserImage;

        public ItemViewHolder(@NonNull View itemView){
            super(itemView);
            Title = itemView.findViewById(R.id.postTitle);
            Description = itemView.findViewById(R.id.postDescription);
            Date = itemView.findViewById(R.id.postDate);
            UserImage = itemView.findViewById(R.id.postUserImage);
//            EventDate = itemView.findViewById(R.id.eventDate);
//            EventHour = itemView.findViewById(R.id.eventHour);
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
        PostActivity posts = (PostActivity) listRecyclerView.get(position);
        itemViewHolder.Title.setText(posts.getTitle());
        itemViewHolder.Description.setText(posts.getEventDescription());
        itemViewHolder.Date.setText(posts.getDate());
        itemViewHolder.EventDate.setText(posts.getEventDate());
        itemViewHolder.EventHour.setText(posts.getEventHour());
//        itemViewHolder.UserImage.Picasso.; //imagem
        Picasso.get().load(posts.getImage()).into(itemViewHolder.UserImage);
    }

    @Override
    public int getItemCount() {
        return listRecyclerView.size();
    }
}