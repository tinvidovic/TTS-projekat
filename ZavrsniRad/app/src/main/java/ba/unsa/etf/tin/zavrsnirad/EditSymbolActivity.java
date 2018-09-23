package ba.unsa.etf.tin.zavrsnirad;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

public class EditSymbolActivity extends AppCompatActivity {

    private Symbol mSymbol;
    private EditText mEditTextSymbolText;
    private int mOdabraniTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_symbol);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DatabaseHelper db = new DatabaseHelper(getApplicationContext());

        List<Symbol> symbols = db.getSymbols();

        readDisplayStateValues();

        final ImageView imageSymbolImage = (ImageView) findViewById(R.id.imageView_EditSymbolSlika);

        final TextView textSymbolName = (TextView) findViewById(R.id.textView_EditSymbolName);

        mEditTextSymbolText = (EditText) findViewById(R.id.editText_EditSymbolText);

        int idSlike = this.getResources().getIdentifier(mSymbol.getImagePath(),  "drawable", this.getPackageName());

        try
        {
            FileInputStream fis = new FileInputStream (new File(mSymbol.getImagePath()));
            imageSymbolImage.setImageBitmap(BitmapFactory.decodeFile(mSymbol.getImagePath()));

        }catch (FileNotFoundException e)
        {
            imageSymbolImage.setImageResource(idSlike);
            Log.d("nepostojeciFile", "Exception 42. Ne postoji " + mSymbol.getImagePath());
        }

        textSymbolName.setText(mSymbol.getName());

        mEditTextSymbolText.setText(mSymbol.getText());

        final ImageButton imageButtonRemoveButton = (ImageButton) findViewById(R.id.imageButton_RemoveButton);

        imageButtonRemoveButton.setOnClickListener(new AdapterView.OnClickListener()
        {

            @Override
            public void onClick(View v) {
                if(mOdabraniTab == 1)
                mSymbol.setmIsSelectedTab1(false);
                if(mOdabraniTab == 2)
                    mSymbol.setmIsSelectedTab2(false);
                if(mOdabraniTab == 3)
                    mSymbol.setmIsSelectedTab3(false);
                if(mOdabraniTab == 4)
                    mSymbol.setmIsSelectedTab4(false);

                DatabaseHelper db = new DatabaseHelper(getApplicationContext());

                db.toggleSymbolIsSelected(mSymbol, mOdabraniTab);

                Toast.makeText( getApplicationContext() , Constants.SYMBOL_REMOVED_TOAST_MESSAGE , Toast.LENGTH_SHORT).show();

                db.close();

                finish();
            }
        });

        final ImageButton imageButtonConfirmButton = (ImageButton) findViewById(R.id.imageButton_ConfirmButton);

        imageButtonConfirmButton.setOnClickListener(new AdapterView.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText( getApplicationContext() , Constants.SYMBOL_SAVED_TOAST_MESSAGE , Toast.LENGTH_SHORT).show();

                saveSymbol();

            }
        });


    }

    private void readDisplayStateValues() {

        Intent intent = getIntent();

        int position = intent.getIntExtra(Constants.SYMBOL_POSITION, Constants.POSITION_NOT_SET);

        mOdabraniTab = intent.getIntExtra(Constants.STATE_ODABRANI_TAB, 0);

        if(position == Constants.POSITION_NOT_SET)
        {
            // TODO: Ukoliko dodje do greske ( nije odabran nijedan simbol)
        }
        else
        {
            DatabaseHelper db = new DatabaseHelper(getApplicationContext());

            mSymbol = db.getSelectedSymbolsForTab(mOdabraniTab).get(position);
        }

    }

    @Override
    protected void onPause() {

        super.onPause();

    }

    private void saveSymbol() {

        mSymbol.setText(mEditTextSymbolText.getText().toString());

        DatabaseHelper db = new DatabaseHelper(getApplicationContext());

        db.updateSymbolText(mSymbol);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id==android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
