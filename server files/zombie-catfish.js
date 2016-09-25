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
var userFullRef = database.ref("user-full");

// userFullRef.set("TEST");


var numUsers = 5;

var adjectives = ["popular", "epic", "funky", "happy", "wonderful", "confident", "dazzling", "dynamic", "jolly", "fearless", "ridiculous", "radical", "obvious"];
var artists = ["Jesse McCartney", "Mandy Moore", "Sara Bareilles", "Ingrid Michaelson", "Michael Buble", "Chantal Kreviazuk", "Jonas Brothers", "Lifehouse", "James Blunt", "Rick Astley", "Ed Sheeran"];
var tracks = ["Beautiful Soul", "Bleeding Love", "Bottle It Up", "Breathe Again", "Can't Help Falling in Love", "Celebrate", "Close Your Eyes", "Drink You Gone", "Everything", "Feels Like Home", "Eye Of the Tiger", "Fix You", "Gravity", "Give Me Love", "Girls Chase Boys", "Gardenia", "Hanging by a Moment", "Haven't Met You Yet", "I See The Light", "Invincible", "King of Anything", "Keep Singing", "Last Christmas", "Looking Forward to Looking Back", "Merrimack River", "Never Gonna Give You Up", "You and I", "You're Beautiful", "Time Machine", "The Scientist"];

function shuffle(a) {
    var j, x, i;
    for (i = a.length; i; i--) {
        j = Math.floor(Math.random() * i);
        x = a[i - 1];
        a[i - 1] = a[j];
        a[j] = x;
    }
}

console.log(tracks.length);
for (var i = 0; i < numUsers; i++) {
    // Create zombie user and add to database

    var username = adjectives[Math.floor((Math.random() * adjectives.length))] +
        "Catfish" + (Math.floor((Math.random() * 900)) + 100);
    console.log(username);

    var email = username.toLowerCase() + "@doesntexist.com";

    var phoneNumber = "Fake Number LOL";

    var topArtists = [];

    shuffle(artists);
    for (var j = 0; j < 5; j++) {
        topArtists[j] = artists[j];
    }

    var topTracks = [];

    shuffle(tracks);
    for (var k = 0; k < 5; k++) {
        topTracks[k] = tracks[k];
    }

    var user = {
        "email": email,
        "phone-number": phoneNumber,
        "top-artists": topArtists,
        "top-tracks": topTracks
    };

    var userRef = userFullRef.child(username);
    userRef.set(user);
    console.log(user);
}
// process.exit();