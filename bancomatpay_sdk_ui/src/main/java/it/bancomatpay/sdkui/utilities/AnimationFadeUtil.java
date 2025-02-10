package it.bancomatpay.sdkui.utilities;

import android.animation.Animator;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;

import static android.view.View.GONE;

public class AnimationFadeUtil {

    public static final int DEFAULT_DURATION = 100; //in millis
    public static final int DEFAULT_CROSSFADE_DURATION = 120; //in millis

    /**
     * Util method to fade in a view,
     * uses android.view.animation.Animation API
     */
    public static void startFadeInAnimationV1(final View view, int duration) {
        // Fix for crash in android 6.0 Marshmallow
        if (view.getVisibility() == GONE) {
            view.setVisibility(View.VISIBLE);
        }
        // End fix
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new LinearInterpolator());
        fadeIn.setDuration(duration);
        fadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                view.setVisibility(View.VISIBLE);
            }
            @Override
            public void onAnimationEnd(Animation animation) {
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        view.startAnimation(fadeIn);
    }

    /**
     * Util method to fade in a view,
     * uses android.view.animation.Animation API
     */
    public static void startFadeInAnimationV1(final View view, int duration, final AnimationEndListener listener) {
        // Fix for crash in android 6.0 Marshmallow
        if (view.getVisibility() == GONE){
            view.setVisibility(View.INVISIBLE);
        }
        // End fix
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new LinearInterpolator());
        fadeIn.setDuration(duration);
        fadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                view.setVisibility(View.VISIBLE);
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                listener.onAnimationEndAction();
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        view.startAnimation(fadeIn);
    }

    /**
     * Util method to fade out a view,
     * uses android.view.animation.Animation API
     */
    public static void startFadeOutAnimationV1(final View view, int duration, final int visibility) {
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new LinearInterpolator());
        fadeOut.setDuration(duration);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(visibility);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        view.startAnimation(fadeOut);
    }

    /**
     * Util method to fade out a view,
     * uses android.view.animation.Animation API
     */
    public static void startFadeOutAnimationV1(final View view, int duration, final int visibility, final AnimationEndListener listener) {
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new LinearInterpolator());
        fadeOut.setDuration(duration);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(visibility);
                listener.onAnimationEndAction();
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        view.startAnimation(fadeOut);
    }

    /**
     * Util method to fade in a view,
     * uses android.view.ViewPropertyAnimator API
     */
    public static void startFadeInAnimationV2(final View view, int duration) {
        // Fix for crash in android 6.0 Marshmallow
        if (view.getVisibility() == GONE) {
            view.setVisibility(View.INVISIBLE);
        }
        // End fix
        view.post(() ->
                view.animate().alpha(1).setDuration(duration).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        view.setVisibility(View.VISIBLE);
                    }
                    @Override
                    public void onAnimationEnd(Animator animation) {
                    }
                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }
                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                }).start()
        );
    }

    /**
     * Util method to fade in a view,
     * uses android.view.ViewPropertyAnimator API
     */
    public static void startFadeInAnimationV2(final View view, int duration, final AnimationEndListener listener) {
        // Fix for crash in android 6.0 Marshmallow
        if (view.getVisibility() == GONE) {
            view.setVisibility(View.INVISIBLE);
        }
        // End fix
        view.post(() ->
                view.animate().alpha(1).setDuration(duration).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        view.setVisibility(View.VISIBLE);
                    }
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        listener.onAnimationEndAction();
                    }
                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }
                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                }).start()
        );
    }

    /**
     * Util method to fade out a view,
     * uses android.view.ViewPropertyAnimator API
     */
    public static void startFadeOutAnimationV2(final View view, int duration, final int visibility) {
        view.post(() ->
                view.animate().alpha(0).setDuration(duration).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setVisibility(visibility);
                    }
                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }
                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                }).start()
        );
    }

    /**
     * Util method to fade out a view,
     * uses android.view.ViewPropertyAnimator API
     */
    public static void startFadeOutAnimationV2(final View view, int duration, final int visibility, final AnimationEndListener listener) {
        view.post(() ->
                view.animate().alpha(0).setDuration(duration).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setVisibility(visibility);
                        listener.onAnimationEndAction();
                    }
                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }
                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                }).start()
        );
    }

    /**
     * Util method to crossfade 2 views,
     * uses android.view.ViewPropertyAnimator API
     */
    public static void startCrossfadeAnimation(final View view1, final View view2, int duration1, int duration2) {
        view2.setAlpha(0);
        ViewPropertyAnimator fadeIn = view2.animate().alpha(1).setDuration(duration2)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        //view2.setAlpha(0);
                    }
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        //view2.setVisibility(View.VISIBLE);
                    }
                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }
                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                }).setStartDelay(duration1);
        view1.animate().alpha(0).setDuration(duration1)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        view1.setVisibility(View.VISIBLE);
                        view2.setVisibility(View.VISIBLE);
                    }
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view1.setVisibility(View.INVISIBLE);
                        fadeIn.start();
                    }
                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }
                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                }).start();
    }

    public interface AnimationEndListener {
        void onAnimationEndAction();
    }

}
