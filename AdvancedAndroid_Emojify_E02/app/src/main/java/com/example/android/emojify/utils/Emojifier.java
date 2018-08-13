package com.example.android.emojify.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
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
    public static Bitmap detectFacesAndAddEmojis(Context context, Bitmap bitmap) {
        // local variables
        int numberOfFaces = 0;
        Emoji emoji = null;
        Bitmap modifiedBitmap = bitmap;

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
                Bitmap emojiBitmap = null;

                // get the face probability and emoji
                emoji = whichEmoji(face);

                // now get the bitmap emoji
                emojiBitmap = getEmojiBitmap(context, emoji);

                // replace the emoji in the modified full image bitmap
                modifiedBitmap = addBitmapToFace(modifiedBitmap, emojiBitmap, face);
            }
        }

        // release the detector
        faceDetector.release();

        // return
        return modifiedBitmap;
    }

    /**
     * return the matching emoji bitmap
     *
     * @param context
     * @param emoji
     * @return
     */
    private static Bitmap getEmojiBitmap(Context context, Emoji emoji) {
        // local variables
        Bitmap emojiBitmap = null;

        switch (emoji) {
            case SMILE:
                emojiBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.closed_frown);
                break;
            case FROWN:
                emojiBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.closed_frown);
                break;
            default:
                emojiBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.closed_frown);
                break;
        }

        // return
        return emojiBitmap;
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

    private static Emoji whichEmoji(Face face) {
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

        // return
        return emoji;
    }

    /**
     * Combines the original picture with the emoji bitmaps
     *
     * @param backgroundBitmap The original picture
     * @param emojiBitmap      The chosen emoji
     * @param face             The detected face
     * @return The final bitmap, including the emojis over the faces
     */
    private static Bitmap addBitmapToFace(Bitmap backgroundBitmap, Bitmap emojiBitmap, Face face) {
        // local variables
        float emojiScaleFactor = 1f;

        // Initialize the results bitmap to be a mutable copy of the original image
        Bitmap resultBitmap = Bitmap.createBitmap(backgroundBitmap.getWidth(),
                backgroundBitmap.getHeight(), backgroundBitmap.getConfig());

        // Scale the emoji so it looks better on the face
        float scaleFactor = emojiScaleFactor;

        // Determine the size of the emoji to match the width of the face and preserve aspect ratio
        int newEmojiWidth = (int) (face.getWidth() * scaleFactor);
        int newEmojiHeight = (int) (emojiBitmap.getHeight() *
                newEmojiWidth / emojiBitmap.getWidth() * scaleFactor);


        // Scale the emoji
        emojiBitmap = Bitmap.createScaledBitmap(emojiBitmap, newEmojiWidth, newEmojiHeight, false);

        // Determine the emoji position so it best lines up with the face
        float emojiPositionX =
                (face.getPosition().x + face.getWidth() / 2) - emojiBitmap.getWidth() / 2;
        float emojiPositionY =
                (face.getPosition().y + face.getHeight() / 2) - emojiBitmap.getHeight() / 3;

        // Create the canvas and draw the bitmaps to it
        Canvas canvas = new Canvas(resultBitmap);
        canvas.drawBitmap(backgroundBitmap, 0, 0, null);
        canvas.drawBitmap(emojiBitmap, emojiPositionX, emojiPositionY, null);

        return resultBitmap;
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
