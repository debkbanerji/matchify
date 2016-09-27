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

var adjectives = ["agreeable", "alert", "alluring", "ambitious", "amused", "boundless", "brave", "bright", "calm", "capable", "charming", "cheerful", "coherent", "comfortable", "confident", "cooperative", "courageous", "credible", "cultured", "dashing", "dazzling", "debonair", "decisive", "decorous", "delightful", "detailed", "determined", "diligent", "discreet", "dynamic", "eager", "efficient", "elated", "eminent", "enchanting", "encouraging", "endurable", "energetic", "entertaining", "enthusiastic", "excellent", "excited", "exclusive", "exuberant", "fabulous", "fair", "faithful", "fantastic", "fearless", "fine", "frank", "friendly", "funny", "generous", "gentle", "glorious", "good", "happy", "harmonious", "helpful", "hilarious", "honorable", "impartial", "industrious", "instinctive", "jolly", "joyous", "kind", "kind-hearted", "knowledgeable", "level", "likeable", "lively", "lovely", "loving", "lucky", "mature", "modern", "nice", "obedient", "painstaking", "peaceful", "perfect", "placid", "plausible", "pleasant", "plucky", "productive", "protective", "proud", "punctual", "quiet", "receptive", "reflective", "relieved", "resolute", "responsible", "rhetorical", "righteous", "romantic", "sedate", "seemly", "selective", "self-assured", "sensitive", "shrewd", "silly", "sincere", "skillful", "smiling", "splendid", "steadfast", "stimulating", "successful", "succinct", "talented", "thoughtful", "thrifty", "tough", "trustworthy", "unbiased", "unusual", "upbeat", "vigorous", "vivacious", "warm", "willing", "wise", "witty", "wonderful"];
var artists = ["Jesse McCartney", "Mandy Moore", "Sara Bareilles", "Ingrid Michaelson", "Michael Buble", "Chantal Kreviazuk", "Jonas Brothers", "Lifehouse", "James Blunt", "Rick Astley", "Ed Sheeran"];
var tracks = ["A Bridge over You", "Afterlife", "Ain't No Mountain High Enough", "All I Can Do", "All Of Me", "All Star", "American Pie", "Angel", "Angels On My Side", "Another Life", "Basic Space", "Be OK", "Beautiful Soul", "Before", "Billie Jean", "Birds and Bees", "Bleeding Love", "Blind", "Bloodstream", "Blowin' in the Wind", "Bluebird", "Bohemian Rhapsody", "Bottle It Up", "Brave", "Breathe Again", "Bridge over Troubled Water", "Broken", "Budapest", "Burnin' Up", "Bye Bye Bye", "Call Me Beep Me", "Can't Fight the Moonlight", "Can't Help Falling in Love", "Can't You Just Adore Her", "Careless Whisper", "Cecilia", "Celebrate", "Chances", "Chasing Cars", "City", "Close Your Eyes", "Colours Of The Wind", "Come and Get Your Love", "Come Home", "Could've Been Watching You", "Crave You", "Crazy Love", "Crystalised", "Crystals", "Dirty Paws", "Don't", "Don't Stop Believin'", "Drink You Gone", "Drops of Jupiter", "Everblue", "Everybody", "Everybody's Got Somebody But Me", "Everything", "Eye Of the Tiger", "Faithfully", "Falling Into Place", "Falling Slowly", "Feels Like Home", "Few Days Down", "Fight Song", "Five Hundred Miles", "Fix You", "For the First Time in Forever", "FourFiveSeconds", "Gardenia", "Gimme Something Good", "Girls Chase Boys", "Give Me Love", "Gonna Get Over You", "Gravity", "Greatest Change", "Green Apples", "Hallelujah", "Hanging by a Moment", "Hard Sail", "Haven't Met You Yet", "Heart of Blue", "Hello", "Here Comes the Sun", "Hey Jude", "Hey, Soul Sister", "Home", "Homeward Bound", "Hotel California", "How Do I Live", "How To Save A Life", "Human", "I Believe I Can Fly", "I Can't Make You Love Me", "I Choose You", "I Don't Know, Baby", "I Don't Want to Change You", "I Don't Want to Miss a Thing", "I Remember Her", "I See Fire", "I See The Light", "I Want It That Way", "I Want to Know What Love Is", "I Will Always Love You", "I Will Be", "I Will Remember You", "I Won't Give Up", "I Write Sins Not Tragedies", "If I Ain't Got You", "I'll Make a Man Out of You", "I'm Yours", "Intro", "Invincible", "Iridescent", "Iris", "It Ends Tonight", "It's a Beautiful Day", "Jamaica Farewell", "Just Say Yes", "Just the Way You Are", "Keep Singing", "King of Anything", "Kiss the Girl", "Ladies' Choice", "Last Christmas", "Latest Mistake", "Lay Me Down", "Leave Out All The Rest", "Leaving on a Jet Plane", "Lego House", "Let It Be", "Let It Go", "Life After You", "Light in the Dark ", "Light Me Up", "Listen to the Man", "Little Talks", "Little Wonders", "Littleroot Town", "Livin' La Vida Loca", "Livin' On A Prayer", "Locked Up", "Looking Forward to Looking Back", "Lost", "Love Me Like the World Is Ending", "Love Song", "Lucky", "Manhattan", "Marry You", "Merrimack River", "Miss America", "Miss You", "Morningside", "Never Gonna Give You Up", "New Soul", "No Surprise", "Nothing Everything", "Nothing That You Are", "Old Days", "One Call Away", "One Love", "One Night Town", "One Sweet Love", "Only the Good Die Young", "Paradise Awaits", "Photograph", "Pocket Philosopher", "Pray With Me", "Relax", "Riptide", "Rise from the Underworld", "Rolling in the Deep", "S.O.S", "Say Something", "Secrets", "Service and Sacrifice", "Set Fire To The Rain", "Shake It Out", "She Used To Be Mine", "She Will Be Loved", "Shelter", "Short Change Hero", "Shut Up and Dance", "Skyfall", "Slice", "Slow", "Slummin' in Paradise", "So Close", "Someone Like You", "Something That I Want ", "Somewhere Only We Know", "Son Of Man", "Spaceman", "Stand by Me", "Stay", "Stay With Me", "Stick", "Still The One", "Stop And Stare", "Strangers Like Me", "Sugar", "Sunday", "Sunset", "Supermassive Black Hole", "Sweet Serendipity", "Sweet Victory", "Swept Away", "Take Me Away", "Take Me To Church", "Take My Breath Away", "Tell Her About It", "Tenerife Sea", "Tha Mo Ghaol Air ird A' Chuain", "That's How You Know", "The A Team", "The Ballad Of Mona Lisa", "The Circle of Life", "The End", "The Heady Feeling of Freedom", "The Last Goodbye", "The Light", "The Longest Time", "The Scientist", "The Show", "The Sound of Silence", "The Strawberry Blonde", "The Tides of Destiny", "The Way I Am", "The Whole Of The Moon", "Thinking Out Loud", "This Is Gospel", "This Year", "Thnks fr th Mmrs", "Time", "Time in a Bottle", "Time Machine", "Time Travel", "To Love Somebody", "Truly Madly Deeply", "Uncharted", "Uptown Funk", "Uptown Girl", "VCR", "Viva La Vida", "Waiting for Superman", "Wanted Dead Or Alive", "Warpath", "We Didn't Start the Fire", "What Kind of Pokemon Are You", "When I Was Your Man", "When She Loved Me", "When Will My Life Begin", "Whole Lot Of Heart", "Why Can't We Be Friends", "Why Don't You Kiss Her", "Winter Song", "Wonderful Unknown", "Won't Go Home Without You", "Writing's On The Wall", "Yellow", "Yesterday", "You and I", "You and Me", "You Found Me", "You Give Love a Bad Name", "You Got Me", "You Know My Name", "You Raise Me Up", "You'll Be In My Heart", "You're Beautiful"];
function shuffle(a) {
    var j, x, i;
    for (i = a.length; i; i--) {
        j = Math.floor(Math.random() * i);
        x = a[i - 1];
        a[i - 1] = a[j];
        a[j] = x;
    }
}

shuffle(adjectives);

console.log(tracks.length);
for (var i = 0; i < numUsers; i++) {
    // Create zombie user and add to database

    var username = adjectives[Math.floor((i % adjectives.length))] +
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