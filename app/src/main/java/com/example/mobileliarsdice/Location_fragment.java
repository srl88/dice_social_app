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

import com.example.mobileliarsdice.Models.UserInfo;
import com.example.mobileliarsdice.Models.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class Location_fragment extends Fragment {

    private RecyclerView recycleView;
    private UserAdapter userAdapter;

    //store all the users for ranking
    private List<Users> ranking;

    //store all the online users
    private List<Users> allUsers;
    //public Locations mLocation = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.location, container, false);

        recycleView = rootView.findViewById(R.id.recycleView);
        recycleView.setHasFixedSize(true);
        recycleView.setLayoutManager(new LinearLayoutManager(getContext()));

        ranking = new ArrayList<>();
        allUsers = new ArrayList<>();

        updateLocationView();

        return rootView;
    }


    private void updateLocationView(){
        DatabaseReference refUser = FireBaseGlobals.getDataBase().getReference("USERS");
        //ANY CHANGES ON THE USER_DATABASE WILL BE REFLECTED ON THE USER OBJECT
        refUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ranking.clear();
                allUsers.clear();
                for(DataSnapshot c: dataSnapshot.getChildren()){
                    Users temp = c.getValue(Users.class);
                    if(Main.mUser!=null && temp!=null){
                        if(!Main.mUser.getId().equals(temp.getId())&&temp.getOnline()){
                            //check location!
                            float[] results = new float[3];
                            Location.distanceBetween(Main.mUser.getLatitude(), Main.mUser.getLongitude(),
                                    temp.getLatitude(), temp.getLongitude(), results);
                            // if less than 100 km add it!
                            if(results[0]<=100000){
                                allUsers.add(temp);
                            }
                        }
                        ranking.add(temp);
                    }
                }

                userAdapter = new UserAdapter(getContext(), allUsers);
                recycleView.setAdapter(userAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // TODO:read documentation to see the type of error
            }
        });
    }

}
