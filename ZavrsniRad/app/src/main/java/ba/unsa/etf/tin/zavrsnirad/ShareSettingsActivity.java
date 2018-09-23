package ba.unsa.etf.tin.zavrsnirad;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class ShareSettingsActivity extends AppCompatActivity {


    private static final String TAG = "TAG";

    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
    private ArrayList<BluetoothDevice> mBluetoothDevices = new ArrayList<BluetoothDevice>();
    private ArrayList<String> mBluetoothDevicesNames = new ArrayList<String>();
    private ArrayAdapter<String> mAdapterDevices;
    private ListView mListaBluetoothUredjaja;
    private static ArrayList<Symbol> mSymbols;
    private String Configuration;
    BluetoothConnectionManager mBluetoohConnectionmanager;
    private static final UUID MY_UUID = java.util.UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    BluetoothDevice mDevice;
    private static Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mBluetoohConnectionmanager = new BluetoothConnectionManager(ShareSettingsActivity.this, this);

        mContext = getApplicationContext();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);

        Intent intent = getIntent();

        mSymbols = intent.getParcelableArrayListExtra(Constants.SYMBOL_CONFIGURATION);

        turnSymbolsIntoString();

        final Button dugmePretrazi = (Button) findViewById(R.id.button_PretraziUredjaje);

        final Button dugmeBudiVidljiv = (Button) findViewById(R.id.button_BudiVidljiv);

        final Button dugmePosaljikonfiguraciju = (Button) findViewById(R.id.button_PosaljiKonfiguraciju);



        mListaBluetoothUredjaja = (ListView) findViewById(R.id.listView_BluetoothDevices);

        initializeDisplayContent();

        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            //ODVEC UPARENI BLUETOOTH UREDJAJI
            for (BluetoothDevice device : pairedDevices) {
                mBluetoothDevices.add(device);
                mBluetoothDevicesNames.add(device.getName());
                mAdapterDevices.notifyDataSetChanged();

            }
        }

        dugmePretrazi.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View view) {

                if(mBluetoothAdapter.isDiscovering())
                {
                    mBluetoothAdapter.cancelDiscovery();
                }

                checkBTPermissions();

                mBluetoothAdapter.startDiscovery();



            }
        });

        dugmeBudiVidljiv.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View view) {

                Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                startActivity(discoverableIntent);

            }
        });

        dugmePosaljikonfiguraciju.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {


                    byte[] bytes = Configuration.getBytes(Charset.defaultCharset());
                    if (bytes == null) {

                    } else {
                        mBluetoohConnectionmanager.write(bytes);
                    }
                }catch(NullPointerException e)
                {
                    Toast.makeText(getApplicationContext(), "Molimo uparite uređaje!", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });


        mListaBluetoothUredjaja.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                mBluetoothAdapter.cancelDiscovery();
                mDevice = mBluetoothDevices.get(position);
                mBluetoothDevices.get(position).createBond();


                if(mDevice.getBondState() == mDevice.BOND_BONDED) {
                    startConnection();
                }
                Log.d("TAG", mDevice.getName());

            }
        });



    }


    private void checkBTPermissions() {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {

                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
            }
        }else{

        }
    }

    private void initializeDisplayContent() {

        mAdapterDevices = new ArrayAdapter<String>( this , android.R.layout.simple_list_item_1 , mBluetoothDevicesNames);

        mListaBluetoothUredjaja.setAdapter(mAdapterDevices);

    }

    //NEUPARENI BLUETOOTH UREDJAJI KOJI SU VIDLJIVI

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Log.d("tag", "tu smo");
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(device != null) {
                    if (!mBluetoothDevices.contains(device) && device.getName() != null) {
                        mBluetoothDevices.add(device);
                        mBluetoothDevicesNames.add(device.getName());
                        mAdapterDevices.notifyDataSetChanged();

                    }
                }

            }
        }
    };

    private void startConnection() {

        startBTConnection(mDevice, MY_UUID);

    }

    public void startBTConnection(BluetoothDevice device, UUID uuid)
    {
        Log.d("TAG", "startBTConnection " + mDevice.getName());



        mBluetoohConnectionmanager.startClient(device, uuid);


    }

    public static void toggleProgressBarVisibility()
    {

        Toast.makeText(mContext, "Uređaji upareni", Toast.LENGTH_LONG).show();

    }

    private void turnSymbolsIntoString() {

        Configuration = "";
        for(int index = 0; index < mSymbols.size(); index++)
        {

            if(mSymbols.get(index).getImageURL().equals(""))
            {
                Configuration += mSymbols.get(index).getName() + Constants.SEPERATOR2 + "Error: 404" + Constants.SEPERATOR2 + mSymbols.get(index).getImagePath() + Constants.SEPERATOR2 + mSymbols.get(index).getText() + Constants.SEPERATOR2;

            }
            else
            {
                Configuration += mSymbols.get(index).getName() + Constants.SEPERATOR2 + mSymbols.get(index).getImageURL() + Constants.SEPERATOR2 + mSymbols.get(index).getImagePath() + Constants.SEPERATOR2 + mSymbols.get(index).getText() + Constants.SEPERATOR2;

            }

            if(mSymbols.get(index).getIsSelectedTab1())
            {
                    Configuration += Constants.IS_SELECTED_FLAG + "TAB1" + Constants.SEPERATOR2;
            }

            if(mSymbols.get(index).getIsSelectedTab2())
            {
                Configuration += Constants.IS_SELECTED_FLAG + "TAB2" + Constants.SEPERATOR2;
            }

            if(mSymbols.get(index).getIsSelectedTab3())
            {
                Configuration += Constants.IS_SELECTED_FLAG + "TAB3" + Constants.SEPERATOR2;
            }

            if(mSymbols.get(index).getIsSelectedTab4())
            {
                Configuration += Constants.IS_SELECTED_FLAG + "TAB4" + Constants.SEPERATOR2;
            }

            Configuration += Constants.SEPERATOR;
        }

        Log.d("TAG", Configuration);
    }

    private static void turnStringIntoSymbols(String Configuration)
    {




        DatabaseHelper db = new DatabaseHelper(mContext);

        ArrayList<Symbol> allSymbols = db.getSymbols();
        for(int i = 0; i < allSymbols.size(); i++)
        {

            db.deleteSymbol(allSymbols.get(i));

        }

        String[] tmp = Configuration.split(Constants.SEPERATOR);

        for(int index = 0; index < tmp.length; index++)
        {
            String[] tmp2 = tmp[index].split(Constants.SEPERATOR2);
            Log.d("tmp[index]",tmp[index]);
            Symbol symbol = null;

            if(tmp2[1].equals("Error: 404"))
            {
                symbol = new Symbol(tmp2[0], tmp2[3], tmp2[2], "");
            }
            else
            {
                symbol = new Symbol(tmp2[0], tmp2[3], tmp2[2], tmp2[1]);
            }

            Log.d("TAG", symbol.getImageURL());

            db.deleteSymbol(symbol);
            db.createSymbol(symbol);


            if(tmp[index].contains(Constants.IS_SELECTED_FLAG) && tmp[index].contains("TAB1"))
            {
                db.setSymbolTab(symbol, 1);

            }else
            {
                db.unsetSymbol(symbol, 1);
            }

            if(tmp[index].contains(Constants.IS_SELECTED_FLAG) && tmp[index].contains("TAB1"))
            {
                tmp[index] = tmp[index].replace(Constants.IS_SELECTED_FLAG + "TAB1", "");
            }

            if(tmp[index].contains(Constants.IS_SELECTED_FLAG) && tmp[index].contains("TAB2"))
            {
                db.setSymbolTab(symbol, 2);

            }else
            {
                db.unsetSymbol(symbol, 2);
            }

            if(tmp[index].contains(Constants.IS_SELECTED_FLAG) && tmp[index].contains("TAB2"))
            {
                tmp[index] = tmp[index].replace(Constants.IS_SELECTED_FLAG + "TAB2", "");
            }

            if(tmp[index].contains(Constants.IS_SELECTED_FLAG) && tmp[index].contains("TAB3"))
            {
                db.setSymbolTab(symbol, 3);

            }else
            {
                db.unsetSymbol(symbol, 3);
            }

            if(tmp[index].contains(Constants.IS_SELECTED_FLAG) && tmp[index].contains("TAB3"))
            {
                tmp[index] = tmp[index].replace(Constants.IS_SELECTED_FLAG + "TAB3", "");
            }

            if(tmp[index].contains(Constants.IS_SELECTED_FLAG) && tmp[index].contains("TAB4"))
            {
                db.setSymbolTab(symbol, 4);

            }else
            {
                db.unsetSymbol(symbol, 4);
            }

            if(tmp[index].contains(Constants.IS_SELECTED_FLAG) && tmp[index].contains("TAB4"))
            {
                tmp[index] = tmp[index].replace(Constants.IS_SELECTED_FLAG + "TAB4", "");
            }
        }

        db.close();
    }

    public static void postaviKonfiguraciju(String Configuration)
    {
        turnStringIntoSymbols(Configuration);

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();

        mBluetoothAdapter.cancelDiscovery();
        // Don't forget to unregister the ACTION_FOUND receiver.

        unregisterReceiver(mReceiver);
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


