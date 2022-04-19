package cn.authing.guard.finclip;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.finogeeks.lib.applet.client.FinAppClient;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.news).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startMiniProgram("6240251d78c1a7000142b0f8");
            }
        });

        findViewById(R.id.guard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startMiniProgram("62467a363eb8ce0001b772a3");
                startMiniProgram("6244175278c1a7000142b2c5");
            }
        });
    }

    private void startMiniProgram(String appId){
        FinAppClient.INSTANCE.getAppletApiManager().startApplet(MainActivity.this, appId);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_user) {
            Intent intent = new Intent(this, UserActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

}