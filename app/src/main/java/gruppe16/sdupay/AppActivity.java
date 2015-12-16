package gruppe16.sdupay;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.media.Image;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.app.Activity;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import android.nfc.NfcAdapter;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

public class AppActivity extends Activity {

    private static final String MIME_TEXT_PLAIN = "text/plain";
    public static final String TAG = "NfcDemo";

    ViewFlipper flippy;
    ImageButton cartBtn, favBtn, histBtn, profileBtn, menuBtn;
    Button depositBtn, scanBtn;
    LinearLayout cartView;
    RelativeLayout background;
    Typeface amaranthB, amaranthR;
    TextView title, balanceTV, balanceTV1, mTextView, txtTagContent;
    NfcAdapter nfcAdapter;
    EditText depositAmount;
    UserAccount account = new UserAccount();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);


    flippy = (ViewFlipper) findViewById(R.id.flippy);
    scanBtn = (Button) findViewById(R.id.scanBtn);
    cartBtn = (ImageButton) findViewById(R.id.cartBtn);
    favBtn = (ImageButton) findViewById(R.id.favBtn);
    histBtn = (ImageButton) findViewById(R.id.histBtn);
    profileBtn = (ImageButton) findViewById(R.id.profileButton);
    menuBtn = (ImageButton) findViewById(R.id.menuBtn);
    cartView = (LinearLayout) findViewById(R.id.cartView);
    title = (TextView) findViewById(R.id.Title);
    balanceTV = (TextView) findViewById(R.id.balanceTV);
    balanceTV1 = (TextView) findViewById(R.id.balanceTV1);
    depositBtn = (Button) findViewById(R.id.depositMoneyBtn);
    depositAmount = (EditText) findViewById(R.id.moneyAmount);



    balanceTV.setText("" + account.balance);

    cartBtn.setOnClickListener(button_click);
    favBtn.setOnClickListener(button_click);
    histBtn.setOnClickListener(button_click);
    profileBtn.setOnClickListener(button_click);
    depositBtn.setOnClickListener(button_click);
    scanBtn.setOnClickListener(button_click);


        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        txtTagContent = (TextView)findViewById(R.id.txtTagContent);
    }


    @Override
    protected void onResume() {
        super.onResume();

        enableForegroundDispatchSystem();
    }

    @Override
    protected void onPause() {
        super.onPause();

        disableForegroundDispatchSystem();
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent.hasExtra(NfcAdapter.EXTRA_TAG)) {
            Toast.makeText(this, "NfcIntent!", Toast.LENGTH_SHORT).show();


                Parcelable[] parcelables = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

                if(parcelables != null && parcelables.length > 0)
                {
                    readTextFromMessage((NdefMessage) parcelables[0]);
                }else{
                    Toast.makeText(this, "No NDEF messages found!", Toast.LENGTH_SHORT).show();
                }



        }
    }

    private void readTextFromMessage(NdefMessage ndefMessage) {

        NdefRecord[] ndefRecords = ndefMessage.getRecords();

        if(ndefRecords != null && ndefRecords.length>0){

            NdefRecord ndefRecord = ndefRecords[0];

            String tagContent = getTextFromNdefRecord(ndefRecord);

            txtTagContent.setText(tagContent);

        }else
        {
            Toast.makeText(this, "No NDEF records found!", Toast.LENGTH_SHORT).show();
        }

    }






    private void enableForegroundDispatchSystem() {

        Intent intent = new Intent(this, AppActivity.class).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        IntentFilter[] intentFilters = new IntentFilter[]{};

        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null);
    }

    private void disableForegroundDispatchSystem() {
        nfcAdapter.disableForegroundDispatch(this);
    }

    private void formatTag(Tag tag, NdefMessage ndefMessage) {
        try {

            NdefFormatable ndefFormatable = NdefFormatable.get(tag);

            if (ndefFormatable == null) {
                Toast.makeText(this, "Tag is not ndef formatable!", Toast.LENGTH_SHORT).show();
                return;
            }


            ndefFormatable.connect();
            ndefFormatable.format(ndefMessage);
            ndefFormatable.close();

            Toast.makeText(this, "Tag writen!", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Log.e("formatTag", e.getMessage());
        }

    }

    private void writeNdefMessage(Tag tag, NdefMessage ndefMessage) {

        try {

            if (tag == null) {
                Toast.makeText(this, "Tag object cannot be null", Toast.LENGTH_SHORT).show();
                return;
            }

            Ndef ndef = Ndef.get(tag);

            if (ndef == null) {
                // format tag with the ndef format and writes the message.
                formatTag(tag, ndefMessage);
            } else {
                ndef.connect();

                if (!ndef.isWritable()) {
                    Toast.makeText(this, "Tag is not writable!", Toast.LENGTH_SHORT).show();

                    ndef.close();
                    return;
                }

                ndef.writeNdefMessage(ndefMessage);
                ndef.close();

                Toast.makeText(this, "Tag writen!", Toast.LENGTH_SHORT).show();

            }

        } catch (Exception e) {
            Log.e("writeNdefMessage", e.getMessage());
        }

    }


    private NdefRecord createTextRecord(String content) {
        try {
            byte[] language;
            language = Locale.getDefault().getLanguage().getBytes("UTF-8");

            final byte[] text = content.getBytes("UTF-8");
            final int languageSize = language.length;
            final int textLength = text.length;
            final ByteArrayOutputStream payload = new ByteArrayOutputStream(1 + languageSize + textLength);

            payload.write((byte) (languageSize & 0x1F));
            payload.write(language, 0, languageSize);
            payload.write(text, 0, textLength);

            return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], payload.toByteArray());

        } catch (UnsupportedEncodingException e) {
            Log.e("createTextRecord", e.getMessage());
        }
        return null;
    }


    private NdefMessage createNdefMessage(String content) {

        NdefRecord ndefRecord = createTextRecord(content);

        NdefMessage ndefMessage = new NdefMessage(new NdefRecord[]{ndefRecord});

        return ndefMessage;
    }


    public void tglReadWriteOnClick(View view){
        txtTagContent.setText("");
    }


    public String getTextFromNdefRecord(NdefRecord ndefRecord)
    {
        String tagContent = null;
        try {
            byte[] payload = ndefRecord.getPayload();
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
            int languageSize = payload[0] & 0063;
            tagContent = new String(payload, languageSize + 1,
                    payload.length - languageSize - 1, textEncoding);
        } catch (UnsupportedEncodingException e) {
            Log.e("getTextFromNdefRecord", e.getMessage(), e);
        }
        return tagContent;
    }


public View.OnClickListener button_click = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cartBtn:
                title.setText("Indkøbskurv");
                flippy.setDisplayedChild(0);
                break;
            case R.id.favBtn:
                title.setText("Favoritter");
                flippy.setDisplayedChild(1);
                break;
            case R.id.histBtn:
                title.setText("Historik");
                flippy.setDisplayedChild(2);
                break;
            case R.id.profileButton:
                title.setText("Konto");
                flippy.setDisplayedChild(3);
                break;
            case R.id.depositMoneyBtn:
                account.balance =  account.balance + Float.parseFloat(depositAmount.getText().toString());
                Toast.makeText(getApplicationContext(), "Du har indsat " + depositAmount.getText().toString() + "kr", Toast.LENGTH_SHORT).show();
                depositAmount.setText("");
                balanceTV.setText("" + account.balance);
                balanceTV1.setText("" + account.balance);
                break;


        }
    }
};



}
