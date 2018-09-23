package ba.unsa.etf.tin.zavrsnirad;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    static final String STATE_TRENUTNI_GENERISANI_TEKST = "trenutniGenerisaniTekst";
    static final String STATE_TEKST_ZA_TTS = "tekstZaTTS";

    private static int mOdabraniTab = 1;

    private ArrayList<String> mTrenutniGenerisaniTekst = new ArrayList<String>();
    private String mTextZaTTS = "";

    private TextView mTekstZaIzgovor;
    private boolean mAdministratorskiRežimRada;

    public static ArrayList<Symbol> getmSelectedSymbols() {
        return mSelectedSymbols;
    }

    private static ArrayList<Symbol> mSelectedSymbols = new ArrayList<>();

    public static ArrayList<Symbol> mAllSymbols = new ArrayList<>();
    private SymbolAdapter mAdapterSymbols;
    private boolean mIsLongClick = false;
    private String mStariTekstSimbola;
    private int mStaraPozicijaSimbola;
    private boolean mIzgovorNakonUnosaSimbola = false;
    private boolean mIzgovorNakonVremenskogPerioda = false;
    private boolean mIzgovorNakonPritiskaNaDugme = false;
    private int mVrijemeCekanjaPrijeIzgovora = 0;

    private ImageView mSlikaSimbola;
    private GridView mGridView;


    private static TextToSpeech mTTS;
    private int result;
    private Button mTab1;
    private Button mTab2;
    private Button mTab3;
    private Button mTab4;

    private static Context mContext;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mTTS != null)
        {
            mTTS.stop();
            mTTS.shutdown();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mTekstZaIzgovor = (TextView) findViewById(R.id.textView_GeneratedText);

        if (savedInstanceState != null) {

            mTextZaTTS = savedInstanceState.getString(STATE_TEKST_ZA_TTS);
            mTrenutniGenerisaniTekst = savedInstanceState.getStringArrayList(STATE_TRENUTNI_GENERISANI_TEKST);
            mOdabraniTab = savedInstanceState.getInt(Constants.STATE_ODABRANI_TAB);
        }
        else
        {

            mTrenutniGenerisaniTekst.add(mTekstZaIzgovor.getText().toString());
        }


        Button fabAdd = (Button) findViewById(R.id.fab_add);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this , AddRemoveSymbolsActivity.class);
                populateSelectedSymbolsList();
                intent.putExtra(Constants.STATE_ODABRANI_TAB, mOdabraniTab);

                intent.putParcelableArrayListExtra(Constants.SELECTED_SYMBOLS, mSelectedSymbols);

                startActivity(intent);

            }
        });



        Button fabSpeak = (Button) findViewById(R.id.fab_speak);
        fabSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(mIzgovorNakonPritiskaNaDugme) {

                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(getApplicationContext(), "Jezik nije podrzan", Toast.LENGTH_LONG).show();
                    } else {

                        mTTS.speak(mTextZaTTS, TextToSpeech.QUEUE_FLUSH, null);
                    }
                }
            }
        });

        Button fabClear = (Button) findViewById(R.id.fab_clear_text);
        fabClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                mTrenutniGenerisaniTekst.clear();

                pretvoriTrenutniGenerisaniTekstUTekstZaIzgovor();


            }
        });

        mTab1 = (Button) findViewById(R.id.tab1);
        mTab2 = (Button) findViewById(R.id.tab2);
        mTab3 = (Button) findViewById(R.id.tab3);
        mTab4 = (Button) findViewById(R.id.tab4);

        mTab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOdabraniTab = 1;
                handleTabs();

                mTrenutniGenerisaniTekst.clear();
                mTextZaTTS = "";
                mTekstZaIzgovor.setText("");
            }
        });

        mTab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOdabraniTab = 2;
                handleTabs();

                mTrenutniGenerisaniTekst.clear();
                mTextZaTTS = "";
                mTekstZaIzgovor.setText("");
            }
        });

        mTab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOdabraniTab = 3;
                handleTabs();

                mTrenutniGenerisaniTekst.clear();
                mTextZaTTS = "";
                mTekstZaIzgovor.setText("");
            }
        });

        mTab4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOdabraniTab = 4;
                handleTabs();

                mTrenutniGenerisaniTekst.clear();
                mTextZaTTS = "";
                mTekstZaIzgovor.setText("");
            }
        });

        configureTextToSpeechSettings();

        configureGeneralSettings();

        initializeDisplayContent();

        handleTabs();

        mTTS = new TextToSpeech(MainActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS)
                {
                    result = mTTS.setLanguage(Locale.ENGLISH);
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Jezik nije podrzan", Toast.LENGTH_LONG).show();
                }
            }
        });

    }



    private void handleTabs() {

        if(mOdabraniTab == 1)
        {
            mTab1.setBackgroundColor(getResources().getColor(R.color.colorTabHighlight));
            mTab2.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            mTab3.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            mTab4.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }
        else if(mOdabraniTab == 2)
        {
            mTab2.setBackgroundColor(getResources().getColor(R.color.colorTabHighlight));
            mTab1.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            mTab3.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            mTab4.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }
        else if(mOdabraniTab == 3)
        {
            mTab3.setBackgroundColor(getResources().getColor(R.color.colorTabHighlight));
            mTab2.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            mTab1.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            mTab4.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }
        else if(mOdabraniTab == 4)
        {
            mTab4.setBackgroundColor(getResources().getColor(R.color.colorTabHighlight));
            mTab2.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            mTab3.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            mTab1.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }

        populateSelectedSymbolsList();

        //pretvoriTrenutniGenerisaniTekstUTekstZaIzgovor();

        mAdapterSymbols.notifyDataSetChanged();

    }

    private void configureGeneralSettings() {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);



        Button fabAdd = (Button) findViewById(R.id.fab_add);

        if(mAdministratorskiRežimRada)
        {
            fabAdd.setVisibility(View.VISIBLE);
        }
        else
        {
            fabAdd.setVisibility(View.INVISIBLE);
        }

    }

    private void configureTextToSpeechSettings() {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        String textToSpeechSettingIndex = sharedPref.getString(SettingsActivity.KEY_PREF_TEXT_TO_SPEECH, "");

        //Resetuj vrijednosti
        mIzgovorNakonUnosaSimbola = false;
        mIzgovorNakonVremenskogPerioda = false;
        mIzgovorNakonPritiskaNaDugme = false;



        if(textToSpeechSettingIndex.equals(Constants.IZGOVOR_NAKON_UNOSA_SIMBOLA )) mIzgovorNakonUnosaSimbola = true;
        else if(textToSpeechSettingIndex.equals(Constants.IZGOVOR_NAKON_VREMENSKOG_PERIODA))
        {
            mIzgovorNakonVremenskogPerioda = true;

            String textToSpeechDelayTimeSettingIndex = sharedPref.getString(SettingsActivity.KEY_PREF_TEXT_TO_SPEECH_DELAY_TIME, "");

            if(textToSpeechDelayTimeSettingIndex.equals(Constants.POLA_SEKUNDE))
            {
                mVrijemeCekanjaPrijeIzgovora = 500;
            }
            else if(textToSpeechDelayTimeSettingIndex.equals(Constants.JEDNA_SEKUNDA))
            {
                mVrijemeCekanjaPrijeIzgovora = 1000;
            }
            else if(textToSpeechDelayTimeSettingIndex.equals(Constants.DVIJE_SEKUNDE))
            {
                mVrijemeCekanjaPrijeIzgovora = 2000;
            }
            else if(textToSpeechDelayTimeSettingIndex.equals(Constants.PET_SEKUNDI))
            {
                mVrijemeCekanjaPrijeIzgovora = 5000;
            }
        }
        else if(textToSpeechSettingIndex.equals(Constants.IZGOVOR_NAKON_PRITISKA_NA_DUGME)) mIzgovorNakonPritiskaNaDugme = true;

    }


    private void initializeDisplayContent() {



        pretvoriTrenutniGenerisaniTekstUTekstZaIzgovor();

        mIsLongClick = false;

        mGridView = (GridView) findViewById(R.id.listView_symbols);

        DatabaseHelper db = new DatabaseHelper(getApplicationContext());

        mAllSymbols = db.getSymbols();

        populateSelectedSymbolsList();

        mAdapterSymbols = new SymbolAdapter( this , R.layout.symbol_element, mSelectedSymbols);

        mGridView.setAdapter(mAdapterSymbols);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(!mIsLongClick) {

                    mTrenutniGenerisaniTekst.add(mSelectedSymbols.get(position).getText());

                    pretvoriTrenutniGenerisaniTekstUTekstZaIzgovor();


                    if(mIzgovorNakonUnosaSimbola)
                    {
                        mTTS.speak(mTrenutniGenerisaniTekst.get(mTrenutniGenerisaniTekst.size() - 1), TextToSpeech.QUEUE_ADD, null);
                    }

                    if(mIzgovorNakonVremenskogPerioda)
                    {
                        Handler handler = new Handler();
                        final String posljednjiTekstUTrenutkuOdabira = mTrenutniGenerisaniTekst.get(mTrenutniGenerisaniTekst.size() - 1);
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                mTTS.speak(posljednjiTekstUTrenutkuOdabira, TextToSpeech.QUEUE_ADD, null);
                            }
                        }, mVrijemeCekanjaPrijeIzgovora);
                    }
                }

                mIsLongClick = false;
            }
        });

        mGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                mIsLongClick = true;

                if(mAdministratorskiRežimRada) {

                    mStariTekstSimbola = mSelectedSymbols.get(position).getText();

                    mStaraPozicijaSimbola = position;

                    Intent intent = new Intent(MainActivity.this, EditSymbolActivity.class);

                    intent.putExtra(Constants.SYMBOL_POSITION, position);
                    intent.putExtra(Constants.STATE_ODABRANI_TAB, mOdabraniTab);

                    startActivity(intent);
                }


                return false;
            }
        });

        postaviBrojKolonaGridViewa();
    }

    private void postaviBrojKolonaGridViewa() {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        String imageSizeSettingIndex = sharedPref.getString(SettingsActivity.KEY_PREF_SYMBOL_IMAGE_SIZE, "");

        if (imageSizeSettingIndex.equals(Constants.pedesetPiksela)) {

            mGridView.setNumColumns(4);


        } else if (imageSizeSettingIndex.equals(Constants.stotinuPiksela)) {

            mGridView.setNumColumns(3);

        } else if (imageSizeSettingIndex.equals(Constants.stotinuPedesetPiksela)) {

            mGridView.setNumColumns(3);

        } else if(imageSizeSettingIndex.equals(Constants.dvijeStotinePedesetPiksela))
        {

            mGridView.setNumColumns(3);

        } else if(imageSizeSettingIndex.equals(Constants.triStotinePedesetPiksela))
        {

            mGridView.setNumColumns(2);

        } else if(imageSizeSettingIndex.equals(Constants.petStotinaPiksela))
        {

            mGridView.setNumColumns(2);

        }else if(imageSizeSettingIndex.equals(Constants.sedamStodinaPedesetPiksela))
        {

            mGridView.setNumColumns(1);

        } else if(imageSizeSettingIndex.equals(Constants.hiljaduPiksela))
        {

            mGridView.setNumColumns(1);

        }

    }

    private void postaviVeličinuDugmadi() {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        String buttonSizeSettingIndex = sharedPref.getString(SettingsActivity.KEY_PREF_BUTTON_SIZE, "");

        Button dugmeAdd = (Button) findViewById(R.id.fab_add);
        Button dugmeSpeak = (Button) findViewById(R.id.fab_speak);
        Button dugmeClearText = (Button) findViewById(R.id.fab_clear_text);



        if(buttonSizeSettingIndex.equals("-1"))
        {
            dugmeAdd.setWidth(  (int) ((getApplicationContext().getResources().getConfiguration().screenWidthDp)/ (3.4))   * 2);
            dugmeSpeak.setWidth(  (int) ((getApplicationContext().getResources().getConfiguration().screenWidthDp)/ (3.4)) * 2 );
            dugmeClearText.setWidth(  (int) ((getApplicationContext().getResources().getConfiguration().screenWidthDp)/ (3.4)) * 2 );

            dugmeAdd.setHeight(  (int) ((getApplicationContext().getResources().getConfiguration().screenWidthDp)/ (4)) * 2 );
            dugmeSpeak.setHeight(  (int) ((getApplicationContext().getResources().getConfiguration().screenWidthDp)/ (4)) * 2 );
            dugmeClearText.setHeight(  (int) ((getApplicationContext().getResources().getConfiguration().screenWidthDp)/ (4)) * 2 );
        }
        if(buttonSizeSettingIndex.equals("0"))
        {
            dugmeAdd.setWidth(  (int) ((getApplicationContext().getResources().getConfiguration().screenWidthDp)/ (3.5)) * 2 );
            dugmeSpeak.setWidth(  (int) ((getApplicationContext().getResources().getConfiguration().screenWidthDp)/ (3.5)) * 2 );
            dugmeClearText.setWidth(  (int) ((getApplicationContext().getResources().getConfiguration().screenWidthDp)/ (3.5)) * 2 );

            dugmeAdd.setHeight(  (int) ((getApplicationContext().getResources().getConfiguration().screenWidthDp)/ (5)) * 2 );
            dugmeSpeak.setHeight(  (int) ((getApplicationContext().getResources().getConfiguration().screenWidthDp)/ (5))* 2  );
            dugmeClearText.setHeight(  (int) ((getApplicationContext().getResources().getConfiguration().screenWidthDp)/ (5)) * 2 );
        }
        if(buttonSizeSettingIndex.equals("1"))
        {
            dugmeAdd.setWidth(  (int) ((getApplicationContext().getResources().getConfiguration().screenWidthDp)/ (4)) * 2 );
            dugmeSpeak.setWidth(  (int) ((getApplicationContext().getResources().getConfiguration().screenWidthDp)/ (4)) * 2 );
            dugmeClearText.setWidth(  (int) ((getApplicationContext().getResources().getConfiguration().screenWidthDp)/ (4))* 2  );

            dugmeAdd.setHeight(  (int) ((getApplicationContext().getResources().getConfiguration().screenWidthDp)/ (6))* 2  );
            dugmeSpeak.setHeight(  (int) ((getApplicationContext().getResources().getConfiguration().screenWidthDp)/ (6))* 2  );
            dugmeClearText.setHeight(  (int) ((getApplicationContext().getResources().getConfiguration().screenWidthDp)/ (6))* 2  );
        }
        if(buttonSizeSettingIndex.equals("2"))
        {
            dugmeAdd.setWidth(  (int) ((getApplicationContext().getResources().getConfiguration().screenWidthDp)/ (5))  );
            dugmeSpeak.setWidth(  (int) ((getApplicationContext().getResources().getConfiguration().screenWidthDp)/ (5))  );
            dugmeClearText.setWidth(  (int) ((getApplicationContext().getResources().getConfiguration().screenWidthDp)/ (5))  );

            dugmeAdd.setHeight(  (int) ((getApplicationContext().getResources().getConfiguration().screenWidthDp)/ (6.5))  );
            dugmeSpeak.setHeight(  (int) ((getApplicationContext().getResources().getConfiguration().screenWidthDp)/ (6.5))  );
            dugmeClearText.setHeight(  (int) ((getApplicationContext().getResources().getConfiguration().screenWidthDp)/ (6.5))  );
        }
    }

    private void pretvoriTrenutniGenerisaniTekstUTekstZaIzgovor() {

        String noviGenerisaniTekst = "";

        for(int index = 0; index < mTrenutniGenerisaniTekst.size(); index++)
        {
            noviGenerisaniTekst += " " + mTrenutniGenerisaniTekst.get(index);
        }

        // TODO: popravi ovaj hardcoded value
        if(noviGenerisaniTekst.equals(" Kliknite na simbol"))
        {
            noviGenerisaniTekst = "";
            mTrenutniGenerisaniTekst.clear();
        }

        mTekstZaIzgovor.setText(noviGenerisaniTekst);

        mTextZaTTS = noviGenerisaniTekst;
    }

    private void populateSelectedSymbolsList() {

        mSelectedSymbols.clear();

        DatabaseHelper db = new DatabaseHelper(getApplicationContext());

        mAllSymbols = db.getSymbols();

        for( int currentSymbol = 0; currentSymbol < mAllSymbols.size(); currentSymbol++)
        {

            Symbol simbol = mAllSymbols.get(currentSymbol);

            if(mAllSymbols.get(currentSymbol).getIsSelectedTab1() && mOdabraniTab == 1) mSelectedSymbols.add(mAllSymbols.get(currentSymbol));
            if(mAllSymbols.get(currentSymbol).getIsSelectedTab2() && mOdabraniTab == 2) mSelectedSymbols.add(mAllSymbols.get(currentSymbol));

            if(mAllSymbols.get(currentSymbol).getIsSelectedTab3() && mOdabraniTab == 3) mSelectedSymbols.add(mAllSymbols.get(currentSymbol));
            if(mAllSymbols.get(currentSymbol).getIsSelectedTab4() && mOdabraniTab == 4) mSelectedSymbols.add(mAllSymbols.get(currentSymbol));

        }

    }

    private void zamjeniStariTekstSimbolaNovim() {

        for(int index = 0; index < mTrenutniGenerisaniTekst.size(); index++)
        {
            if(mTrenutniGenerisaniTekst.get(index).equals(mStariTekstSimbola))
            {
                if(mAllSymbols.get(mStaraPozicijaSimbola).getIsSelectedTab1() && mOdabraniTab == 1)
                {
                    mTrenutniGenerisaniTekst.set(index, mAllSymbols.get(mStaraPozicijaSimbola).getText());
                }
                else if(mAllSymbols.get(mStaraPozicijaSimbola).getIsSelectedTab2() && mOdabraniTab == 2)
                {
                    mTrenutniGenerisaniTekst.set(index, mAllSymbols.get(mStaraPozicijaSimbola).getText());
                }
                else if(mAllSymbols.get(mStaraPozicijaSimbola).getIsSelectedTab3() && mOdabraniTab == 3)
                {
                    mTrenutniGenerisaniTekst.set(index, mAllSymbols.get(mStaraPozicijaSimbola).getText());
                }
                else if(mAllSymbols.get(mStaraPozicijaSimbola).getIsSelectedTab4() && mOdabraniTab == 4)
                {
                    mTrenutniGenerisaniTekst.set(index, mAllSymbols.get(mStaraPozicijaSimbola).getText());
                }
                else
                {
                    mTrenutniGenerisaniTekst.remove(index);
                }
            }
        }

        pretvoriTrenutniGenerisaniTekstUTekstZaIzgovor();

    }


    @Override
    protected void onPause() {
        super.onPause();
        mTTS.stop();
    }

    @Override
    protected void onResume() {

        super.onResume();

        mContext = getApplicationContext();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        mAdministratorskiRežimRada = sharedPref.getBoolean(SettingsActivity.KEY_PREF_ADMIN_MODE, true);

        configureTextToSpeechSettings();

        configureGeneralSettings();

        mIsLongClick = false;

        zamjeniStariTekstSimbolaNovim();

        populateSelectedSymbolsList();

        mAdapterSymbols.notifyDataSetChanged();

        postaviBrojKolonaGridViewa();

        postaviVeličinuDugmadi();

        handleTabs();


    }




    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(STATE_TEKST_ZA_TTS, mTextZaTTS);
        outState.putStringArrayList(STATE_TRENUTNI_GENERISANI_TEKST, mTrenutniGenerisaniTekst);
        outState.putInt(Constants.STATE_ODABRANI_TAB, mOdabraniTab);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            Intent intent = new Intent(MainActivity.this , SettingsActivity.class);

            //intent.putParcelableArrayListExtra(AddRemoveSymbolsActivity.SELECTED_SYMBOLS, mSelectedSymbols);

            startActivity(intent);

        }

        if (id == R.id.action_share) {

            if(mAdministratorskiRežimRada)
            {
                BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (bluetoothAdapter == null) {

                    Toast.makeText(getApplicationContext(), "Bluetooth nije podržan ba vašem uređaju", Toast.LENGTH_LONG).show();
                }
                else
                {

                    if (!bluetoothAdapter.isEnabled()) {
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, Constants.REQUEST_ENABLE_BT);

                        /**
                         * REZULTAT  OBRADE
                         * @see #onAcitvityResult
                         */
                    }
                    else
                    {

                        // POKRENI SHARESETTINGSACTIVITY UKOLIKO JE BLUETOOTH UKLJUCEN
                        Intent intent = new Intent(MainActivity.this , ShareSettingsActivity.class);
                        intent.putParcelableArrayListExtra(Constants.SYMBOL_CONFIGURATION , mAllSymbols);
                        startActivity(intent);
                    }

                }
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Aktivirajte administratorski režim rada", Toast.LENGTH_LONG).show();
            }


        }

        if (id == R.id.action_help) {

            Intent intent = new Intent(MainActivity.this , HelpActivity.class);

            //intent.putParcelableArrayListExtra(AddRemoveSymbolsActivity.SELECTED_SYMBOLS, mSelectedSymbols);

            startActivity(intent);

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK)
        {
            Intent intent = new Intent(MainActivity.this , ShareSettingsActivity.class);
            intent.putParcelableArrayListExtra(Constants.SYMBOL_CONFIGURATION , mAllSymbols);
            startActivity(intent);
        }
        else if(resultCode == RESULT_CANCELED)
        {
            Toast.makeText(getApplicationContext(), "Kako bi podijelili konfiguraciju morate uključiti Bluetooth.", Toast.LENGTH_LONG).show();
        }

    }

    public static void izgovoriSimbolSaTastature(String incomingMessage) {


        Context context = mContext;
        Integer taster = 0;
        if(incomingMessage.equals("1")) taster = 0;
        else if(incomingMessage.equals("2")) taster = 1;
        else if(incomingMessage.equals("3")) taster = 2;
        else if(incomingMessage.equals("4")) taster = 3;
        else if(incomingMessage.equals("5")) taster = 4;




        if(mSelectedSymbols.size() > taster)
        {


            mTTS.speak( mSelectedSymbols.get(taster).getText(),TextToSpeech.QUEUE_ADD, null);

        }
        else
        {

            mTTS.speak( "Trebate dodati simbol.",TextToSpeech.QUEUE_FLUSH, null);
        }


    }
}
