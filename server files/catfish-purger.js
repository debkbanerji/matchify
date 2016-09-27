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
var artistsPickRef = database.ref("artists-pick");
var tracksPickRef = database.ref("tracks-pick");


userFullRef.on('child_added', function (data) {
    console.log(/^((.*)Catfish(.*))$/.test(data.key));
    if (/^((.*)Catfish(.*))$/.test(data.key)) {
        userFullRef.child(data.key).remove();
        userFullRef.child(data.key).remove();
        userFullRef.child(data.key).remove();
    }
});