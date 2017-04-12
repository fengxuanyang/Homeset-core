package com.ragentek.homeset.audiocenter;

import android.os.Parcel;
import android.os.Parcelable;

import com.ximalaya.ting.android.opensdk.model.track.Track;

public class MyTrack implements Parcelable {
    private Track mTrack;

    public Track getTrack() {
        return mTrack;
    }

    public void setTrack(Track track) {
        this.mTrack = track;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(mTrack, flags);
    }

    public static final Parcelable.Creator<MyTrack> CREATOR = new Creator<MyTrack>() {
        @Override
        public MyTrack[] newArray(int size) {
            return new MyTrack[size];
        }

        @Override
        public MyTrack createFromParcel(Parcel source) {
            MyTrack myTrack = new MyTrack();

            Track track = source.readParcelable(Track.class.getClassLoader());
            myTrack.mTrack = track;

            return myTrack;
        }
    };

    @Override
    public String toString() {
        return "MyTrack{" +
                "mTrack=" + mTrack +
                '}';
    }
}