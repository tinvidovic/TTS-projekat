package ba.unsa.etf.tin.zavrsnirad;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Tin on 08.07.2018..
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "MyDB.db";
    public static final String SYMBOLS_TABLE = "Symbols";
    public static final int DATABASE_VERSION = 20;

    //kolone tabele simboli

    public static final String SYMBOL_ID = "id";
    public static final String SYMBOL_NAME = "name";
    public static final String SYMBOL_TEXT = "text";
    public static final String SYMBOL_IMAGE_PATH = "image_path";
    public static final String SYMBOL_IS_SELECTED_TAB_1 = "is_selected_tab1";
    public static final String SYMBOL_IS_SELECTED_TAB_2 = "is_selected_tab2";
    public static final String SYMBOL_IS_SELECTED_TAB_3 = "is_selected_tab3";
    public static final String SYMBOL_IS_SELECTED_TAB_4 = "is_selected_tab4";
    public static final String SYMBOL_URL = "url";


    private static final String CREATE_TABLE_SYMBOLS = "CREATE TABLE "
            + SYMBOLS_TABLE + "(" + SYMBOL_ID + " INTEGER PRIMARY KEY ,"
            + SYMBOL_NAME + " TEXT ," + SYMBOL_TEXT + " TEXT,"
            + SYMBOL_IMAGE_PATH + " TEXT," + SYMBOL_IS_SELECTED_TAB_1 + " INTEGER," + SYMBOL_IS_SELECTED_TAB_2
            + " INTEGER," + SYMBOL_IS_SELECTED_TAB_3 + " INTEGER," + SYMBOL_IS_SELECTED_TAB_4 + " INTEGER, " + SYMBOL_URL + " TEXT" + ")";


    public DatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Pozove se ukoliko baza nije kreirana

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        //kreiranje tabela;

        db.execSQL(CREATE_TABLE_SYMBOLS);

        ArrayList<Symbol> symbols = DataManager.getInstance().getSymbols();

        for(int index = 0; index < symbols.size(); index++)
        {
            ContentValues values = new ContentValues();

            values.put(SYMBOL_NAME, symbols.get(index).getName());
            values.put(SYMBOL_TEXT, symbols.get(index).getText());
            values.put(SYMBOL_IMAGE_PATH, symbols.get(index).getImagePath());
            if(symbols.get(index).getIsSelectedTab1())
            {
                values.put(SYMBOL_IS_SELECTED_TAB_1, 1);
            }
            else
            {
                values.put(SYMBOL_IS_SELECTED_TAB_1, 0);
            }
            if(symbols.get(index).getIsSelectedTab2())
            {
                values.put(SYMBOL_IS_SELECTED_TAB_2, 1);
            }
            else
            {
                values.put(SYMBOL_IS_SELECTED_TAB_2, 0);
            }
            if(symbols.get(index).getIsSelectedTab3())
            {
                values.put(SYMBOL_IS_SELECTED_TAB_3, 1);
            }
            else
            {
                values.put(SYMBOL_IS_SELECTED_TAB_3, 0);
            }
            if(symbols.get(index).getIsSelectedTab4())
            {
                values.put(SYMBOL_IS_SELECTED_TAB_4, 1);
            }
            else
            {
                values.put(SYMBOL_IS_SELECTED_TAB_4, 0);
            }

            values.put(SYMBOL_URL, symbols.get(index).getImageURL());

            long symbol_id = db.insert(SYMBOLS_TABLE, null, values);

        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + SYMBOLS_TABLE);

        onCreate(db);
    }

    // CRUD

    public long createSymbol(Symbol symbol)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(SYMBOL_NAME, symbol.getName());
        values.put(SYMBOL_TEXT, symbol.getText());
        values.put(SYMBOL_IMAGE_PATH, symbol.getImagePath());
        if(symbol.getIsSelectedTab1())
        {
            values.put(SYMBOL_IS_SELECTED_TAB_1, 1);
        }
        else
        {
            values.put(SYMBOL_IS_SELECTED_TAB_1, 0);
        }
        if(symbol.getIsSelectedTab2())
        {
            values.put(SYMBOL_IS_SELECTED_TAB_2, 1);
        }
        else
        {
            values.put(SYMBOL_IS_SELECTED_TAB_2, 0);
        }
        if(symbol.getIsSelectedTab3())
        {
            values.put(SYMBOL_IS_SELECTED_TAB_3, 1);
        }
        else
        {
            values.put(SYMBOL_IS_SELECTED_TAB_3, 0);
        }
        if(symbol.getIsSelectedTab4())
        {
            values.put(SYMBOL_IS_SELECTED_TAB_4, 1);
        }
        else
        {
            values.put(SYMBOL_IS_SELECTED_TAB_4, 0);
        }

        values.put(SYMBOL_URL, symbol.getImageURL());

        long symbol_id = db.insert(SYMBOLS_TABLE, null, values);

        return symbol_id;

    }

    public void toggleSymbolIsSelected(Symbol symbol, Integer tab)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues UpdatedValues = new ContentValues();

        int helper = 0;
        if(tab == 1) {
            if (symbol.getIsSelectedTab1()) {
                helper = 1;
            }

            UpdatedValues.put(SYMBOL_IS_SELECTED_TAB_1, helper);
        }

        if(tab == 2) {
            if (symbol.getIsSelectedTab2()) {
                helper = 1;
            }

            UpdatedValues.put(SYMBOL_IS_SELECTED_TAB_2, helper);
        }

        if(tab == 3) {
            if (symbol.getIsSelectedTab3()) {
                helper = 1;
            }

            UpdatedValues.put(SYMBOL_IS_SELECTED_TAB_3, helper);
        }

        if(tab == 4) {
            if (symbol.getIsSelectedTab4()) {
                helper = 1;
            }

            UpdatedValues.put(SYMBOL_IS_SELECTED_TAB_4, helper);
        }

        String[] arguments = {symbol.getName()};

        String where = SYMBOL_NAME + " LIKE ?";


        db.update(SYMBOLS_TABLE, UpdatedValues, where, arguments);
    }



    public void updateSymbolText(Symbol symbol)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues UpdatedValues = new ContentValues();

        UpdatedValues.put(SYMBOL_TEXT, symbol.getText());

        String[] arguments = {symbol.getName()};

        String where = SYMBOL_NAME + " LIKE ?";


        db.update(SYMBOLS_TABLE, UpdatedValues, where, arguments);
    }

    public void setSymbolTab(Symbol symbol, Integer tab)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues UpdatedValues = new ContentValues();

        if(tab == 1)
        UpdatedValues.put(SYMBOL_IS_SELECTED_TAB_1, true);

        if(tab == 2)
            UpdatedValues.put(SYMBOL_IS_SELECTED_TAB_2, true);

        if(tab == 3)
            UpdatedValues.put(SYMBOL_IS_SELECTED_TAB_3, true);

        if(tab == 4)
            UpdatedValues.put(SYMBOL_IS_SELECTED_TAB_4, true);

        String[] arguments = {symbol.getName()};

        String where = SYMBOL_NAME + " LIKE ?";


        db.update(SYMBOLS_TABLE, UpdatedValues, where, arguments);
    }

    public void unsetSymbol(Symbol symbol, Integer tab)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues UpdatedValues = new ContentValues();

        if(tab == 1)
            UpdatedValues.put(SYMBOL_IS_SELECTED_TAB_1, false);

        if(tab == 2)
            UpdatedValues.put(SYMBOL_IS_SELECTED_TAB_2, false);

        if(tab == 3)
            UpdatedValues.put(SYMBOL_IS_SELECTED_TAB_3, false);

        if(tab == 4)
            UpdatedValues.put(SYMBOL_IS_SELECTED_TAB_4, false);

        String[] arguments = {symbol.getName()};

        String where = SYMBOL_NAME + " LIKE ?";


        db.update(SYMBOLS_TABLE, UpdatedValues, where, arguments);
    }

    public void deleteSymbol(Symbol symbol)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(SYMBOLS_TABLE, SYMBOL_NAME + " = ?",
                new String[] { String.valueOf(symbol.getName()) });
    }


    public ArrayList<Symbol> getSymbols()
    {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] kolone = {SYMBOL_NAME, SYMBOL_TEXT, SYMBOL_IMAGE_PATH, SYMBOL_IS_SELECTED_TAB_1, SYMBOL_IS_SELECTED_TAB_2, SYMBOL_IS_SELECTED_TAB_3, SYMBOL_IS_SELECTED_TAB_4, SYMBOL_URL};


        String selectQuery = "SELECT * FROM " + SYMBOLS_TABLE;

        Cursor c = db.query(SYMBOLS_TABLE, kolone, null, null, null, null, null);


        ArrayList<Symbol> mSymbols = new ArrayList<Symbol>();



        if(c != null)
        {


            c.moveToFirst();

            if(c.getCount() != 0)
            {

                do {

                    Boolean is_selected_tab1 = false;

                    if(c.getInt(c.getColumnIndex(SYMBOL_IS_SELECTED_TAB_1)) == 1)
                    {
                        is_selected_tab1 = true;
                    }
                    Boolean is_selected_tab2 = false;

                    if(c.getInt(c.getColumnIndex(SYMBOL_IS_SELECTED_TAB_2)) == 1)
                    {
                        is_selected_tab2 = true;
                    }
                    Boolean is_selected_tab3 = false;

                    if(c.getInt(c.getColumnIndex(SYMBOL_IS_SELECTED_TAB_3)) == 1)
                    {
                        is_selected_tab3 = true;
                    }
                    Boolean is_selected_tab4 = false;

                    if(c.getInt(c.getColumnIndex(SYMBOL_IS_SELECTED_TAB_4)) == 1)
                    {
                        is_selected_tab4 = true;
                    }

                    ArrayList<Boolean> selectedTabs = new ArrayList<Boolean>();
                    selectedTabs.add(is_selected_tab1);
                    selectedTabs.add(is_selected_tab2);
                    selectedTabs.add(is_selected_tab3);
                    selectedTabs.add(is_selected_tab4);
                    Symbol symbol = new Symbol(c.getString(c.getColumnIndex(SYMBOL_NAME))
                            , c.getString(c.getColumnIndex(SYMBOL_TEXT))
                            , c.getString(c.getColumnIndex(SYMBOL_IMAGE_PATH))
                            ,selectedTabs, c.getString(c.getColumnIndex(SYMBOL_URL)));


                    mSymbols.add(symbol);
                } while (c.moveToNext());
            }
        }

        return mSymbols;
    }

    public ArrayList<Symbol> getSelectedSymbolsForTab(Integer tab) {

        SQLiteDatabase db = this.getReadableDatabase();
        String[] kolone = new String[]{SYMBOL_NAME, SYMBOL_TEXT, SYMBOL_IMAGE_PATH, SYMBOL_IS_SELECTED_TAB_1, SYMBOL_IS_SELECTED_TAB_2, SYMBOL_IS_SELECTED_TAB_3, SYMBOL_IS_SELECTED_TAB_4, SYMBOL_URL};;
        String where = " ";
        if(tab == 1) {

            where = SYMBOL_IS_SELECTED_TAB_1 + "=1";
        }
        if(tab == 2) {

            where = SYMBOL_IS_SELECTED_TAB_2 + "=1";
        }
        if(tab == 3) {

            where = SYMBOL_IS_SELECTED_TAB_3 + "=1";
        }
        if(tab == 4) {

            where = SYMBOL_IS_SELECTED_TAB_4 + "=1";
        }




        Cursor c = db.query(SYMBOLS_TABLE, kolone, where, null, null, null, null);


        ArrayList<Symbol> mSelectedSymbols = new ArrayList<Symbol>();

        if(c != null)
        {
            c.moveToFirst();

            if(c.getCount() != 0)
            {

                do {

                    Boolean is_selected_tab1 = false;

                    if(c.getInt(c.getColumnIndex(SYMBOL_IS_SELECTED_TAB_1)) == 1)
                    {
                        is_selected_tab1 = true;
                    }
                    Boolean is_selected_tab2 = false;

                    if(c.getInt(c.getColumnIndex(SYMBOL_IS_SELECTED_TAB_2)) == 1)
                    {
                        is_selected_tab2 = true;
                    }
                    Boolean is_selected_tab3 = false;

                    if(c.getInt(c.getColumnIndex(SYMBOL_IS_SELECTED_TAB_3)) == 1)
                    {
                        is_selected_tab3 = true;
                    }
                    Boolean is_selected_tab4 = false;

                    if(c.getInt(c.getColumnIndex(SYMBOL_IS_SELECTED_TAB_4)) == 1)
                    {
                        is_selected_tab4 = true;
                    }

                    ArrayList<Boolean> selectedTabs = new ArrayList<Boolean>();
                    selectedTabs.add(is_selected_tab1);
                    selectedTabs.add(is_selected_tab2);
                    selectedTabs.add(is_selected_tab3);
                    selectedTabs.add(is_selected_tab4);
                    Symbol symbol = new Symbol(c.getString(c.getColumnIndex(SYMBOL_NAME))
                            , c.getString(c.getColumnIndex(SYMBOL_TEXT))
                            , c.getString(c.getColumnIndex(SYMBOL_IMAGE_PATH))
                            ,selectedTabs, c.getString(c.getColumnIndex(SYMBOL_URL)));


                    mSelectedSymbols.add(symbol);
                } while (c.moveToNext());
            }
        }

        return mSelectedSymbols;

    }


    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }
}
