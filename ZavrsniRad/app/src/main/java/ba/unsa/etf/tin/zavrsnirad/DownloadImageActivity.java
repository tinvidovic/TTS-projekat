package ba.unsa.etf.tin.zavrsnirad;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class DownloadImageActivity extends AppCompatActivity {

    private EditText mEditTextURL;
    private EditText mEditTextIme;
    private EditText mEditTextTekst;
    private ArrayList<Symbol> mAllSymbols;
    private Boolean mDaLiJeDohvaćanjeNedostajućih;
    private String mImeNedostajucegSimbola;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_image);
        mEditTextURL = (EditText) findViewById(R.id.editText_URL);
        mEditTextIme = (EditText) findViewById(R.id.editText_ImeSimbola);
        mEditTextTekst = (EditText) findViewById(R.id.editText_TekstSimbola);

        mAllSymbols = getIntent().getParcelableArrayListExtra(Constants.ALL_SYMBOLS);
        Button dugmeDownloadFromURL = (Button) findViewById(R.id.button_downloadFromURL);

        dugmeDownloadFromURL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean connected = false;

                if(isNetworkAvailable())
                {
                    if(!mEditTextIme.getText().toString().equals("") && !mEditTextTekst.getText().toString().equals("") && !mEditTextURL.getText().toString().equals("")) {

                        mDaLiJeDohvaćanjeNedostajućih = false;
                        new DownloadImage().execute(mEditTextURL.getText().toString());
                        connected = true;

                    }
                    else
                    {

                        Toast.makeText( getApplicationContext() , "Molimo popunite potrebna polja!" , Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    Toast.makeText( getApplicationContext() , "Molimo povežite se na mrežu!" , Toast.LENGTH_LONG).show();
                    connected = false;
                }


            }
        });

        Button dugmeDownloadMissingImages = (Button) findViewById(R.id.button_downloadMissingImages);

        dugmeDownloadMissingImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean connected = false;

                if(isNetworkAvailable())
                {
                    for(int index = 0; index < mAllSymbols.size(); index++)
                    {
                        if(!mAllSymbols.get(index).getImageURL().equals(""))
                        {



                            if(!mAllSymbols.get(index).getImageURL().equals(""))
                            {
                                try
                                {
                                    FileInputStream fis = new FileInputStream (new File(mAllSymbols.get(index).getImagePath()));


                                }catch (FileNotFoundException e)
                                {
                                    mDaLiJeDohvaćanjeNedostajućih = true;
                                    mImeNedostajucegSimbola = mAllSymbols.get(index).getName();
                                    new DownloadImage().execute(mAllSymbols.get(index).getImageURL());

                                    e.printStackTrace();
                                }
                            }

                        }

                    }
                }
                else
                {
                    Toast.makeText( getApplicationContext() , "Molimo povežite se na mrežu!" , Toast.LENGTH_LONG).show();
                    connected = false;
                }


            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void saveImage(Context context, Bitmap b, String imageName) {
        FileOutputStream foStream;
        try {
            foStream = context.openFileOutput(imageName, Context.MODE_PRIVATE);
            b.compress(Bitmap.CompressFormat.PNG, 100, foStream);
            foStream.close();
        } catch (Exception e) {
            Log.d("saveImage", "Exception 2, Something went wrong!");
            e.printStackTrace();
        }
    }



    private class DownloadImage extends AsyncTask<String, Void, Bitmap> {
        private String TAG = "DownloadImage";
        private Bitmap downloadImageBitmap(String sUrl) {
            Bitmap bitmap = null;
            try {
                InputStream inputStream = new URL(sUrl).openStream();   // Download Image from URL
                bitmap = BitmapFactory.decodeStream(inputStream);       // Decode Bitmap
                inputStream.close();
            } catch (Exception e) {
                Log.d(TAG, "Exception 1, Something went wrong!");
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            return downloadImageBitmap(params[0]);
        }

        protected void onPostExecute(Bitmap result) {


            if(mDaLiJeDohvaćanjeNedostajućih)
            {
                if(result != null)
                {
                    saveImage(getApplicationContext(), result, mImeNedostajucegSimbola + ".jpeg");

                    Toast.makeText( getApplicationContext() , "Simbol " + mImeNedostajucegSimbola + " učitan!" , Toast.LENGTH_LONG).show();

                }
                else
                {
                    Toast.makeText( getApplicationContext() , "Neuspješno učitavanje slike!" , Toast.LENGTH_LONG).show();
                }
            }
            else
            {
                File file = getApplicationContext().getFileStreamPath(mEditTextIme.getText().toString()+".jpeg");

                final DatabaseHelper db = new DatabaseHelper(getApplicationContext());

                String imageFullPath = file.getAbsolutePath();

                imageFullPath = file.getPath();

                Log.d(TAG, imageFullPath);

                Symbol noviSimbol = new Symbol(mEditTextIme.getText().toString(), mEditTextTekst.getText().toString(), imageFullPath, mEditTextURL.getText().toString());

                if (file.exists())
                {
                    Toast.makeText( getApplicationContext() , "Simbol sa ovim nazivom već postoji!" , Toast.LENGTH_LONG).show();

                    db.deleteSymbol(noviSimbol);
                }


                if(result != null)
                    {
                        saveImage(getApplicationContext(), result, mEditTextIme.getText().toString() + ".jpeg");

                        db.createSymbol(noviSimbol);

                        db.close();

                        finish();
                    }
                    else
                    {
                        Toast.makeText( getApplicationContext() , "Neuspješno učitavanje slike!" , Toast.LENGTH_LONG).show();
                    }


            }

            mDaLiJeDohvaćanjeNedostajućih = false;

        }
    }

}

