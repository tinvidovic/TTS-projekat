package ba.unsa.etf.tin.zavrsnirad;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class AddRemoveSymbolsActivity extends AppCompatActivity {

    private SymbolAdapter mAdapterSymbols;

    private ArrayList<Symbol> mAllSymbols;

    private ArrayList<Symbol> mSelectedSymbols = new ArrayList<Symbol>();
    private int mOdabraniTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_remove_symbols);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        readDisplayStateValues();

        initializeDisplayContent();

        FloatingActionButton fab_download_image = (FloatingActionButton) findViewById(R.id.fab_download_image);

        fab_download_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(AddRemoveSymbolsActivity.this , DownloadImageActivity.class);

                intent.putParcelableArrayListExtra(Constants.ALL_SYMBOLS, mAllSymbols);

                startActivity(intent);

            }
        });

        fab_download_image.setSize(FloatingActionButton.SIZE_NORMAL);
    }


    private void initializeDisplayContent() {

        final DatabaseHelper db = new DatabaseHelper(getApplicationContext());

        mAllSymbols = db.getSymbols();

        final ListView listAllSymbols = (ListView) findViewById(R.id.listView_AddRemoveSymbols);

        mAdapterSymbols = new SymbolAdapter( this , R.layout.symbol_element, mAllSymbols);

        listAllSymbols.setAdapter(mAdapterSymbols);

        listAllSymbols.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Symbol PickedSymbol = mAllSymbols.get(position);

                if(!mSelectedSymbols.contains(PickedSymbol)) {

                    if(mOdabraniTab == 1)
                    PickedSymbol.setmIsSelectedTab1(true);
                    if(mOdabraniTab == 2)
                        PickedSymbol.setmIsSelectedTab2(true);
                    if(mOdabraniTab == 3)
                        PickedSymbol.setmIsSelectedTab3(true);
                    if(mOdabraniTab == 4)
                        PickedSymbol.setmIsSelectedTab4(true);

                    db.toggleSymbolIsSelected(PickedSymbol, mOdabraniTab);

                    Toast.makeText( getApplicationContext() , Constants.SYMBOL_ADDED_TOAST_MESSAGE , Toast.LENGTH_SHORT).show();

                    mSelectedSymbols.add(PickedSymbol);
                }

                else
                {
                    Toast.makeText( getApplicationContext() , Constants.SYMBOL_ALREADY_ADDED_TOAST_MESSAGE , Toast.LENGTH_SHORT).show();
                }

            }
        });

        listAllSymbols.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                File file = getApplicationContext().getFileStreamPath(mAllSymbols.get(i).getName().toString()+".jpeg");
                if (file.delete()) Log.d("file", "my_image.jpeg deleted!");
                DatabaseHelper db = new DatabaseHelper(getApplicationContext());
                db.deleteSymbol(mAllSymbols.get(i));

                Toast.makeText( getApplicationContext() , "Simbol je obrisan" , Toast.LENGTH_SHORT).show();

                initializeDisplayContent();
                return true;
            }
        });
    }

    private void readDisplayStateValues() {

        Intent intent = getIntent();

        mOdabraniTab = intent.getIntExtra(Constants.STATE_ODABRANI_TAB, 0);
        mSelectedSymbols = MainActivity.getmSelectedSymbols();
        Log.d("TAG", "kako mamice");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id==android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {

        super.onResume();

        initializeDisplayContent();

    }


}
