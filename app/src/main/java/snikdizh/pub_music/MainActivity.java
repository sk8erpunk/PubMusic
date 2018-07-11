package snikdizh.pub_music;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

//import snikdizh.pub_music.adapters.OPAdapter;
import snikdizh.pub_music.subclasses.OverallPlaylist;

public class MainActivity extends AppCompatActivity {

    ParseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00212121")));

        setContentView(R.layout.activity_main);


        user = ParseUser.getCurrentUser();
        user.put("id", "dummy_id");
        user.saveInBackground();

        Button createRoomButton = (Button) findViewById(R.id.button_create_room);
        Button joinRoomButton = (Button) findViewById(R.id.button_join_room);

        createRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Create Room", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(MainActivity.this, OverallPlaylist.class);

                //TODO: get next available id for the room;
                int roomId = -1;
//                OPAdapter.roomId = roomId;
                intent.putExtra("roomId",roomId);
                intent.putExtra("isDJ", true);
                intent.putExtra("userId", user.getObjectId());

                startActivity(intent);
            }
        });

        joinRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Join Room", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(MainActivity.this, OverallPlaylist.class);

                //TODO: show dialog to chose the room id
                int roomId = -1;
//                OPAdapter.roomId = roomId;
                intent.putExtra("roomId",roomId);
                intent.putExtra("isDJ", false);
                intent.putExtra("userId", user.getObjectId());

                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

}
