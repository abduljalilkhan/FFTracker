package com.khan.fftracker.Animation_Utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import com.varunest.sparkbutton.SparkButton;

public class Animate_Views {
        public Animate_Views() {

        }

        public void changeViews(final SparkButton btnMyVideo, final SparkButton btnRecievedVideo, final int video_upload, final int video_recieved1) {

            btnMyVideo.animate().rotationX(90).setDuration(200).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    btnMyVideo.setRotationX(-90);
                    btnMyVideo.animate().rotationX(0).setDuration(200).setListener(null);

                    btnMyVideo.setActiveImage(video_upload);
                    btnMyVideo.setInactiveImage(video_upload);

                    btnRecievedVideo.setActiveImage(video_recieved1);
                    btnRecievedVideo.setInactiveImage(video_recieved1);
                    ObjectAnimator anim = ObjectAnimator.ofFloat(btnRecievedVideo, "rotation",0, 0);
                    anim.setDuration(500);
                    anim.start();

                }
            });

        }

        //1)inFromRightAnimation

        public Animation inFromRightAnimation() {

            Animation inFromRight = new TranslateAnimation(
                    Animation.RELATIVE_TO_PARENT, +1.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f);
            inFromRight.setDuration(500);
            inFromRight.setInterpolator(new AccelerateInterpolator());
            return inFromRight;
        }

        //2)outToLeftAnimation
        public Animation outToLeftAnimation() {
            Animation outtoLeft = new TranslateAnimation(
                    Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, -1.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f);
            outtoLeft.setDuration(500);
            outtoLeft.setInterpolator(new AccelerateInterpolator());
            return outtoLeft;
        }

//3)inFromLeftAnimation

        public Animation inFromLeftAnimation() {
            Animation inFromLeft = new TranslateAnimation(
                    Animation.RELATIVE_TO_PARENT, -1.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f);
            inFromLeft.setDuration(500);
            inFromLeft.setInterpolator(new AccelerateInterpolator());
            return inFromLeft;
        }

//4)outToRightAnimation

        public Animation outToRightAnimation() {
            Animation outtoRight = new TranslateAnimation(
                    Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, +1.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f);
            outtoRight.setDuration(500);
            outtoRight.setInterpolator(new AccelerateInterpolator());
            return outtoRight;
        }

}
