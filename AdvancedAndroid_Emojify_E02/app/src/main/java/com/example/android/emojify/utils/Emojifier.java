package com.example.android.emojify.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.example.android.emojify.R;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

/**
 * Created by mduby on 8/12/18.
 */

public class Emojifier {
    // static variables
    private static final double SMILING_PROB_THRESHOLD = .15;
    private static final double EYE_OPEN_PROB_THRESHOLD = .5;

    /**
     * detects faces
     *
     * @param context
     * @param bitmap
     */
    public static void detectFaces(Context context, Bitmap bitmap) {
        // local variables
        int numberOfFaces = 0;

        // log
        Log.i(Emojifier.class.getName(), "In face detector");

        // detect the faces in the image and log the results
        // create the face detector
        FaceDetector faceDetector = new FaceDetector.Builder(context)
                .setTrackingEnabled(false)
                .setLandmarkType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();

        // build the frame
        Frame frame = new Frame.Builder().setBitmap(bitmap).build();

        // get the array of faces
        SparseArray<Face> faceSparseArray = faceDetector.detect(frame);

        // set the number of faces
        numberOfFaces = faceSparseArray.size();

        // log
        Log.i(Emojifier.class.getName(), "Got number of faces: " + numberOfFaces);

        // if there are faces, log
        if (numberOfFaces == 0) {
            // if no faces, show a toast message
            Toast.makeText(context, R.string.message_no_faces, Toast.LENGTH_LONG).show();

        } else {
            // get the probablity for each face
            for (int i = 0; i < faceSparseArray.size(); i++) {
                Face face = faceSparseArray.get(i);

                // get the face probability
                whichEmoji(face);
            }
        }

        // release the detector
        faceDetector.release();
    }

    /**
     * log the probabilty of smiling, left and right eyes open
     *
     * @param face
     */
    private static void getClassifications(Face face) {
        // local variables
        String className = Emojifier.class.getName();

        // log
        Log.i(className, "the left eye open probability is: " + face.getIsLeftEyeOpenProbability());
        Log.i(className, "the right eye open probability is: " + face.getIsRightEyeOpenProbability());
        Log.i(className, "the smiling probability is: " + face.getIsSmilingProbability());
    }

    private static void whichEmoji(Face face) {
        // local variables
        String className = Emojifier.class.getName();

        // Log all the probabilities
        Log.d(className, "whichEmoji: smilingProb = " + face.getIsSmilingProbability());
        Log.d(className, "whichEmoji: leftEyeOpenProb = "
                + face.getIsLeftEyeOpenProbability());
        Log.d(className, "whichEmoji: rightEyeOpenProb = "
                + face.getIsRightEyeOpenProbability());


        boolean smiling = face.getIsSmilingProbability() > SMILING_PROB_THRESHOLD;

        boolean leftEyeClosed = face.getIsLeftEyeOpenProbability() < EYE_OPEN_PROB_THRESHOLD;
        boolean rightEyeClosed = face.getIsRightEyeOpenProbability() < EYE_OPEN_PROB_THRESHOLD;


        // Determine and log the appropriate emoji
        Emoji emoji;
        if(smiling) {
            if (leftEyeClosed && !rightEyeClosed) {
                emoji = Emoji.LEFT_WINK;
            }  else if(rightEyeClosed && !leftEyeClosed){
                emoji = Emoji.RIGHT_WINK;
            } else if (leftEyeClosed){
                emoji = Emoji.CLOSED_EYE_SMILE;
            } else {
                emoji = Emoji.SMILE;
            }
        } else {
            if (leftEyeClosed && !rightEyeClosed) {
                emoji = Emoji.LEFT_WINK_FROWN;
            }  else if(rightEyeClosed && !leftEyeClosed){
                emoji = Emoji.RIGHT_WINK_FROWN;
            } else if (leftEyeClosed){
                emoji = Emoji.CLOSED_EYE_FROWN;
            } else {
                emoji = Emoji.FROWN;
            }
        }


        // Log the chosen Emoji
        Log.d(className, "whichEmoji: " + emoji.name());
    }

    // Enum for all possible Emojis
    private enum Emoji {
        SMILE,
        FROWN,
        LEFT_WINK,
        RIGHT_WINK,
        LEFT_WINK_FROWN,
        RIGHT_WINK_FROWN,
        CLOSED_EYE_SMILE,
        CLOSED_EYE_FROWN
    }

}
