package ba.unsa.etf.tin.zavrsnirad;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tin on 25.05.2018..
 */

// DataManager - Singleton koji se bavi hardkodiranim podacima, potrebnim za testiranje aplikacije.


public class DataManager {
    private static DataManager ourInstance = null;

    private ArrayList<Symbol> mSymbols = new ArrayList<>();


    public static DataManager getInstance() {
        if(ourInstance == null) {
            ourInstance = new DataManager();
            ourInstance.initializeSymbols();

        }
        return ourInstance;
    }

    public String getCurrentUserName() {
        return "Tin Vidovic";
    }

    public String getCurrentUserEmail() {
        return "tvidovic1@etf.unsa.ba";
    }

    public ArrayList<Symbol> getSymbols() {
        return mSymbols;
    }

   /* public ArrayList<Symbol> getSelectedSymbols() {

        ArrayList<Symbol> odabraniSimboli = new ArrayList<Symbol>();

        for(int index = 0; index < mSymbols.size(); index++)
        {
            if(mSymbols.get(index).getIsSelected()) odabraniSimboli.add(mSymbols.get(index));
        }

        return odabraniSimboli;
    }*/

    // Funkcija koja dodaje novi simbol u DataManager, te vraca ukupan broj do sada dodanih simbola.

    public int createNewSymbol() {
        Symbol newSymbol = new Symbol(null, null, null, null);
        mSymbols.add(newSymbol);
        return mSymbols.size() - 1;
    }

    // Funkcija koja vraca redni broj trazenog simbola ukoliko postoji, u suprotnom vraca -1.

    public int findSymbol(Symbol symbol) {

        for(int index = 0; index < mSymbols.size(); index++) {
            if(symbol.equals(mSymbols.get(index)))
                return index;
        }

        return -1;
    }

    // Funkcija, koja brise simbol na indexu koji se salje kao parametar.

    public void removeSymbol(int index) {
        mSymbols.remove(index);
    }

    private DataManager() {
    }

    //region Initialization code

    private void initializeSymbols() {
        mSymbols.add(initializeSymbol1());
        mSymbols.add(initializeSymbol2());
        mSymbols.add(initializeSymbol3());
        mSymbols.add(initializeSymbol4());
    }


    private Symbol initializeSymbol1() {

        ArrayList<Boolean> selectedTabs = new ArrayList<Boolean>();
        selectedTabs.add(true); selectedTabs.add(false); selectedTabs.add(false); selectedTabs.add(false);
         return new Symbol("Prvi simbol", "I have clicked on the first symbol.", "symbol_car", selectedTabs, "");
    }

    private Symbol initializeSymbol2() {

        ArrayList<Boolean> selectedTabs = new ArrayList<Boolean>();
        selectedTabs.add(false); selectedTabs.add(true); selectedTabs.add(false); selectedTabs.add(false);
        return new Symbol("Drugi simbol", "I have clicked on the second symbol.", "symbol_man", selectedTabs, "");
    }

    private Symbol initializeSymbol3() {

        ArrayList<Boolean> selectedTabs = new ArrayList<Boolean>();
        selectedTabs.add(false); selectedTabs.add(false); selectedTabs.add(true); selectedTabs.add(false);
        return new Symbol("Treci simbol", "I have clicked on the third symbol.", "symbol_woman", selectedTabs, "");
    }

    private Symbol initializeSymbol4() {

        ArrayList<Boolean> selectedTabs = new ArrayList<Boolean>();
        selectedTabs.add(false); selectedTabs.add(false); selectedTabs.add(false); selectedTabs.add(true);
        return new Symbol("Cetvrti simbol", "I have clicked on the fourth symbol", "symbol_bird_crow", selectedTabs, "");
    }
    //endregion

}