package com.example.mobileliarsdice.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Intent;
import java.util.List;

import com.example.mobileliarsdice.InvitationActivity;
import com.example.mobileliarsdice.Models.Users;
import com.example.mobileliarsdice.R;
import com.example.mobileliarsdice.UserGlobals;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

/**
 *
 */
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private Context mContext;
    private List<Users>  users;

    public UserAdapter(Context c, List<Users> u){
        this.mContext = c;
        this.users = u;
    }

    /**
     *
     * @param viewGroup
     * @param i
     * @return
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.user_item, viewGroup, false);
        return new UserAdapter.ViewHolder(v);
    }

    /**
     *
     * @param viewHolder
     * @param i
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final Users u = users.get(i);
        viewHolder._USER_NAME.setText(u.getUserName());
        if(u.getUrl().equals("NONE")){
            viewHolder._USER_IMAGE.setImageResource(R.mipmap.app_foreground);
        }else{
            Picasso.get().load(u.getUrl()).into(viewHolder._USER_IMAGE);
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get all data required
                String friend_id = u.getId();
                String friend_name = u.getUserName();
                String friend_URL = u.getUrl();
                //Create the invitation
                Intent intent = new Intent(mContext, InvitationActivity.class);
                intent.putExtra("friend_id", friend_id);
                intent.putExtra("friend_name", friend_name);
                intent.putExtra("friend_URL", friend_URL);
                //start new activity
                UserGlobals.isChallanger = true;
                UserGlobals.isInvited = true;
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    /**
     *
     */
    public class ViewHolder extends  RecyclerView.ViewHolder{
        public TextView _USER_NAME;
        public ImageView _USER_IMAGE;

        public ViewHolder(View v){
            super(v);
            _USER_NAME = itemView.findViewById(R.id.name_profile);
            _USER_IMAGE = itemView.findViewById(R.id.image_profile);
        }
    }
}
