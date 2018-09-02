/*
* Copyright (C) 2017 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*  	http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.example.android.classicalmusicquiz;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;

public class QuizActivity extends AppCompatActivity implements View.OnClickListener {
    // instance variables
    private String TAG = this.getClass().getName();

    private static final int CORRECT_ANSWER_DELAY_MILLIS = 1000;
    private static final String REMAINING_SONGS_KEY = "remaining_songs";
    private int[] mButtonIDs = {R.id.buttonA, R.id.buttonB, R.id.buttonC, R.id.buttonD};
    private ArrayList<Integer> mRemainingSampleIDs;
    private ArrayList<Integer> mQuestionSampleIDs;
    private int mAnswerSampleID;
    private int mCurrentScore;
    private int mHighScore;
    private Button[] mButtons;
    private SimpleExoPlayer simpleExoPlayer;
    private SimpleExoPlayerView simpleExoPlayerView;

    // step 12 - adding media session object
    private MediaSessionCompat mediaSessionCompat;
    private PlaybackStateCompat.Builder playbackBuilder;

    // setp 13 - adding notification
    private NotificationManager mNotificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        // TODO (2): Replace the ImageView with the SimpleExoPlayerView, and remove the method calls on the composerView.
//        ImageView composerView = (ImageView) findViewById(R.id.composerView);
        this.simpleExoPlayerView = (SimpleExoPlayerView)this.findViewById(R.id.exoplayer_view);

        boolean isNewGame = !getIntent().hasExtra(REMAINING_SONGS_KEY);

        // If it's a new game, set the current score to 0 and load all samples.
        if (isNewGame) {
            QuizUtils.setCurrentScore(this, 0);
            mRemainingSampleIDs = Sample.getAllSampleIDs(this);
            // Otherwise, get the remaining songs from the Intent.
        } else {
            mRemainingSampleIDs = getIntent().getIntegerArrayListExtra(REMAINING_SONGS_KEY);
        }

        // Get current and high scores.
        mCurrentScore = QuizUtils.getCurrentScore(this);
        mHighScore = QuizUtils.getHighScore(this);

        // Generate a question and get the correct answer.
        mQuestionSampleIDs = QuizUtils.generateQuestion(mRemainingSampleIDs);
        mAnswerSampleID = QuizUtils.getCorrectAnswerID(mQuestionSampleIDs);

        // TODO (3): Replace the default artwork in the SimpleExoPlayerView with the question mark drawable.
        // Load the image of the composer for the answer into the ImageView.
        this.simpleExoPlayerView.setDefaultArtwork(BitmapFactory.decodeResource(this.getResources(), R.drawable.question_mark));
//        composerView.setImageBitmap(Sample.getComposerArtBySampleID(this, mAnswerSampleID));

        // If there is only one answer left, end the game.
        if (mQuestionSampleIDs.size() < 2) {
            QuizUtils.endGame(this);
            finish();
        }

        // Initialize the buttons with the composers names.
        mButtons = initializeButtons(mQuestionSampleIDs);

        // TODO (4): Create a Sample object using the Sample.getSampleByID() method and passing in mAnswerSampleID;
        Sample answerSample = Sample.getSampleByID(this, this.mAnswerSampleID);
        if (answerSample == null) {
            Toast.makeText(this, this.getString(R.string.sample_list_load_error), Toast.LENGTH_SHORT).show();;
            return;
        }

        // step 12 - create a media session object
        this.mediaSessionCompat = new MediaSessionCompat(this, TAG);

        // set the media session flags
        this.mediaSessionCompat.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // set the media button receiver component
        this.mediaSessionCompat.setMediaButtonReceiver(null);

        // set the available actions and initial statwe
        this.playbackBuilder = new PlaybackStateCompat.Builder().setActions(
                PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_PAUSE | PlaybackStateCompat.ACTION_PLAY_PAUSE |
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
        );
        this.mediaSessionCompat.setPlaybackState(this.playbackBuilder.build());

        // set the call back
        this.mediaSessionCompat.setCallback(new MySessionCallback());

        // start the session
        this.mediaSessionCompat.setActive(true);

        // TODO (5): Create a method called initializePlayer() that takes a Uri as an argument and call it here, passing in the Sample URI.
        this.initializePlayer(Uri.parse(answerSample.getUri()));
    }

    /**
     * initializes the exo player
     *
     * @param mediaUri
     */
    private void initializePlayer(Uri mediaUri) {
        // In initializePayer
        // TODO (6): Instantiate a SimpleExoPlayer object using DefaultTrackSelector and DefaultLoadControl.
        // TODO (7): Prepare the MediaSource using DefaultDataSourceFactory and DefaultExtractorsFactory, as well as the Sample URI you passed in.
        // TODO (8): Prepare the ExoPlayer with the MediaSource, start playing the sample and set the SimpleExoPlayer to the SimpleExoPlayerView.
        if (this.simpleExoPlayer == null) {
            // create the exoplayer
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            this.simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);

            // add in a listener
            this.simpleExoPlayer.addListener(new PlayerListener(this));

            // add the player to the player view
            this.simpleExoPlayerView.setPlayer(this.simpleExoPlayer);

            // prepare the media source
            String userAgent = Util.getUserAgent(this, "ClassicalMusicQuiz");
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(this, userAgent),
                    new DefaultExtractorsFactory(), null,null);
            this.simpleExoPlayer.prepare(mediaSource);
            this.simpleExoPlayer.setPlayWhenReady(true);
        }

    }

    /**
     * releases the exo player
     *
     */
    private void releasePalyer() {
        // NOTE - also call this in onPause and onStop when the app is not visible
        this.simpleExoPlayer.stop();
        this.simpleExoPlayer.release();
        this.simpleExoPlayer = null;
    }


    /**
     * Initializes the button to the correct views, and sets the text to the composers names,
     * and set's the OnClick listener to the buttons.
     *
     * @param answerSampleIDs The IDs of the possible answers to the question.
     * @return The Array of initialized buttons.
     */
    private Button[] initializeButtons(ArrayList<Integer> answerSampleIDs) {
        Button[] buttons = new Button[mButtonIDs.length];
        for (int i = 0; i < answerSampleIDs.size(); i++) {
            Button currentButton = (Button) findViewById(mButtonIDs[i]);
            Sample currentSample = Sample.getSampleByID(this, answerSampleIDs.get(i));
            buttons[i] = currentButton;
            currentButton.setOnClickListener(this);
            if (currentSample != null) {
                currentButton.setText(currentSample.getComposer());
            }
        }
        return buttons;
    }


    /**
     * The OnClick method for all of the answer buttons. The method uses the index of the button
     * in button array to to get the ID of the sample from the array of question IDs. It also
     * toggles the UI to show the correct answer.
     *
     * @param v The button that was clicked.
     */
    @Override
    public void onClick(View v) {

        // Show the correct answer.
        showCorrectAnswer();

        // Get the button that was pressed.
        Button pressedButton = (Button) v;

        // Get the index of the pressed button
        int userAnswerIndex = -1;
        for (int i = 0; i < mButtons.length; i++) {
            if (pressedButton.getId() == mButtonIDs[i]) {
                userAnswerIndex = i;
            }
        }

        // Get the ID of the sample that the user selected.
        int userAnswerSampleID = mQuestionSampleIDs.get(userAnswerIndex);

        // If the user is correct, increase there score and update high score.
        if (QuizUtils.userCorrect(mAnswerSampleID, userAnswerSampleID)) {
            mCurrentScore++;
            QuizUtils.setCurrentScore(this, mCurrentScore);
            if (mCurrentScore > mHighScore) {
                mHighScore = mCurrentScore;
                QuizUtils.setHighScore(this, mHighScore);
            }
        }

        // Remove the answer sample from the list of all samples, so it doesn't get asked again.
        mRemainingSampleIDs.remove(Integer.valueOf(mAnswerSampleID));

        // Wait some time so the user can see the correct answer, then go to the next question.
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // TODO (9): Stop the playback when you go to the next question.
                simpleExoPlayer.stop();

                Intent nextQuestionIntent = new Intent(QuizActivity.this, QuizActivity.class);
                nextQuestionIntent.putExtra(REMAINING_SONGS_KEY, mRemainingSampleIDs);
                finish();
                startActivity(nextQuestionIntent);
            }
        }, CORRECT_ANSWER_DELAY_MILLIS);

    }

    /**
     * Disables the buttons and changes the background colors to show the correct answer.
     */
    private void showCorrectAnswer() {
        // TODO (10): Change the default artwork in the SimpleExoPlayerView to show the picture of the composer, when the user has answered the question.
        this.simpleExoPlayerView.setDefaultArtwork(Sample.getComposerArtBySampleID(this, this.mAnswerSampleID));


        for (int i = 0; i < mQuestionSampleIDs.size(); i++) {
            int buttonSampleID = mQuestionSampleIDs.get(i);

            mButtons[i].setEnabled(false);

            if (buttonSampleID == mAnswerSampleID) {
                mButtons[i].getBackground().setColorFilter(ContextCompat.getColor
                                (this, android.R.color.holo_green_light),
                        PorterDuff.Mode.MULTIPLY);
                mButtons[i].setTextColor(Color.WHITE);
            } else {
                mButtons[i].getBackground().setColorFilter(ContextCompat.getColor
                                (this, android.R.color.holo_red_light),
                        PorterDuff.Mode.MULTIPLY);
                mButtons[i].setTextColor(Color.WHITE);

            }
        }
    }


    /**
     * Shows Media Style notification, with an action that depends on the current MediaSession
     * PlaybackState.
     * @param state The PlaybackState of the MediaSession.
     */
    private void showNotification(PlaybackStateCompat state) {
        // local variables
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        int icon;
        String play_pause;

        // check state of the player
        if(state.getState() == PlaybackStateCompat.STATE_PLAYING){
            icon = R.drawable.exo_controls_pause;
            play_pause = getString(R.string.pause);

        } else {
            icon = R.drawable.exo_controls_play;
            play_pause = getString(R.string.play);
        }

        // get the notification
        NotificationCompat.Action playPauseAction = new NotificationCompat.Action(
                icon, play_pause,
                MediaButtonReceiver.buildMediaButtonPendingIntent(this,
                        PlaybackStateCompat.ACTION_PLAY_PAUSE));

        NotificationCompat.Action restartAction = new android.support.v4.app.NotificationCompat
                .Action(R.drawable.exo_controls_previous, getString(R.string.restart),
                MediaButtonReceiver.buildMediaButtonPendingIntent
                        (this, PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS));

        // get the pending intent
        PendingIntent contentPendingIntent = PendingIntent.getActivity
                (this, 0, new Intent(this, QuizActivity.class), 0);

        // build the notification
        builder.setContentTitle(getString(R.string.guess))
                .setContentText(getString(R.string.notification_text))
                .setContentIntent(contentPendingIntent)
                .setSmallIcon(R.drawable.ic_music_note)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .addAction(restartAction)
                .addAction(playPauseAction)
                .setStyle(new NotificationCompat.MediaStyle()
                        .setMediaSession(this.mediaSessionCompat.getSessionToken())
                        .setShowActionsInCompactView(0,1));

        // launch the notification
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, builder.build());
    }

    // TODO (11): Override onDestroy() to stop and release the player when the Activity is destroyed.
    @Override
    protected void onDestroy() {
        super.onDestroy();

        // stop the media session
        this.mediaSessionCompat.setActive(false);

        // release the exo player
        this.releasePalyer();
    }

    public class PlayerListener implements ExoPlayer.EventListener {
        // instance variables
        Context context;

        public PlayerListener(Context con) {
            this.context = con;
        }

        @Override
        public void onLoadingChanged(boolean isLoading) {

        }

        @Override
        public void onTimelineChanged(Timeline timeline, Object manifest) {

        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {

        }

        @Override
        public void onPositionDiscontinuity() {

        }

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            if ((playbackState == ExoPlayer.STATE_READY) && playWhenReady) {
                // update the media session
                playbackBuilder.setState(PlaybackStateCompat.STATE_PLAYING, simpleExoPlayer.getCurrentPosition(), 1f);
                mediaSessionCompat.setPlaybackState(playbackBuilder.build());

                // toast
                Toast.makeText(this.context, "Player ready", Toast.LENGTH_SHORT).show();

            } else if ((playbackState == ExoPlayer.STATE_READY)) {
                // update the media session
                playbackBuilder.setState(PlaybackStateCompat.STATE_PAUSED, simpleExoPlayer.getCurrentPosition(), 1f);
                mediaSessionCompat.setPlaybackState(playbackBuilder.build());

                // toast
                Toast.makeText(this.context, "Player paused", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(this.context, "Player other state", Toast.LENGTH_SHORT).show();
            }

            // step 13 - show the state of the player in a notification
            showNotification(playbackBuilder.build());
        }
    }

    public class MySessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            super.onPlay();
        }

        @Override
        public void onPause() {
            super.onPause();
        }

        @Override
        public void onSkipToPrevious() {
            super.onSkipToPrevious();
        }
    }
}
