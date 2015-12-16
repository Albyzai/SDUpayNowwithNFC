package gruppe16.sdupay;

import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

/**
 * Created by jonaspedersen on 15/12/15.
 */
public class CustomTextWatcher implements TextWatcher {

    static String et1String, et2String, et3String, et4String;
    private EditText mEditText;
    private Main MainObject = new Main();
    private String passString;

    public CustomTextWatcher(EditText e, Main MainObject) {
        mEditText = e;
        this.MainObject = MainObject;
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (mEditText == Main.et1){
            et1String = Main.et1.getText().toString();
            Main.et2.requestFocus();
        } else if (mEditText == Main.et2){
            et2String = Main.et2.getText().toString();
            Main.et3.requestFocus();
        } else if (mEditText == Main.et3){
            et3String = Main.et3.getText().toString();
            Main.et4.requestFocus();
        } else if (mEditText == Main.et4){
            et4String = Main.et4.getText().toString();
            passString = et1String + et2String + et3String + et4String;
            MainObject.login(passString);

        }
    }
}
