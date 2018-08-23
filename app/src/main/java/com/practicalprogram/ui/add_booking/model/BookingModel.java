package com.practicalprogram.ui.add_booking.model;

import android.os.Parcel;
import android.os.Parcelable;

public class BookingModel implements Parcelable {


    public static final Creator<BookingModel> CREATOR = new Creator<BookingModel>() {
        @Override
        public BookingModel createFromParcel(Parcel in) {
            return new BookingModel(in);
        }

        @Override
        public BookingModel[] newArray(int size) {
            return new BookingModel[size];
        }
    };
    private String source_address = "", source_latitude = "", source_longitude = "", destination_address = "", destination_latitude, destination_longitude = "";

    public BookingModel() {
    }

    protected BookingModel(Parcel in) {
        source_address = in.readString();
        source_latitude = in.readString();
        source_longitude = in.readString();
        destination_address = in.readString();
        destination_latitude = in.readString();
        destination_longitude = in.readString();
    }

    public String getSource_address() {
        return source_address;
    }

    public void setSource_address(String source_address) {
        this.source_address = source_address;
    }

    public String getSource_latitude() {
        return source_latitude;
    }

    public void setSource_latitude(String source_latitude) {
        this.source_latitude = source_latitude;
    }

    public String getSource_longitude() {
        return source_longitude;
    }

    public void setSource_longitude(String source_longitude) {
        this.source_longitude = source_longitude;
    }

    public String getDestination_address() {
        return destination_address;
    }

    public void setDestination_address(String destination_address) {
        this.destination_address = destination_address;
    }

    public String getDestination_latitude() {
        return destination_latitude;
    }

    public void setDestination_latitude(String destination_latitude) {
        this.destination_latitude = destination_latitude;
    }

    public String getDestination_longitude() {
        return destination_longitude;
    }

    public void setDestination_longitude(String destination_longitude) {
        this.destination_longitude = destination_longitude;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(source_address);
        parcel.writeString(source_latitude);
        parcel.writeString(source_longitude);
        parcel.writeString(destination_address);
        parcel.writeString(destination_latitude);
        parcel.writeString(destination_longitude);
    }
}
