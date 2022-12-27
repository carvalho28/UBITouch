package pt.ubi.di.pdm.ubitouch;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Objects;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private final ArrayList<Event> listRecyclerView;

    public RecyclerAdapter(Context context, ArrayList<Event> posts) {
        this.context = context;
        this.listRecyclerView = posts;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView Title;
        private TextView Description;
        private TextView EventDate;
        private TextView EventHour;
        private ImageView UserImage;
        private TextView verifiedFlag;
        private TextView unverifiedFLag;
        private TextView mapLocation;
        private TextView name;
        private TextView username;
        private ImageButton interested;
        private TextView share;
        private ImageView imageView;
        private VideoView videoView;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            Title = itemView.findViewById(R.id.postTitle);
            Description = itemView.findViewById(R.id.postDescription);
            EventDate = itemView.findViewById(R.id.postDate);
            UserImage = itemView.findViewById(R.id.postUserImage);
            EventHour = itemView.findViewById(R.id.postTime);
            verifiedFlag = itemView.findViewById(R.id.verifiedFlag);
            unverifiedFLag = itemView.findViewById(R.id.unverifiedFlag);
            mapLocation = itemView.findViewById(R.id.postLocalization);
            name = itemView.findViewById(R.id.postName);
            username = itemView.findViewById(R.id.postUsername);
            interested = itemView.findViewById(R.id.btnInterested);
            share = itemView.findViewById(R.id.postShare);

            imageView = itemView.findViewById(R.id.postImage);
            videoView = itemView.findViewById(R.id.postVideo);

            mapLocation.setOnClickListener(this);

            UserImage.setOnClickListener(this);

            name.setOnClickListener(this);

            username.setOnClickListener(this);

            interested.setOnClickListener(this);

            share.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Event event = listRecyclerView.get(position);

            int viewId = view.getId();
            if (viewId == R.id.postUserImage || viewId == R.id.postName || viewId == R.id.postUsername) {
                Intent i = new Intent(context, ProfileActivity.class);
                i.putExtra("userID", event.getUserID());
                context.startActivity(i);
            }

            // open maps with the location of the event
            if (viewId == R.id.postLocalization) {
                // log the event
                Log.d("Diogo", "Click" + position);
                Log.d("Diogo", "Latitude: " + event.getLatitude());
                Log.d("Diogo", "Longitude: " + event.getLongitude());
                String latitude = event.getLatitude();
                String longitude = event.getLongitude();
                if (!Objects.equals(latitude, "") && !Objects.equals(longitude, "")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:<" + latitude +
                            ">,<" + longitude
                            + ">?q=<" + latitude + ">,<" + longitude + ">(" + Title.getText() + ")"));
                    intent.setPackage("com.google.android.apps.maps");
                    context.startActivity(intent);
                }
            }

            if (view.getId() == R.id.btnInterested) {
                // if it is empty heart change to full heart
                if (interested.getTag().equals("empty")) {
                    // add event to the user's interested events
                    addInterestedEvent(event, interested);
                } else {
                    // if it is full heart change to empty heart
                    removeInterestedEvent(event, interested);
                }
            }

            if (view.getId() == R.id.postShare) {
                // share the event
                shareEvent(event);
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

        // itemViewHolder.EventDate.setText(posts.getEventDate());
        // set date as dd/mm/yyyy

        String date = posts.getEventDate();
        String time = posts.getEventHour();
        // if date is not null and not empty
        if (date != null && !Objects.equals(date, "") && !Objects.equals(date, "null")) {
            // convert date from 2022-12-22T00:00:00.000Z to dd/mm/yyyy
            String[] dateParts = date.substring(0, 10).split("-");
            String year = dateParts[0];
            String month = dateParts[1];
            String day = dateParts[2];
            date = day + "/" + month + "/" + year;
            itemViewHolder.EventDate.setText(date);
        }
        // if time is not null and not empty
        if (time != null && !Objects.equals(time, "") && !Objects.equals(time, "null")) {
            // convert time to hh:mm
            String[] timeParts = time.split(":");
            String hour = timeParts[0];
            String minutes = timeParts[1];
            time = hour + ":" + minutes;
            itemViewHolder.EventHour.setText(time);
        }

        if (posts.getImage().length() > 0)
            Picasso.get().load(posts.getImage()).into(itemViewHolder.UserImage);
        if (posts.getDescription() != null && !Objects.equals(posts.getDescription(), "")) {
            itemViewHolder.Description.setText(posts.getDescription());
        }
        // if (itemViewHolder.Description.length() > 0)
        // itemViewHolder.Description.setVisibility(View.VISIBLE);
        if (posts.getDescription() != null && !Objects.equals(posts.getDescription(), "")
                && !Objects.equals(posts.getDescription(), "null")) {
            itemViewHolder.Description.setVisibility(View.VISIBLE);
        }
        if (itemViewHolder.EventHour.length() > 0)
            itemViewHolder.EventHour.setVisibility(View.VISIBLE);
        if (itemViewHolder.mapLocation.length() > 0)
            itemViewHolder.mapLocation.setVisibility(View.VISIBLE);
        if (Objects.equals(posts.getLatitude(), "") && Objects.equals(posts.getLongitude(), "")) {
            itemViewHolder.mapLocation.setVisibility(View.INVISIBLE);
        }
        if (Objects.equals(posts.getEventHour(), "") || Objects.equals(posts.getEventHour(), "null")) {
            itemViewHolder.EventHour.setVisibility(View.INVISIBLE);
        }

        // load username and name
        itemViewHolder.username.setText("@" + posts.getUsername());
        itemViewHolder.name.setText(posts.getName());

        // if isInterested is true, change the heart to full
        if (!Objects.equals(posts.getIsInterested(), "0")) {
            itemViewHolder.interested.setImageResource(R.drawable.filled_heart);
            itemViewHolder.interested.setTag("full");
        } else {
            itemViewHolder.interested.setImageResource(R.drawable.empty_heart);
            itemViewHolder.interested.setTag("empty");
        }

        // if imageOrVideo is different from null and not empty
        if (posts.getImageOrVideo() != null && !Objects.equals(posts.getImageOrVideo(), "")
                && !Objects.equals(posts.getImageOrVideo(), "null")) {
            // if imageOrVideo is a video
            if (posts.getImageOrVideo().contains("video")) {
                itemViewHolder.imageView.setVisibility(View.GONE);
                itemViewHolder.videoView.setVisibility(View.VISIBLE);
                itemViewHolder.videoView.setVideoURI(Uri.parse(posts.getImageOrVideo()));
                // play loop
                itemViewHolder.videoView.setOnPreparedListener(mp -> {
                    mp.setLooping(true);
                });
                itemViewHolder.videoView.start();
            } else {
                itemViewHolder.videoView.setVisibility(View.GONE);
                itemViewHolder.imageView.setVisibility(View.VISIBLE);
                Picasso.get().load(posts.getImageOrVideo()).into(itemViewHolder.imageView);
            }
        }
    }

    @Override
    public int getItemCount() {
        return listRecyclerView.size();
    }

    private void addInterestedEvent(Event event, ImageView interested) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        String idUser = sharedPreferences.getString("id", "");
        String idEvent = event.getEventId();

        String url = "https://server-ubi-touch.herokuapp.com/interests/add";

        RequestQueue queue = Volley.newRequestQueue(context);
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("idUser", idUser);
            jsonBody.put("idEvent", idEvent);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                response -> {
                    Log.d("Diogo", "Response: " + response.toString());
                    interested.setImageResource(R.drawable.filled_heart);
                    interested.setTag("full");
                },
                error -> Log.d("Diogo", "Error: " + error.toString()));
        queue.add(jsonObjectRequest);

    }

    private void removeInterestedEvent(Event event, ImageView interested) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        String idUser = sharedPreferences.getString("id", "");
        String idEvent = event.getEventId();

        String url = "https://server-ubi-touch.herokuapp.com/interests/remove";

        RequestQueue queue = Volley.newRequestQueue(context);
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("idUser", idUser);
            jsonBody.put("idEvent", idEvent);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                response -> {
                    interested.setImageResource(R.drawable.empty_heart);
                    interested.setTag("empty");
                    Log.d("Diogo", "Response: " + response.toString());
                },
                error -> Log.d("Diogo", "Error: " + error.toString()));
        queue.add(jsonObjectRequest);

    }

    private void shareEvent(Event event) {
        String title = event.getTitle();
        String description = event.getDescription();
        String date = event.getEventDate();

        if (date != null && !Objects.equals(date, "") && !Objects.equals(date, "null")) {
            // convert date from 2022-12-22T00:00:00.000Z to dd/mm/yyyy
            String[] dateParts = date.substring(0, 10).split("-");
            String year = dateParts[0];
            String month = dateParts[1];
            String day = dateParts[2];
            date = day + "/" + month + "/" + year;
        }

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, title + " - " + description + " - " + date);
        sendIntent.setType("text/plain");
        context.startActivity(sendIntent);

    }
}