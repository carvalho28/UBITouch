package pt.ubi.di.pdm.ubitouch;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.Objects;

public class RecyclerAdapterUser extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private final Context context;
    private final ArrayList<User> listRecyclerView;

    public RecyclerAdapterUser(Context context, ArrayList<User> users) {
        this.context = context;
        this.listRecyclerView = users;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView Name;
        private TextView Username;
        private ImageView Image;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            Name = itemView.findViewById(R.id.name);
            Username = itemView.findViewById(R.id.username);
            Image = itemView.findViewById(R.id.userImage);
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
}
