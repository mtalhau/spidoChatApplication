# spidoChatApplication
Chat Application made for APPCON 2019

By Team "Men On a mission"

M. Talha Usman (u2017328@giki.edu.pk)

M. Hamza (u2017261@giki.edu.pk)

Azib Zahid (u2017092@giki.edu.pk)

Setting Up the backend:

Backend is deployed on Parse server hosted by back4app.com. Create a java class StaterApplication.java and copy the code provided. Create a new app on back4app.com and replace the app id & client key in the code with your Back4app's app id and client key available in the "API Reference" section on the website. Use "https://parseapi.back4app.com/" as the server. Also in android manifest, declare android:name=".StarterApplication" under <aplication tag.

Setting Up the frontend:

All java and xml files are provided in the repository. The app is connected to Google Firebase, Parse Server and OneSignal (for notifications). So the following dependencies need to be added in App level build.gradle:

    implementation 'com.google.firebase:firebase-analytics:17.2.0'
    
    implementation 'com.google.firebase:firebase-messaging:20.0.0'
    
    implementation 'com.google.firebase:firebase-core:17.2.0'
    
    implementation 'com.onesignal:OneSignal:[3.11.2, 3.99.99]'
    
    implementation 'com.mcxiaoke.volley:library:1.0.0'
    
    implementation 'com.parse.bolts:bolts-tasks:1.3.0'
    
    implementation 'com.parse:parse-android:1.13.0'
    

In project level build.gradle, add the following code in 

>buildscript>repositories: maven { url "https://jitpack.io" }.

>buildscript>dependencies: classpath 'com.google.gms:google-services:4.3.2'

Hoping that will be enough to set up the app and run locally.

Required IDE:
Android Studio

Link to the app:
https://appdistribution.firebase.dev/i/MwMqtJ2z

Google drive link to other documents and video:
https://drive.google.com/open?id=1QWKUwuXhnc1lbw_WT4xtiIV7P-_4aeqt
