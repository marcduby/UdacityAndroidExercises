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
                getClassifications(face);
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
}
