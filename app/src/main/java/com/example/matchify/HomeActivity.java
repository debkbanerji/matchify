package com.example.matchify;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;

import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.UserPrivate;
import retrofit.Callback;
import retrofit.RetrofitError;

public class HomeActivity extends AppCompatActivity implements
        PlayerNotificationCallback, ConnectionStateCallback {

    Button matchButton;
    Button settingsButton;


    // TODO: Replace with your client ID
    private static final String CLIENT_ID = "d8ec9b6eb1e64b10bc2d2d081bb06625";
    // TODO: Replace with your redirect URI
    private static final String REDIRECT_URI = "matchify://callback";
    private static final int REQUEST_CODE = 1337;


    TelephonyManager tm;
    //    Player mPlayer;
    SpotifyApi api;
    static UserPrivate me;

    private static final String[] INITIAL_PERMS = {
            Manifest.permission.READ_SMS, Manifest.permission.READ_PHONE_STATE
    };
    private static final int INITIAL_REQUEST = 1337;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference userFullRef = database.getReference("user-full");
    DatabaseReference userProfileRef = database.getReference("user-profile");
    DatabaseReference currentUserRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Requesting permissions
        if (Build.VERSION.SDK_INT >= 23 && (PackageManager.PERMISSION_GRANTED != checkSelfPermission(Manifest.permission.READ_SMS) ||
                PackageManager.PERMISSION_GRANTED != checkSelfPermission(Manifest.permission.READ_PHONE_STATE))) {
            requestPermissions(INITIAL_PERMS, INITIAL_REQUEST);
        }

        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);


        matchButton = (Button) findViewById(R.id.beginMatching);
        settingsButton = (Button) findViewById(R.id.settingsButton);

        matchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent playIntent = new Intent(HomeActivity.this, MatchActivity.class);
                startActivity(playIntent);
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent playIntent = new Intent(HomeActivity.this, SettingsActivity.class);
                startActivity(playIntent);
            }
        });

        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID,
                AuthenticationResponse.Type.TOKEN,
                REDIRECT_URI);

        builder.setScopes(new String[]{"user-read-private", "user-read-email", "streaming", "user-top-read"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);


        AuthenticationResponse authenticationResponse = null;
        // Check if result comes from the correct activity
//        if (requestCode == REQUEST_CODE) {
        authenticationResponse = AuthenticationClient.getResponse(resultCode, intent);
//        }
        Log.d("REQUEST_CODE", Integer.toString(requestCode));
        assert (authenticationResponse != null);


        api = new SpotifyApi();
        api.setAccessToken(authenticationResponse.getAccessToken());
        SpotifyService spotify = api.getService();


//        mTextView.setText(authenticationResponse.getAccessToken());

//        spotify.getAlbum("2dIGnmEIy1WZIcZCFSj6i8", new Callback<Album>() {
//            @Override
//            public void success(Album album, retrofit.client.Response response) {
//                Log.d("Album success", album.name);
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//                Log.d("Album failure", error.toString());
//            }
//        });
        me = null;
        spotify.getMe(new Callback<UserPrivate>() {
            @Override
            public void success(UserPrivate userPrivate, retrofit.client.Response response) {
                Log.d("GetMe", "SUCCESS");
                me = userPrivate;
                Log.d("Spotify_api_me", me.toString());
                Log.d("Spotify_api_id", me.id);
                Log.d("Spotify_api_email", me.email);

                currentUserRef = userFullRef.child(userPrivate.id);


                final DatabaseReference emailRef = currentUserRef.child("email");
                emailRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() == null)
                            emailRef.setValue(me.email);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                final DatabaseReference phoneRef = currentUserRef.child("phone-number");
                phoneRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        Log.e("PHONEVALUE", dataSnapshot.getValue().toString());
                        if (dataSnapshot.getValue() == null || dataSnapshot.getValue().equals("Number Not Available"))
                            try {
                                String number = tm.getLine1Number();
                                if (number == null) {
                                    number = "Number Not Available";
                                }
                                currentUserRef.child("phone-number").setValue(number);
                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), "Unable to retrieve phone number. Other users will not be able to access your phone number",
                                        Toast.LENGTH_LONG).show();
                            }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("GetMe", "FAILURE");

            }
        });

        spotify.getTopArtists(new Callback<Pager<Artist>>() {
            @Override
            public void success(Pager<Artist> artistPager, retrofit.client.Response response) {
                final List artistList = artistPager.items;
                while (artistList.size() > 5) {
                    artistList.remove(artistList.size() - 1);
                }
                for (int i = 0; i < artistList.size(); i++) {
                    Object a = artistList.get(i);
                    artistList.set(i, ((Artist) a).name);
                    Log.d("Artist", a.toString());
                }
//                matchButton.setText(artistList.toString());
                final DatabaseReference topArtistsRef = currentUserRef.child("top-artists");
                topArtistsRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() == null) {
                            topArtistsRef.setValue(artistList);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("artistPager", "FAILURE");

            }
        });

        spotify.getTopTracks(new Callback<Pager<Track>>() {
            @Override
            public void success(Pager<Track> trackPager, retrofit.client.Response response) {
                final List trackList = trackPager.items;
                while (trackList.size() > 5) {
                    trackList.remove(trackList.size() - 1);
                }
                for (int i = 0; i < trackList.size(); i++) {
                    Object a = trackList.get(i);
                    trackList.set(i, ((Track) a).name);
                    Log.d("Track", a.toString());
                }
//                matchButton.setText(artistList.toString());
                final DatabaseReference topTracksRef = currentUserRef.child("top-tracks");
                topTracksRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() == null) {
                            topTracksRef.setValue(trackList);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("trackPager", "FAILURE");

            }
        });

    }

    @Override
    public void onLoggedIn() {
        Log.d("MainActivity", "User logged in");
    }

    @Override
    public void onLoggedOut() {
        Log.d("MainActivity", "User logged out");
    }

    @Override
    public void onLoginFailed(Throwable error) {
        Log.d("MainActivity", "Login failed");
    }

    @Override
    public void onTemporaryError() {
        Log.d("MainActivity", "Temporary error occurred");
    }

    @Override
    public void onConnectionMessage(String message) {
        Log.d("MainActivity", "Received connection message: " + message);
    }

    @Override
    public void onPlaybackEvent(EventType eventType, PlayerState playerState) {
        Log.d("MainActivity", "Playback event received: " + eventType.name());
    }

    @Override
    public void onPlaybackError(ErrorType errorType, String errorDetails) {
        Log.d("MainActivity", "Playback error received: " + errorType.name());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}