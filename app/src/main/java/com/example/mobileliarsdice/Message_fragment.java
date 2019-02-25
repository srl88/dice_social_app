package com.example.mobileliarsdice;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mobileliarsdice.Models.Chats;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Message_fragment extends Fragment {

    private RecyclerView recycleView;
    private ChatAdapter chatAdapter;

    private List<Chats> mChats;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.messages, container, false);

        //set the view
        recycleView = rootView.findViewById(R.id.recycleView);
        recycleView.setHasFixedSize(true);
        recycleView.setLayoutManager(new LinearLayoutManager(getContext()));

        // get all chats
        mChats = new ArrayList<>();
        getAllChats();

        return rootView;
    }


    private void getAllChats(){
        String _id = FireBaseGlobals.getUser().getUid();
        DatabaseReference refUser = FireBaseGlobals.getDataBase().getReference("CHATS").child(_id);
        //ANY CHANGES ON THE USER_DATABASE WILL BE REFLECTED ON THE USER OBJECT
        refUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChats.clear();
                for(DataSnapshot c: dataSnapshot.getChildren()) {
                    Chats temp = c.getValue(Chats.class);
                    if (Main.mUser != null && temp != null) {
                        mChats.add(temp);
                    }
                }
                chatAdapter = new ChatAdapter(getContext(), mChats);
                recycleView.setAdapter(chatAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // TODO:read documentation to see the type of error
            }
        });
    }
}
