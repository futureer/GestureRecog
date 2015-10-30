#Smartphone Gesture Recognize app
## Summary
This repo is programmed for a UAV competition in SJTU 2015.05.
The whole app can recognize a gesture, and then send instructions to the uav.
And this repository extract the gesture recognition part. 
With a simple theory of pattern  recognition. This APP can recognize up to 10 already-recorded gestures with 3-axis accelerators and 
cosine similarity algorithm.
Wish these codes would help others.

## How it works
Firstly, You should record your gestures with this app's instructions.
Including input your gesture's id , perform the same gestures for times so that
APP can remember the gesture's features.(You should keep tapping the screen while you are performing gestures, and release as soon as 
the gesture ends).

Secondly, tap the recognize button to enter recognize part. Tap the screen when performing gestures as before. Then this app will give the
recognize result in both UI and logcat.

## The Recognize Algorithm:
The main idea of this app is to compare the similarities between gestures with cos similarity algorithm ,
and use a 3-dimension vector to represent a gesture.

1.Represent a gesture: 
Take advantage of the 3-axis accelerator in Android devices, keep sampling the accelerate while screen is being tapped every 50ms,
and map them into a vector).

2.Calculate the similarities value between the recorded gestures and the to-be-recognized gesture vector.
The gesture will be considered the class of gesture that has the minimum similarity value.
Normalization: To make vectors having different lengths , I normalize all vectors in to a 60 sample accelerates with linear interpolation algorithm.
