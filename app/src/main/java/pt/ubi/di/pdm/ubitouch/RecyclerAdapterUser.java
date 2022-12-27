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


public class RecyclerAdapterUser extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private final ArrayList<User> listRecyclerView;
    private final Context context;

    public RecyclerAdapterUser(Context context, ArrayList<User> users) {
        this.context = context;
        this.listRecyclerView = users;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView Name;
        private TextView Username;
        private ImageView Image;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            Name = itemView.findViewById(R.id.name);
            Username = itemView.findViewById(R.id.username);
            Image = itemView.findViewById(R.id.userImage);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            User user = listRecyclerView.get(position);

            int viewId = view.getId();

            if (viewId == R.id.btnRemove){
                Log.i("remove", "Removes user");
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
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        User users = (User) listRecyclerView.get(position);
        itemViewHolder.Name.setText(users.getName());
        itemViewHolder.Username.setText("@" + users.getUsername());
        if (users.getImage().length() > 0)
            Picasso.get().load(users.getImage()).into(itemViewHolder.Image);

    }

    @Override
    public int getItemCount() {
        return listRecyclerView.size();
    }
}
