package com.example.mobileliarsdice;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mobileliarsdice.Models.Chats;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private Context mContext;
    private List<Chats> chats;

    public ChatAdapter(Context c, List<Chats> ch){
        this.mContext = c;
        this.chats = ch;
    }

    /**
     *
     * @param viewGroup
     * @param i
     * @return
     */
    @NonNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.user_item, viewGroup, false);
        return new ChatAdapter.ViewHolder(v);
    }


    /**
     *
     * @param viewHolder
     * @param i
     */
    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ViewHolder viewHolder, int i) {
        Chats u = chats.get(i);
        viewHolder._USER_NAME.setText(u.getUserName());

        //set image
        if(u.getUrl().equals("NONE")){
            viewHolder._USER_IMAGE.setImageResource(R.mipmap.app_foreground);
        }else{
            Picasso.get().load(u.getUrl()).into(viewHolder._USER_IMAGE);
        }
        //set new message
        if(u.getNewChat()){
            //viewHolder._USER_IMAGE.
            viewHolder._NEW_MESSAGE.setVisibility(View.VISIBLE);
        }
        else{
            viewHolder._NEW_MESSAGE.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    /**
     *
     */
    public class ViewHolder extends  RecyclerView.ViewHolder{
        public TextView _NEW_MESSAGE;
        public TextView _USER_NAME;
        public ImageView _USER_IMAGE;

        public ViewHolder(View v){
            super(v);
            _USER_NAME = itemView.findViewById(R.id.name_profile);
            _USER_IMAGE = itemView.findViewById(R.id.image_profile);
            _NEW_MESSAGE = itemView.findViewById(R.id.lblNew);
        }
    }
}
