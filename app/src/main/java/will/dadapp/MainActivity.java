package will.dadapp;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * finds and displays fishing programmes which are on sky tv
     * @param view
     */
    public void findAndShowProgrammes(View view){
        Intent intent = new Intent(this, ProgrammesActivity.class);
        startActivity(intent);
    }
}
