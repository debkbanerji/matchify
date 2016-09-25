var firebase = require("firebase");

var config = {
    apiKey: "AIzaSyC533sLDJ8rDYEQldtwo0NafGd8RppKIco",
    authDomain: "matchify-e0338.firebaseapp.com",
    databaseURL: "https://matchify-e0338.firebaseio.com",
    storageBucket: "matchify-e0338.appspot.com",
    messagingSenderId: "72045147665"
};
firebase.initializeApp(config);

var database = firebase.database();
var artistsPickRef = database.ref("artists-pick");
var tracksPickRef = database.ref("tracks-pick");
var timestampRef = database.ref("analyzer-timestamp");


console.log("Analyzer running...");
console.log("Press CTRL + C to stop process");

var count = 0;
var maxcount = 100;
var interval = 1000;

var intervalObject = setInterval(function () {
    count++;
    console.log("Passes since analyzer started: ", count);

    // main analytic logic here:
    timestampRef.set(Date.now());

    artistsPickRef.off();


    // for top artists:
    artistsPickRef.on('child_added', function (data) {
        // console.log(data.key);
        var user = data.key;
        var userArtistsPickRef = artistsPickRef.child(user);
        userArtistsPickRef.on('value', function (snapshot) {
            var artistPairs = [];
            var artistPickList = snapshot.forEach(function (childSnapshot) {
                // key will be "fred" the first time and "barney" the second time
                var key = childSnapshot.key;
                // childData will be the actual contents of the child
                var childData = childSnapshot.val();
                artistPairs.push({"artist": key, "count": childData});
            });

            function compare(a, b) {
                if (a.count < b.count)
                    return 1;
                if (a.count > b.count)
                    return -1;
                return 0;
            }

            artistPairs.sort(compare);

            var topArtists = [];

            for (var i = 0; i < artistPairs.length && i < 5; i++) {
                topArtists[i] = artistPairs[i]["artist"];
            }

            var userTopArtistsRef = database.ref("user-full/" + user + "/top-artists");
            userTopArtistsRef.set(topArtists);
        });

    });





    tracksPickRef.off();


    // for top artists:
    tracksPickRef.on('child_added', function (data) {
        // console.log(data.key);
        var user = data.key;
        var userTracksPickRef = tracksPickRef.child(user);
        userTracksPickRef.on('value', function (snapshot) {
            var trackPairs = [];
            var trackPickList = snapshot.forEach(function (childSnapshot) {
                // key will be "fred" the first time and "barney" the second time
                var key = childSnapshot.key;
                // childData will be the actual contents of the child
                var childData = childSnapshot.val();
                trackPairs.push({"track": key, "count": childData});
            });

            function compare(a, b) {
                if (a.count < b.count)
                    return 1;
                if (a.count > b.count)
                    return -1;
                return 0;
            }

            trackPairs.sort(compare);

            // console.log(trackPairs);

            var topTracks = [];

            for (var i = 0; i < trackPairs.length && i < 5; i++) {
                topTracks[i] = trackPairs[i]["track"];
            }
            // console.log(topTracks);

            var userTopArtistsRef = database.ref("user-full/" + user + "/top-tracks");
            userTopArtistsRef.set(topTracks);
        });

    });


    // if (count == maxcount) {
    //     console.log('exiting');
    //     clearInterval(intervalObject);
    // }
}, interval);