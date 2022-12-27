package pt.ubi.di.pdm.ubitouch;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RecyclerAdapterUser extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    private final Context context;
    private ArrayList<User> listRecyclerView;
    // copy of the list
    private ArrayList<User> filteredList = new ArrayList<>();

    public RecyclerAdapterUser(Context context, ArrayList<User> users) {
        this.context = context;
        this.listRecyclerView = users;
        filteredList = users;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                if (charString.isEmpty()) {
                    filteredList = listRecyclerView;
                } else {
                    if (listRecyclerView != null) {
                        ArrayList<User> filteredList = new ArrayList<>();
                        for (User row : listRecyclerView) {
                            if (row.getName().toLowerCase().contains(charString.toLowerCase())
                                    || row.getUsername().toLowerCase().contains(charString.toLowerCase())) {
                                filteredList.add(row);
                            }
                        }
                        listRecyclerView = filteredList;
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = listRecyclerView;
                return filterResults;

            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                listRecyclerView = (ArrayList<User>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public void clearFilter() {
        listRecyclerView = filteredList;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView Name;
        private TextView Username;
        private ImageView Image;
        private Button btnRemove;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            Name = itemView.findViewById(R.id.name);
            Username = itemView.findViewById(R.id.username);
            Image = itemView.findViewById(R.id.userImage);
            btnRemove = itemView.findViewById(R.id.btnRemove);

            btnRemove.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            User user = listRecyclerView.get(position);
            if (view.getId() == R.id.btnRemove) {
                // remove user
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Remove user");
                builder.setMessage("Are you sure you want to remove this user?");
                builder.setPositiveButton("Yes", (dialog, which) -> removeUser(user));
                builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
                builder.show();

            }
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.user_recycler, parent, false);
        return new ItemViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        RecyclerAdapterUser.ItemViewHolder itemViewHolder = (RecyclerAdapterUser.ItemViewHolder) holder;
        User users = (User) listRecyclerView.get(position);
        if (users.getImage().length() > 0)
            Picasso.get().load(users.getImage()).into(itemViewHolder.Image);
        itemViewHolder.Name.setText(users.getName());
        itemViewHolder.Username.setText("@" + users.getUsername());

    }

    @Override
    public int getItemCount() {
        return listRecyclerView.size();
    }

    private void removeUser(User user) {
        String userId = user.getUserId();
        String url = "https://server-ubi-touch.herokuapp.com/users/deleteAdmin/" + userId;

        // get token from shared preferences
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");
        Log.d("Diogo", token);

        RequestQueue queue = Volley.newRequestQueue(context);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, url, null,
                response -> {
                    try {
                        String message = response.getString("message");
                        Log.d("Diogo", message);
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, AdminUsersActivity.class);
                        context.startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
                    try {
                        String message = error.getMessage();
                        Log.d("Diogo", message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", "Bearer " + token);
                return params;
            }
        };
        queue.add(jsonObjectRequest);
    }

}
