package snikdizh.pub_music.subclasses;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import snikdizh.pub_music.R;

/**
 * Created by Magellan on 16/03/2016.
 */
public class OverallPlaylist extends AppCompatActivity {
    public Boolean isDJ = null;
    public Integer roomId = null;
    public String userId = null;

    Menu menu;

    ProgressDialog progressDialog;
    OPAdapter adapter;

    private static Handler handler;

    // Flag to hold if the activity is running or not.
    private boolean isRunning;

    public static boolean isLoaded = true;
    public static Object lock = new Object();

    private boolean isPlaying = false;
    private boolean isFinished = true;

    String curSongURL = null;
    Boolean isSongReady = false;
    ArrayList<String> fiveClosestSongsURL = new ArrayList<>();


    MediaPlayer mMediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isLoaded = true;

        userId = getIntent().getStringExtra("userId");
        roomId = getIntent().getIntExtra("roomId", -1);
        isDJ = getIntent().getBooleanExtra("isDJ", false);

        setContentView(R.layout.activity_overall_playlist);

        handler = new Handler();

        adapter = new OPAdapter(this);
        adapter.setAutoload(false);
        ListView listView = (ListView) findViewById(R.id.overall_playlist);
        listView.setAdapter(adapter);

        Log.w("progress dialog", "onLoading");
        progressDialog = ProgressDialog.show(OverallPlaylist.this, "", "Loading...", true);

        adapter.addOnQueryLoadListener(new ParseQueryAdapter.OnQueryLoadListener<ParseObject>() {
            @Override
            public void onLoading() {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void onLoaded(List<ParseObject> objects, Exception e) {
                //To change body of implemented methods use File | Settings | File Templates.
                if (e == null) {
                    Log.w("progress dialog", "onLoaded");
                } else {
                    e.printStackTrace();
                }

                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();

                synchronized (lock) {
                    isLoaded = true;
                }
            }
        });
    }

    private void checkPlaylist() {
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                if (isRunning) {
//                    adapter.notifyDataSetChanged();
                    boolean tmp;
                    synchronized (lock) {
                        tmp = isLoaded;
                    }
                    if(tmp) {
                        synchronized (lock) {
                            isLoaded = false;
                        }
                        adapter.loadObjects();
                    }
                    checkPlaylist();
                }
            }
        }, 500);    // TODO: check, maybe 500 is better
    }

    @Override
    protected void onResume() {
        super.onResume();
        isRunning = true;
        invalidateOptionsMenu();
        checkPlaylist();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isRunning = false;
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if(mMediaPlayer != null) {
//            mMediaPlayer.release();
//            mMediaPlayer = null;
//        }
//    }

    @Override
    public void setContentView(int layoutResID)
    {
        super.setContentView(layoutResID);
        Log.i("here", "here1");
        setupActionBar();
    }

    /**
     * This method will setup the top title bar (Action bar) content and display
     * values. It will also setup the custom background theme for ActionBar. You
     * can override this method to change the behavior of ActionBar for
     * particular Activity
     */
    protected void setupActionBar()
    {
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar == null)
            return;
        Log.i("here","here2");
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        Log.i("here", "here3");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu newMenu) {
        Log.i("here","here4: "+ userId);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.o_p_menu, newMenu);
        this.menu = newMenu;

//        newMenu.add("Room " + String.valueOf(roomId));

        MenuItem add_song_item = menu.findItem(R.id.add_song_button);
        add_song_item.setVisible(true);
        add_song_item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        if(isDJ) {
            MenuItem play_stop_item = menu.findItem(R.id.play_stop_button);
            play_stop_item.setVisible(true);
            play_stop_item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            isRunning = false;
            finish();
        }

        if(item.getItemId() == R.id.add_song_button) {

            final Dialog d = new Dialog(OverallPlaylist.this);
            d.setTitle("Chose wisely:");
            d.setContentView(R.layout.activity_local_playlist);

            final ListView lw = (ListView) d.findViewById(R.id.local_playlist);

            ContentResolver contentResolver = getContentResolver();
            Uri uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            Cursor cursor = contentResolver.query(uri, null, null, null, null);

            ArrayList<LocalSong> localSongs = new ArrayList<>();

            if (cursor == null) {
                // query failed, handle error.
            } else if (!cursor.moveToFirst()) {
                // no media on the device
                cursor.close();
            } else {
                int titleColumn = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
                int idColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media._ID);
                int artistColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
                int isMusicColumn = cursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC);
                do {
                    if(cursor.getInt(isMusicColumn) != 0) {
                        int thisId = cursor.getInt(idColumn);
                        String thisTitle = cursor.getString(titleColumn);
                        String thisArtist = cursor.getString(artistColumn);
                        Log.i("ID", Long.toString(thisId));
                        Log.i("TITLE", thisTitle);
                        Log.i("ARTIST", thisArtist);
                        LocalSong song = new LocalSong(thisTitle, thisArtist, thisId);
                        localSongs.add(song);
                    }
                } while (cursor.moveToNext());
                cursor.close();
            }

            LPAdapter adp = new LPAdapter(OverallPlaylist.this, R.layout.local_songs_row, localSongs);
            lw.setAdapter(adp);
            lw.setClickable(true);

            lw.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    LocalSong newSong  = (LocalSong) lw.getAdapter().getItem(position);

                    Log.i("aaaaaargh!","arara!");
                    Song po = new Song();
                    po.setArtist(newSong.artist);
                    po.setId(newSong.id);
                    po.setOwnerId(userId);
                    po.setRate(0);
                    po.setRoom(roomId);
                    po.setTitle(newSong.title);
                    Log.i("aaaaaargh!", "arara!2");
                    po.saveInBackground(new SaveCallback() {

                        @Override
                        public void done(ParseException e) {
                            Log.i("aaaaaargh!", "arara!3");
                            if (e == null) {
                                // success
                            } else {
                                // failed
                            }
                            boolean tmp;
                            synchronized (lock) {
                                tmp = isLoaded;
                            }
                            if (tmp) {
                                synchronized (lock) {
                                    isLoaded = false;
                                }
                                adapter.loadObjects();
                            }
                        }
                    });

                    d.dismiss();
                }
            });

            d.show();
        }

        if(item.getItemId() == R.id.play_stop_button) {
            Toast.makeText(getApplicationContext(), "Let's roll!", Toast.LENGTH_SHORT).show();

            if(isPlaying) {
                isPlaying = false;
                item.setTitle(R.string.play_song);

                //TODO: stop this madness
                mMediaPlayer.pause();
            } else {
                isPlaying = true;
                item.setTitle(R.string.stop_song);

                Song song = (Song) adapter.getItem(0);

                if(!isFinished) {
                    mMediaPlayer.start();
                } else {
                    if (song.getOwnerId().equals(userId)) {
                        Log.e("Hi", "Hihihi");
                        playMySong(song);
                    } else {
                        // TODO: load from Parse
                        //TODO: playNotMySong(song);
                    }

                    song.setRate(Integer.MAX_VALUE);
//                    isFinished = false;
                }
            }

            invalidateOptionsMenu();
        }


        return super.onOptionsItemSelected(item);
    }

    private void playMySong(Song song) {
        // TODO: make a service to run in background
        if(mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
        }
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
//                isFinished = true;
                Song oldSong = (Song) adapter.getItem(0);
                Song newSong = (Song) adapter.getItem(1);
                newSong.setRate(Integer.MAX_VALUE);
                oldSong.deleteInBackground();

                if (newSong.getOwnerId().equals(userId)) {
                    playMySong(newSong);
                } else {
                    //TODO: playNotMySong(newSong);
                }

            }
        });
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        long id = song.getId();
        final Uri contentUri = ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
        try {
            mMediaPlayer.setDataSource(getApplicationContext(), contentUri);
        } catch (IOException e) {}
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                isFinished = false;
                mMediaPlayer.start();
            }
        });

        mMediaPlayer.prepareAsync();
    }

    public class OPAdapter extends ParseQueryAdapter<ParseObject> {

        Context context;

        public OPAdapter(Context context) {

            super(context, new ParseQueryAdapter.QueryFactory<ParseObject>() {
                @Override
                public ParseQuery create() {
                    ParseQuery query = new ParseQuery("Song");
                    query.whereEqualTo("room", roomId);
                    query.orderByDescending("rate");
                    return query;
                }
            });

            this.context = context;
        }

        // Customize the layout by overriding getItemView
        @Override
        public View getItemView(final ParseObject object, View v, ViewGroup parent) {
            if (v == null) {
                v = View.inflate(context, R.layout.row_design, null);
            }

            TextView title = (TextView) v.findViewById(R.id.title);
            title.setText(((Song) object).getTitle());

            TextView artist = (TextView) v.findViewById(R.id.artist);
            artist.setText(((Song) object).getArtist());

            TextView rate = (TextView) v.findViewById(R.id.rate);
            ImageButton upvote = (ImageButton) v.findViewById(R.id.upvote);
            ImageButton downvote = (ImageButton) v.findViewById(R.id.downvote);
            ImageView pic = (ImageView) v.findViewById(R.id.play);

            if((object == getItem(0)) && (isPlaying)) {
                rate.setVisibility(View.GONE);
                upvote.setVisibility(View.GONE);
                downvote.setVisibility(View.GONE);
                pic.setVisibility(View.VISIBLE);
                invalidateOptionsMenu();
            } else {
                pic.setVisibility(View.GONE);

                rate.setVisibility(View.VISIBLE);
                rate.setText(String.valueOf(((Song) object).getRate()));

//                final OPAdapter adp = this;
                upvote.setVisibility(View.VISIBLE);
                upvote.setOnClickListener(new View.OnClickListener() {
                                              @Override
                                              public void onClick(View v) {
                                                  ((Song) object).upvote();
//                                          adp.notifyDataSetChanged();
//                                          boolean tmp;
//                                          synchronized (OverallPlaylist.lock) {
//                                              tmp = OverallPlaylist.isLoaded;
//                                          }
//                                          if(tmp) {
//                                              synchronized (OverallPlaylist.lock) {
//                                                  OverallPlaylist.isLoaded = false;
//                                              }
//                                              loadObjects();
//                                          }
                                              }
                                          }
                );

                downvote.setVisibility(View.VISIBLE);
                downvote.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    ((Song) object).downvote();
//                                          adp.notifyDataSetChanged();
//                                          adp.loadObjects();
                                                }
                                            }
                );
                invalidateOptionsMenu();
            }




            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                Log.i("ITEM CLICK","ITEM CLICK");
//                Intent intent = new Intent(getContext(), HitchDetails.class);
//                intent.putExtra("userId", ((Path) object).getDriver().getObjectId());
//                intent.putExtra("pathId", object.getObjectId());
//                startActivity(intent);
                }
            });

//                // more details image click -
//            ImageButton image = (ImageButton) w.findViewById(R.id.more_details_button);
//            image.setOnClickListener(new View.OnClickListener() {
//                 @Override
//                 public void onClick(View v) {
//                     Intent intent = new Intent(getContext(), HitchDetails.class);
//                     intent.putExtra("userId", ((Path) object).getDriver().getObjectId());
//                     intent.putExtra("pathId", object.getObjectId());
//                     startActivity(intent);
//                 }
//             }
//            );

            return v;
        }
    }


    private class LPAdapter extends ArrayAdapter<LocalSong>{

        Context context;
        int layoutResourceId;
        ArrayList<LocalSong> data;

        public LPAdapter(Context context, int layoutResourceId, ArrayList<LocalSong> data) {
            super(context, layoutResourceId, data);
            this.layoutResourceId = layoutResourceId;
            this.context = context;
            this.data = data;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            LocalHolder holder = null;

            if(row == null)
            {
                LayoutInflater inflater = ((Activity)context).getLayoutInflater();
                row = inflater.inflate(layoutResourceId, parent, false);

                holder = new LocalHolder();
                holder.title = (TextView)row.findViewById(R.id.local_title);
                holder.artist = (TextView)row.findViewById(R.id.local_artist);

                row.setTag(holder);
            }
            else
            {
                holder = (LocalHolder)row.getTag();
            }

            LocalSong ls = data.get(position);
            holder.title.setText(ls.title);
            holder.artist.setText(ls.artist);

            return row;
        }

        class LocalHolder {
            TextView title;
            TextView artist;
        }
    }

}

















