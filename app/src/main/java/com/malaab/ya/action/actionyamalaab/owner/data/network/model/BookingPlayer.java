package com.malaab.ya.action.actionyamalaab.owner.data.network.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class BookingPlayer implements Parcelable {

    public String uId;
    public long appUserId;

    public String name;

    public String email;
    public String mobileNo;

    public String profileImageUrl;

    public float price;

    public boolean isPaid;
    public boolean isAttended;

    public int invitees;


    public BookingPlayer() {
        // Default constructor required for calls to DataSnapshot.getValue(MetaData.class)
    }


    @Exclude
    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", mobileNo='" + mobileNo + '\'' +
                ", profileImageUrl='" + profileImageUrl + '\'' +
                ", isPaid='" + isPaid + '\'' +
                ", isAttended='" + isAttended + '\'' +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.uId);
        dest.writeLong(this.appUserId);
        dest.writeString(this.name);
        dest.writeString(this.email);
        dest.writeString(this.mobileNo);
        dest.writeString(this.profileImageUrl);
        dest.writeByte(this.isPaid ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isAttended ? (byte) 1 : (byte) 0);
    }

    protected BookingPlayer(Parcel in) {
        this.uId = in.readString();
        this.appUserId = in.readLong();
        this.name = in.readString();
        this.email = in.readString();
        this.mobileNo = in.readString();
        this.profileImageUrl = in.readString();
        this.isPaid = in.readByte() != 0;
        this.isAttended = in.readByte() != 0;
    }

    public static final Parcelable.Creator<BookingPlayer> CREATOR = new Parcelable.Creator<BookingPlayer>() {
        @Override
        public BookingPlayer createFromParcel(Parcel source) {
            return new BookingPlayer(source);
        }

        @Override
        public BookingPlayer[] newArray(int size) {
            return new BookingPlayer[size];
        }
    };
}
