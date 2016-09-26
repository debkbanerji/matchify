package com.example.matchify;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;

public class MatchActivity extends AppCompatActivity {

    ArrayAdapter<MatchableUser> arrayAdapter;
    //    ArrayList<String> userStringList;
    ArrayList<MatchableUser> userList;
    SwipeFlingAdapterView flingContainer;

    List<String> curTopTracks;
    List<String> curTopArtists;

    String currID;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference userFullRef = database.getReference("user-full");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        ActionBar actionBar = getSupportActionBar();

        actionBar.setTitle(Html.fromHtml("<font color='#11111'>Matchify</font>"));

        setContentView(R.layout.activity_match);

        //add the view via xml or programmatically
        flingContainer = (SwipeFlingAdapterView) findViewById(R.id.frame);

        userList = new ArrayList<MatchableUser>();
        currID = getIntent().getExtras().getString("userId");

        List<String> dummyTopTracks = new LinkedList<>();
        List<String> dummyTopArtists = new LinkedList<>();
        MatchableUser dummyUser = new MatchableUser("name", "email", "phone", dummyTopArtists, dummyTopTracks);
        userList.add(dummyUser);
//        userStringList = new ArrayList<String>();
//        userStringList.add("Swipe to begin matching!");

        // Retrieve list of target users

        final ValueEventListener userFullRefListener = userFullRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (userStringList.get(0).equals("Retrieving users...")) {
//                    userStringList.remove(0);
//                    userList.remove(0);
//                    arrayAdapter.notifyDataSetChanged();
//                }

                Map<String, Object> allUsersMap = (Map<String, Object>) dataSnapshot.getValue();
                for (Map.Entry<String, Object> entry : allUsersMap.entrySet()) {
                    if (!entry.getKey().equals(currID)) {
                        Map<String, Object> userMap = (Map<String, Object>) entry.getValue();
//                        Log.e("USER DOWNLOADED", userMap.toString());
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
                    } else {
                        Map<String, Object> userMap = (Map<String, Object>) entry.getValue();
                        curTopTracks = (List<String>) userMap.get("top-tracks");
                        curTopArtists = (List<String>) userMap.get("top-artists");
                    }
                }

                sortUserList();
//                Log.e("curtoptracks", curTopTracks.toString());

                List<String> dummyTopTracks = new LinkedList<>();
                List<String> dummyTopArtists = new LinkedList<>();
                MatchableUser endDummyUser = new MatchableUser("end", "email", "phone", dummyTopArtists, dummyTopTracks);
                userList.add(userList.size(), endDummyUser);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


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
                userFullRef.removeEventListener(userFullRefListener);
                arrayAdapter.notifyDataSetChanged();

            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                //Do something on the left!
                //You also have access to the original object.
                //If you want to use it just cast it (String) dataObject
//                Toast.makeText(MatchActivity.this, "Left!", Toast.LENGTH_SHORT).show();

                // Do nothing - maybe implement negative scoring in the future?
            }

            @Override
            public void onRightCardExit(Object dataObject) {
//                Toast.makeText(MatchActivity.this, "Right!", Toast.LENGTH_SHORT).show();
                MatchableUser matchedUser = (MatchableUser) dataObject;

                if (matchedUser.getName().equals("name") || matchedUser.getName().equals("end")) {
                    return;
                }

//                Log.e("swipe","right");

                DatabaseReference tracksPickRef = database.getReference("tracks-pick/" + currID);
                for (String track : matchedUser.getTopTracks()) {
                    DatabaseReference trackRef = tracksPickRef.child(track);
                    trackRef.runTransaction(new Transaction.Handler() {
                        @Override
                        public Transaction.Result doTransaction(MutableData currentData) {
                            if (currentData.getValue() == null) {
                                currentData.setValue(Integer.valueOf(1));
                            } else {
                                Integer result = (Integer.parseInt(currentData.getValue().toString()));
                                result = new Integer(result.intValue() + 1);
//                                Log.e("tracks-pick", result.toString());
                                currentData.setValue(result);
                            }
                            return Transaction.success(currentData);
                        }

                        @Override
                        public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                        }
                    });
                }

                DatabaseReference artistsPickRef = database.getReference("artists-pick/" + currID);
                for (String artist : matchedUser.getTopArtists()) {
                    DatabaseReference artistRef = artistsPickRef.child(artist);
                    artistRef.runTransaction(new Transaction.Handler() {
                        @Override
                        public Transaction.Result doTransaction(MutableData currentData) {
                            if (currentData.getValue() == null) {
                                currentData.setValue(Integer.valueOf(1));
                            } else {
                                Integer result = (Integer.parseInt(currentData.getValue().toString()));
                                result = new Integer(result.intValue() + 1);
//                                Log.e("artists-pick", result.toString());
                                currentData.setValue(result);
                            }
                            return Transaction.success(currentData);
                        }

                        @Override
                        public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                        }
                    });
                }

                Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                smsIntent.putExtra("address", matchedUser.getPhoneNumber());
                smsIntent.setData(Uri.parse("sms:"));
                smsIntent.putExtra("sms_body", "Hi, " + matchedUser.getName() + ", I found you through Matchify's mind bogglingly awesome algorithm!");
                startActivity(smsIntent);
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
//            Log.e("SORTING", "SORTING");
            Collections.sort(userList, new Comparator<MatchableUser>() {
                @Override
                public int compare(MatchableUser lhs, MatchableUser rhs) {

                    if (lhs.getName().equals("name")) {
                        return Integer.MIN_VALUE;
                    }

                    if (rhs.getName().equals("name")) {
                        return Integer.MAX_VALUE;
                    }
                    // To deal with sentinel element


                    int lhsResult = 0;
                    int rhsResult = 0;

                    for (String a : curTopArtists) {
                        for (String lhsa : lhs.getTopArtists()) {
                            if (a.equals(lhsa)) {
                                lhsResult++;
                            } else {
                                lhsResult--;
                            }
                        }

                        for (String rhsa : rhs.getTopArtists()) {
                            if (a.equals(rhsa)) {
                                rhsResult++;
                            } else {
                                rhsResult--;
                            }
                        }
                    }

                    for (String t : curTopTracks) {
                        for (String lhst : lhs.getTopArtists()) {
                            if (t.equals(lhst)) {
                                lhsResult += 3;
                            }
                        }

                        for (String rhst : rhs.getTopArtists()) {
                            if (t.equals(rhst)) {
                                rhsResult += 3;
                            }
                        }
                    }

//                    Log.d("LHSRESULT", Integer.toString(lhsResult) + ", " + lhs.toString());
//                    Log.d("RHSRESULT", Integer.toString(rhsResult) + ", " + rhs.toString());

                    return rhsResult - lhsResult;
                }
            });
        }
    }
}
