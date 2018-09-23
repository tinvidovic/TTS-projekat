package ba.unsa.etf.tin.zavrsnirad;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * Created by Tin on 28.05.2018..
 */

public class SymbolAdapter extends ArrayAdapter<Symbol> {

    int mResource;
    private Context mContext;
    private ImageView mSlikaSimbola;
    private GridView mGridView;


    public SymbolAdapter(Context context, int _resource, List<Symbol> items) {

        super(context, _resource, items);

        mContext = context;
        mResource = _resource;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LinearLayout newView;

        if (convertView == null) {
            newView = new LinearLayout(getContext());

            String inflater = Context.LAYOUT_INFLATER_SERVICE;

            LayoutInflater li;

            li = (LayoutInflater) getContext().getSystemService(inflater);

            li.inflate(mResource, newView, true);
        } else {
            newView = (LinearLayout) convertView;
        }

        Symbol classInstance = getItem(position);

        mSlikaSimbola = (ImageView) newView.findViewById(R.id.imageView_SlikaSimbola);

        mGridView = (GridView) newView.findViewById(R.id.listView_symbols);

        final TextView tekstSimbola = (TextView) newView.findViewById(R.id.textView_TekstSimbola);

        tekstSimbola.setText(classInstance.getText());

        int idSlike = mContext.getResources().getIdentifier(classInstance.getImagePath(), "drawable", mContext.getPackageName());


        try
        {
            FileInputStream fis = new FileInputStream (new File(classInstance.getImagePath()));
            mSlikaSimbola.setImageBitmap(BitmapFactory.decodeFile(classInstance.getImagePath()));

        }catch (FileNotFoundException e)
        {
            mSlikaSimbola.setImageResource(idSlike);
            Log.d("nepostojeciFile", "Exception 42. Ne postoji " + classInstance.getImagePath());
        }

        postaviVelicinuSlikeSimbola();

        return newView;
    }

    public Bitmap loadImageBitmap(Context context, String imageName) {
        Bitmap bitmap = null;
        FileInputStream fiStream;
        try {
            fiStream    = context.openFileInput(imageName);
            bitmap      = BitmapFactory.decodeStream(fiStream);
            fiStream.close();
        } catch (Exception e) {
            Log.d("saveImage", "Exception 3, Something went wrong!");
            e.printStackTrace();
        }
        return bitmap;
    }


    private void postaviVelicinuSlikeSimbola() {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());

        String imageSizeSettingIndex = sharedPref.getString(SettingsActivity.KEY_PREF_SYMBOL_IMAGE_SIZE, "");


        if (imageSizeSettingIndex.equals(Constants.pedesetPiksela)) {


            mSlikaSimbola.requestLayout();

            mSlikaSimbola.getLayoutParams().height = 50;

            mSlikaSimbola.getLayoutParams().width = 50;


        } else if (imageSizeSettingIndex.equals(Constants.stotinuPiksela)) {


            mSlikaSimbola.requestLayout();

            mSlikaSimbola.getLayoutParams().height = 100;

            mSlikaSimbola.getLayoutParams().width = 100;


        } else if (imageSizeSettingIndex.equals(Constants.stotinuPedesetPiksela)) {

            mSlikaSimbola.requestLayout();

            mSlikaSimbola.getLayoutParams().height = 150;

            mSlikaSimbola.getLayoutParams().width = 150;

        } else if(imageSizeSettingIndex.equals(Constants.dvijeStotinePedesetPiksela))
        {
            mSlikaSimbola.requestLayout();

            mSlikaSimbola.getLayoutParams().height = 250;

            mSlikaSimbola.getLayoutParams().width = 250;

        } else if(imageSizeSettingIndex.equals(Constants.triStotinePedesetPiksela))
        {
            mSlikaSimbola.requestLayout();

            mSlikaSimbola.getLayoutParams().height = 350;

            mSlikaSimbola.getLayoutParams().width = 350;

        } else if(imageSizeSettingIndex.equals(Constants.petStotinaPiksela))
        {
            mSlikaSimbola.requestLayout();

            mSlikaSimbola.getLayoutParams().height = 500;

            mSlikaSimbola.getLayoutParams().width = 500;

        }else if(imageSizeSettingIndex.equals(Constants.sedamStodinaPedesetPiksela))
        {
            mSlikaSimbola.requestLayout();

            mSlikaSimbola.getLayoutParams().height = 750;

            mSlikaSimbola.getLayoutParams().width = 750;

        } else if(imageSizeSettingIndex.equals(Constants.hiljaduPiksela))
        {
            mSlikaSimbola.requestLayout();

            mSlikaSimbola.getLayoutParams().height = 1000;

            mSlikaSimbola.getLayoutParams().width = 1000;

        }

    }
}