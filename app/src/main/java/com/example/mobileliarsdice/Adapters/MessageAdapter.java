package com.example.mobileliarsdice.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mobileliarsdice.Models.Messages;
import com.example.mobileliarsdice.R;
import com.example.mobileliarsdice.UserGlobals;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    //TO DETERMINE THE VIEW
    static final int MSG_RIGHT = 0, MSG_LEFT=1;

    private Context mContext;
    private List<Messages> msg;
    private String mImage;
    private String fImage;

    public MessageAdapter(Context c, List<Messages> u, String mImage, String fImage) {
        this.mContext = c;
        this.msg = u;
        this.mImage = mImage;
        this.fImage = fImage;
    }

    /**
     * @param viewGroup
     * @param i
     * @return
     */
    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v;
        if(i==MSG_RIGHT){
            v =LayoutInflater.from(mContext).inflate(R.layout.message_item_right, viewGroup, false);
        }
        else{
            v =LayoutInflater.from(mContext).inflate(R.layout.message_item_left, viewGroup, false);
        }
        return new MessageAdapter.ViewHolder(v);
    }

    /**
     * @param viewHolder
     * @param i
     */
    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder viewHolder, int i) {
        Messages m = msg.get(i);

        viewHolder._TEXT.setText(m.getText());

        if(m.getSender_id().equals(UserGlobals.mUser.getId())){
            setImage(mImage, viewHolder);
        }else{
            setImage(fImage, viewHolder);
        }


    }

    private void setImage(String url, @NonNull MessageAdapter.ViewHolder viewHolder){
        //set image
        if(url.equals("NONE")){
            viewHolder._USER_IMAGE.setImageResource(R.mipmap.app_foreground);
        }else{
            Picasso.get().load(url).into(viewHolder._USER_IMAGE);
        }
    }

    @Override
    public int getItemCount() {
        return msg.size();
    }

    /**
     *
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView _TEXT;
        public ImageView _USER_IMAGE;

        public ViewHolder(View v) {
            super(v);
            _TEXT = itemView.findViewById(R.id.text_msg);
            _USER_IMAGE = itemView.findViewById(R.id.profile_picture);
        }
    }

    @Override
    public int getItemViewType(int position){
        if(msg.get(position).getSender_id().equals(UserGlobals.mUser.getId())){
            return MSG_RIGHT;
        }else{
            return MSG_LEFT;
        }
    }
}