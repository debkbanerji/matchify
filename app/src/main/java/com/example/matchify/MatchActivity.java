package com.example.matchify;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MatchActivity extends AppCompatActivity {

    ArrayAdapter<MatchableUser> arrayAdapter;
    //    ArrayList<String> userStringList;
    ArrayList<MatchableUser> userList;
    SwipeFlingAdapterView flingContainer;

    List<String> curTopTracks;
    List<String> curTopArtists;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference userFullRef = database.getReference("user-full");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);

        //add the view via xml or programmatically
        flingContainer = (SwipeFlingAdapterView) findViewById(R.id.frame);

        userList = new ArrayList<MatchableUser>();


        List<String> dummyTopTracks = new LinkedList<>();
        List<String> dummyTopArtists = new LinkedList<>();
        MatchableUser dummyUser = new MatchableUser("name", "email", "phone", dummyTopArtists, dummyTopTracks);
        userList.add(dummyUser);
//        userStringList = new ArrayList<String>();
//        userStringList.add("Swipe to begin matching!");

        // Retrieve list of target users

        userFullRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (userStringList.get(0).equals("Retrieving users...")) {
//                    userStringList.remove(0);
//                    userList.remove(0);
//                    arrayAdapter.notifyDataSetChanged();
//                }

                Map<String, Object> allUsersMap = (Map<String, Object>) dataSnapshot.getValue();
                for (Map.Entry<String, Object> entry : allUsersMap.entrySet()) {
                    Map<String, Object> userMap = (Map<String, Object>) entry.getValue();
                    Log.e("USER DOWNLOADED", userMap.toString());
                    String username = entry.getKey();
                    String email = (String) userMap.get("email");
                    String phoneNumber = (String) userMap.get("phone-number");
                    if (phoneNumber == null) {
                        phoneNumber = "Phone number not available";
                    }
                    List<String> topTracks = (List<String>) userMap.get("top-tracks");
                    List<String> topArtists = (List<String>) userMap.get("top-artists");

                    MatchableUser matchableUser = new MatchableUser(username, email, phoneNumber, topArtists, topTracks);
                    userList.add(matchableUser);
//                    userStringList.add(matchableUser.toString());
                }


//                Log.e("userStringList", userStringList.toString());
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

//        userFullRef.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                Map<String, Object> userMap = (Map<String, Object>) dataSnapshot.getValue();
//                Log.e("USER DOWNLOADED", userMap.toString());
//                String username = dataSnapshot.getKey();
//                String email = (String) userMap.get("email");
//                String phoneNumber = (String) userMap.get("phone-number");
//                if (phoneNumber == null) {
//                    phoneNumber = "Phone number not available";
//                }
//                List<String> topTracks = (List<String>) userMap.get("top-tracks");
//                List<String> topArtists = (List<String>) userMap.get("top-artists");
//
//                MatchableUser matchableUser = new MatchableUser(username, email, phoneNumber, topArtists, topTracks);
//                userList.add(matchableUser);
//                userStringList.add(matchableUser.toString());
//
//                if (userStringList.get(0).equals("Retrieving users...")) {
//                    userStringList.remove(0);
//                }
//
//                arrayAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

        //choose your favorite adapter
        arrayAdapter = new ArrayAdapter<MatchableUser>(this, R.layout.item, R.id.helloText, userList);

        //set the listener and the adapter
        flingContainer.setAdapter(arrayAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!");
//                userStringList.remove(0);
                userList.remove(0);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                //Do something on the left!
                //You also have access to the original object.
                //If you want to use it just cast it (String) dataObject
                Toast.makeText(MatchActivity.this, "Left!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                Toast.makeText(MatchActivity.this, "Right!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                // Ask for more data here
//                al.add("XML ".concat(String.valueOf(i)));
//                arrayAdapter.notifyDataSetChanged();
//                Log.d("LIST", "notified");
//                i++;
                if (itemsInAdapter == 0) {
                    Intent playIntent = new Intent(MatchActivity.this, HomeActivity.class);
                    startActivity(playIntent);
                }
            }

            @Override
            public void onScroll(float v) {

            }
        });

        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
//                Toast.makeText(getApplicationContext(), "Clicked",
//                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sortUserList() {
        if (userList != null && userList.size() > 0
                && curTopArtists != null && curTopArtists.size() > 0
                && curTopTracks != null && curTopTracks.size() > 0) {
            // sort user list


        }
    }
}
