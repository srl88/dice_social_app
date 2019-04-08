# Identification
**Group Pascaline - Mobile Liar's Dice**  
Philibert Adam - B00826597  
Sung Won Bhyun - B00687631  
Eric Lee - B00719123  
Seyd Lopez - B00751312  
Eric Nguyen - B00573038  

Git repository located at URL: https://git.cs.dal.ca/enguyen/csci4176-group-project.git

# Project Summary

Mobile Liar’s Dice is a digital application that adapts the Liar’s Dice game onto a mobile device platform 
as well as incorporates features and functionalities native to mobile devices to further enhance user 
experiences while playing the game. The application was designed with the intention of making use of camera 
functionalities, GPS tracking, and online connectivity. These functionalities were implemented ultimately 
with the intention of incorporating such device features in a way that would enhance the gameplay experience 
of the primary game element of Mobile Liar’s Dice.


## Libraries

* **com.google.firebase:firebase-core:16.0.7:**  Firebase library required to connect to Firebase
* **com.google.firebase:firebase-auth:16.1.0:** Firebase authentication library required to sign up, signin and signout. This library was used to check on the state of the user.
* **com.google.firebase:firebase-database:16.1.0:** Firebase library required for update, delete and update the Non-SQL database. 
* **com.google.firebase:firebase-storage:16.1.0:** Firebase library to store multimedia files such as images.
* **com.android.support:recyclerview-v7:28.0.0:** Library used to display data in a list with less memory.
* **de.hdodenhof:circleimageview:3.0.0:** Library used to display images as circles.
* **com.squareup.picasso:picasso:2.71828:** Library used to load stored image via URL



## Installation Notes

The user accounts for Mobile Liar’s Dice are built on Firebase, which is a service host that facilitates quick 
and simple data management. The database that was used for the user account management element of Mobile Liar’s 
Dice can be accessed at https://firebase.google.com using the following credentials:
  
Email: mobilecompproject2019@gmail.com  
Password: MobileComp@2  

Access to this database will enable developers to view or modify user account information. However, this should 
only be done on rare occasions, as it is more effective to modify a user’s own account information through their 
settings within the Mobile Liar’s Dice app itself.

New users should register and login before using the application using the login page of the application. 


## Code Examples
**Problem:** Event listeners for the database are managed by the OS and they outlive the application which is useful 
for notifications, however, our app required real time data for status changes (location or profile picture), but 
they were not required while the application was not being used. The problem was solved by removing the event listener 
using the OnDestroy method. This solution was also useful for the players waiting for an invitation, since the event 
listener will outlive the activity, if the same user invited the same person again, the integrity of the database would 
be compromised as different event listeners would read it and update it. Hence, removing the event listener once the 
activity is finished, fixed the issue.


```
   //Remove the event listeners once the user logs out or the app is shutdown!
    @Override
    public void onDestroy(){
        super.onDestroy();
        refUser.removeEventListener(userListener);
    }
```
*Note: There is no reference for this solution, it was based on experience.*

**Problem:** Allow the user to take pictures and send them without storing them locally. This was solved by taking the output 
from the camera as a strings of bits, instead of a URI. Hence, we simply update the database with the strings the bits and the 
image type.

```
    //Get the image as bitmap
    Bitmap bm = (Bitmap) data.getExtras().get("data");
    ByteArrayOutputStream st = new ByteArrayOutputStream();
    
    //Compress the image into bit string
    bm.compress(Bitmap.CompressFormat.JPEG, 100, st);
    byte[] imageByte = st.toByteArray();
    
    //save the image
    saveImage(null, imageByte);
```
*Note: There is no reference for this solution, it was based on experience.*

**Problem:** Make two players share same game data on firebase to make the real-time multiplayer to work. 
This was solved by player 1 creating a room data on firebase and sending the room ID to player 2 when 
sending the invitation. Player 2 will receive the room ID from player 1 from the invitation and the room 
with the same ID will be searched on the database. Once found, player2_id will be updated under 
SINGLEHANDROOM data. Furthermore, if both players were to be switched to the game activity at the same 
time, there can be a problem because if player 2 searches for the room with the same ID when the room 
is being created on the database, player 2 would not be able to find the room. This problem was solved 
by adding 1 second delay and the toast message saying "Waiting for room creation.." for player 2 to make 
sure player 2 searches for the room after the room creation.

```
    if(UserGlobals.isChallenger) {
        intent.putExtra("roomMaster", true);
        intent.putExtra("player_id", "player1_id");
        intent.putExtra("id_1", invitation.getId_1());
        intent.putExtra("id_2", invitation.getId_2());
        intent.putExtra("url_1", invitation.getUrl1());
        intent.putExtra("url_2", invitation.getUrl2());
        intent.putExtra("name_1", invitation.getName_1());
        intent.putExtra("name_2", invitation.getName_2());
        database = FirebaseDatabase.getInstance().getReference("SINGLEHANDROOMS");
        SingleHandRooms room = new SingleHandRooms(room_id, "player1_id", "", false, false, false, false, false, 0, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 1, 0, 0);
        database.child(room_id).setValue(room);
        intent.putExtra("room_id", room_id);
        // Prevent BidWindow from opening twice
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        endActivity2();
    } else {
        intent.putExtra("roomMaster", false);
        intent.putExtra("player_id", "player2_id");
        intent.putExtra("room_id", room_id);
        intent.putExtra("id_1", invitation.getId_1());
        intent.putExtra("id_2", invitation.getId_2());
        intent.putExtra("url_1", invitation.getUrl1());
        intent.putExtra("url_2", invitation.getUrl2());
        intent.putExtra("name_1", invitation.getName_1());
        intent.putExtra("name_2", invitation.getName_2());

        Toast.makeText(InvitationActivity.this, "Waiting for room creation..", Toast.LENGTH_SHORT).show();
        // Player 2 waits 1 second for room creation
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Prevent BidWindow from opening twice
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                endActivity2();
            }
        }, 1000);
    }
```
*Note: There is no reference for this solution, it was based on experience.*


## Feature Section

As mentioned before, the primary features of Mobile Liar’s Dice were built around three primary mobile device 
features: built-in cameras, GPS location tracking, and the availability of online connectivity. Use of the 
camera was incorporated through personalizable user account settings such as profile pictures and the option 
to send photos via the chat messaging system; GPS tracking was incorporated into the location tracking of 
other users with accounts on the application; and connectivity was utilized for establishing game sessions 
between players as well as the chat messaging system. 

In addition to the central feature designs, Mobile Liar’s Dice also makes use of artificial intelligence scripts 
for calculating probabilities during single-player mode. This inclusion was to ensure that users interacting with 
our app without the ability to access network connectivity of GPS tracking on their devices would also be able to 
enjoy an engaging user experience.


## Final Project Status

The final iteration of Mobile Liar’s Dice as of the completion of this document is a working application that has 
all of the minimum and expected functionalities we determined for its deliverability. The minimum requirements were 
determined as functions absolutely necessary for allowing users to be able to interact with the application through 
their mobile devices while also incorporating the desired mobile device capabilities. The expected functionalities 
were determined by any functionalities that would streamline all minimum functionalities by enabling greater 
interactive capabilities. Bonus functionalities were determined as those that would further enhance user experiences 
with the application’s fundamental design.

Due to the time constraints of the project as well as the emergence of additional resource requirements for the 
minimum and expected functionalities we had already committed to, we were not able to implement all of the bonus 
features that we listed. However, many of the bonus functionalities we identified depended on the completion of the 
minimum or expected functionalities that we had completed, which means that future works for this application would 
allow for all bonus functionalities to be readily addressed and completed.

#### Minimum Functionality
- User has their own hand of dice (Completed)
- User can view and login with their own profile (Completed)
- User can set a personal profile picture with a camera (Completed)
- Application utilizes GPS to locate and list other nearby players (Completed)
- User can view rules of the game (Completed)
- User can play and interact with a game session (Completed)

#### Expected Functionality
- User can play with other users (Completed)
- User can add other players to their friend’s list (Partially Completed)
- User can chat with other players (Completed)
- User can delete chat sessions (Completed)
- User can modify their profile picture using local storage on their mobile device (Completed)

#### Bonus Functionality
- User can roll the dice by shaking his/her phone (Partially Completed)
- User can see a 3D dice rolling (Partially Completed; only “false” 3D for single player mode only)
- User can chat during the gaming session (Completed)
- User can send pictures and take pictures during the chatting session (Completed)
- The application implements a balance AI for single player mode (Partially Completed)
- User can see a the global ranking from all the users (Not Implemented)
- User can select a thematic user interface layout (Not implemented)
- User can choose between single player and multiplayer (Completed)


## Contributions

**Philbert Adam** - Created and implemented the artificial intelligence scripts for single player mode; added more realistic feedback 
(false 3D dice roll, sound)  
**Sung Won Bhyun** - Developed the core game engine and mechanics; ensured interface for user experience playing the game synthesized 
with the overall application layout and design  
**Eric Lee** - Designed user interface and activity layouts; refactored pages on app to maintain consistent adherence to Nielsen’s 
heuristic goals  
**Seyd Lopez** - Established user accounts and data management with Firebase; set up camera integration functionalities and user 
interaction features such as the messaging system, profile update, messaging multimedia files,GPS location system, game invitation, 
login and logout.  
**Eric Nguyen** - Managed project specifications and Git repository; handled merging of application modules and documentation of 
project development


## Sources

“Liar’s dice”, En.wikipedia.org, 2019. [Online]. Available:
https://en.wikipedia.org/wiki/Liar%27s_dice

“How to Play Liar’s Dice”, wikihow.com, 2019. [Online]. Available:
https://www.wikihow.com/Play-Liar%27s-Dice

"Create Popup Menu in Android Studio", youtube.com, 2017. [Online]. Available:
https://www.youtube.com/watch?v=LXUDqGaToe0

"How To Create Pop Up Window In Android", youtube.com 2015. [Online]. Available:
https://www.youtube.com/watch?v=fn5OlqQuOCk

"How do I pass data between Activities in Android application?", stackoverflow.com, 2010. [Online]. Available:
https://stackoverflow.com/questions/2091465/how-do-i-pass-data-between-activities-in-android-application

"Working with Spinners in Android", studytonight.com, 2019. [Online]. Available:
https://www.studytonight.com/android/spinner-example-in-android