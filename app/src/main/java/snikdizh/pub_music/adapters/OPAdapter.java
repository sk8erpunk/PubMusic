package snikdizh.pub_music.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.ParseQueryAdapter;

import java.util.ArrayList;
import java.util.Date;

import snikdizh.pub_music.R;
import snikdizh.pub_music.subclasses.OverallPlaylist;
import snikdizh.pub_music.subclasses.Song;

public class OPAdapter extends ParseQueryAdapter<ParseObject> {

    Context context;
    public static int roomId;

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
            v = View.inflate(context, R.layout.o_p_adapter_row, null);
        }

        int index = parent.indexOfChild(v);

        TextView title = (TextView) v.findViewById(R.id.title);
        title.setText(((Song) object).getTitle());

        TextView artist = (TextView) v.findViewById(R.id.artist);
        artist.setText(((Song) object).getArtist());

        TextView rate = (TextView) v.findViewById(R.id.rate);
        rate.setText(String.valueOf(((Song) object).getRate()));


        final OPAdapter adp = this;
        ImageButton upvote = (ImageButton) v.findViewById(R.id.upvote);
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

        ImageButton downvote = (ImageButton) v.findViewById(R.id.downvote);
        downvote.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View v) {
                                          ((Song) object).downvote();
//                                          adp.notifyDataSetChanged();
//                                          adp.loadObjects();
                                      }
                                  }
        );


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
