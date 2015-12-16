package gruppe16.sdupay;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class Main extends Activity {


    static EditText et1, et2, et3, et4;
    private String password = "1234";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        et1 = (EditText) findViewById(R.id.editText1);
        et2 = (EditText) findViewById(R.id.editText2);
        et3 = (EditText) findViewById(R.id.editText3);
        et4 = (EditText) findViewById(R.id.editText4);
        et1.addTextChangedListener(new CustomTextWatcher(et1, Main.this));
        et2.addTextChangedListener(new CustomTextWatcher(et2, Main.this));
        et3.addTextChangedListener(new CustomTextWatcher(et3, Main.this));
        et4.addTextChangedListener(new CustomTextWatcher(et4, Main.this));
    }

    public void login (String passInput){
        if(passInput.equals(password)){
            Log.d("asd", "PASS IS CORRECT");
            Intent appIntent = new Intent(getApplicationContext(), AppActivity.class);
            startActivity(appIntent);

        } else {
            Log.d("asd2", "PASS IS NOT CORRECT");
        }

    }



}
