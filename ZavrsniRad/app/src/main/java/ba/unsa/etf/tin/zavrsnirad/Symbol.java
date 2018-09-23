package ba.unsa.etf.tin.zavrsnirad;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Tin on 25.05.2018..
 */

public final class Symbol implements Parcelable{

    private String mName;
    private String mText;
    private String mImagePath;
    private Boolean mIsSelectedTab1 = false;
    private Boolean mIsSelectedTab2 = false;
    private Boolean mIsSelectedTab3 = false;
    private Boolean mIsSelectedTab4 = false;
    private String mImageURL;

    public Symbol(String name, String text, String imagePath, String imageURL)
    {
        mName = name;
        mText = text;
        mImagePath = imagePath;
        //TODO: da li treba false ili da se specificira prilikom kreiranja
        mIsSelectedTab1 = false;
        mIsSelectedTab2 = false;
        mIsSelectedTab3 = false;
        mIsSelectedTab4 = false;
        mImageURL = imageURL;
    }

    public Symbol(String name, String text, String imagePath, ArrayList<Boolean>selectedTabs, String imageURL)
    {
        mName = name;
        mText = text;
        mImagePath = imagePath;
        //TODO: da li treba false ili da se specificira prilikom kreiranja

        for(int index = 0; index < selectedTabs.size(); index++)
        {
            if(selectedTabs.get(index))
            {
                if(index == 0)
                {
                    mIsSelectedTab1 = true;


                }
                if(index == 1)
                {

                    mIsSelectedTab2 = true;

                }
                if(index == 2)
                {

                    mIsSelectedTab3 = true;

                }
                if(index == 3)
                {

                    mIsSelectedTab4 = true;
                }
            }
        }

        mImageURL = imageURL;
    }

    private Symbol(Parcel source)
    {
        mName = source.readString();
        mText = source.readString();
        mImagePath = source.readString();
        mIsSelectedTab1 = source.readByte() == 1;
        mIsSelectedTab2 = source.readByte() == 1;
        mIsSelectedTab3 = source.readByte() == 1;
        mIsSelectedTab4 = source.readByte() == 1;
        mImageURL = source.readString();
    }

    public String getName() {
        return mName;
    }

    public String getText() {
        return mText;
    }

    public String getImagePath() {
        return mImagePath;
    }

    public Boolean getIsSelectedTab1() { return mIsSelectedTab1; }
    public Boolean getIsSelectedTab2() { return mIsSelectedTab2; }
    public Boolean getIsSelectedTab3() { return mIsSelectedTab3; }
    public Boolean getIsSelectedTab4() { return mIsSelectedTab4; }

    public String getImageURL() {
        return mImageURL;
    }


    public void setName(String Name) {
        this.mName = Name;
    }

    public void setText(String Text) {
        this.mText = Text;
    }

    public void setImagePath(String ImagePath) {
        this.mImagePath = ImagePath;
    }

    public void setmIsSelectedTab1(Boolean IsSelected) { this.mIsSelectedTab1 = IsSelected; }
    public void setmIsSelectedTab2(Boolean IsSelected) { this.mIsSelectedTab2 = IsSelected; }
    public void setmIsSelectedTab3(Boolean IsSelected) { this.mIsSelectedTab3 = IsSelected; }
    public void setmIsSelectedTab4(Boolean IsSelected) { this.mIsSelectedTab4 = IsSelected; }

    public void setmImageURL(String URL) {
        this.mImageURL = URL;
    }

    private String getCompareKey() {
        return mName + "|" + mText + "|" + mImagePath + mIsSelectedTab1 + mIsSelectedTab2 + mIsSelectedTab3 + mIsSelectedTab4 + mImageURL;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Symbol that = (Symbol) obj;

        return getCompareKey().equals(that.getCompareKey());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeString(mText);
        dest.writeString(mImagePath);
        dest.writeByte((byte)(mIsSelectedTab1 ? 1 : 0));
        dest.writeByte((byte)(mIsSelectedTab2 ? 1 : 0));
        dest.writeByte((byte)(mIsSelectedTab3 ? 1 : 0));
        dest.writeByte((byte)(mIsSelectedTab4 ? 1 : 0));
        dest.writeString(mImageURL);

    }

    public final static Parcelable.Creator<Symbol> CREATOR = new Creator<Symbol>() {
        @Override
        public Symbol createFromParcel(Parcel source) {
            return new Symbol(source);
        }

        @Override
        public Symbol[] newArray(int size) {
            return new Symbol[size];
        }
    };
}
