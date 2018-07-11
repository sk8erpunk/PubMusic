package snikdizh.pub_music;

import android.app.Activity;
import android.app.Application;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseUser;

import snikdizh.pub_music.subclasses.Room;
import snikdizh.pub_music.subclasses.Song;

/**
 * Created by Magellan on 16/03/2016.
 */
public class PubMusicApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // beware: the next monster exists to force the portrait orientation:
        // register to be informed of activities starting up
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {

            @Override
            public void onActivityCreated(Activity activity,
                                          Bundle savedInstanceState) {
                // new activity created; force its orientation to portrait
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }

        });

        ParseObject.registerSubclass(Room.class);
        ParseObject.registerSubclass(Song.class);

        // Required - Initialize the Parse SDK
        Parse.enableLocalDatastore(this);
        Parse.initialize(this);
        Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG);
//        ParseInstallation.getCurrentInstallation().saveInBackground();
//        ParsePush.subscribeInBackground("Chat");  // subscribe to channel for push notification

        ParseUser.enableAutomaticUser();
    }

}
