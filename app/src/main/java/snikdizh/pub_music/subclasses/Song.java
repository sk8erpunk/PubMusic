package snikdizh.pub_music.subclasses;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

@ParseClassName("Song")
public class Song extends ParseObject{

    public Song() {}

    public void setId(long id) {
        put("songId", id);

        saveInBackground(new SaveCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    // Saved successfully.
                } else {
                    // The save failed.
                }
            }
        });
    }

    public void setTitle(String id) {
        put("title", id);
        saveInBackground(new SaveCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    // Saved successfully.
                } else {
                    // The save failed.
                }
            }
        });
    }
    public void setArtist(String id) {
        put("artist", id);
        saveInBackground(new SaveCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    // Saved successfully.
                } else {
                    // The save failed.
                }
            }
        });
    }
    public void setOwnerId(String id) {
        put("ownerId", id);
        saveInBackground(new SaveCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    // Saved successfully.
                } else {
                    // The save failed.
                }
            }
        });
    }
    public void setRoom(long id) {
        put("room", id);
        saveInBackground(new SaveCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    // Saved successfully.
                } else {
                    // The save failed.
                }
            }
        });
    }
    public void setRate(long id) {
        put("rate", id);
        saveInBackground(new SaveCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    // Saved successfully.
                } else {
                    // The save failed.
                }
            }
        });
    }


    public long getId(){
        return getLong("songId");
    }

    public String getTitle(){
        return getString("title");
    }

    public String getArtist(){
        return getString("artist");
    }

    public int getRoom() {
        return getInt("room");
    }

    public String getOwnerId(){
        return getString("ownerId");
    }

    public int getRate(){
        return getInt("rate");
    }

    public void upvote() {
        increment("rate");
        saveInBackground(new SaveCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    // Saved successfully.
                } else {
                    // The save failed.
                }
            }
        });
    }

    public void downvote() {
        increment("rate", -1);
        saveInBackground(new SaveCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    // Saved successfully.
                } else {
                    // The save failed.
                }
            }
        });
    }

}