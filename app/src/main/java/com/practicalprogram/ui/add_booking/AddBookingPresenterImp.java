package com.practicalprogram.ui.add_booking;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.practicalprogram.service.Getlocation;
import com.practicalprogram.ui.add_booking.model.BookingModel;


public class AddBookingPresenterImp implements IAddBookingPresenter {

    private IAddBookingView view;
    private Context mContext;

    public AddBookingPresenterImp(Context mContext, IAddBookingView view) {
        this.view = view;
        this.mContext = mContext;
    }

    @Override
    public void startService(BookingModel bookingModel) {

        Intent intent = new Intent(mContext, Getlocation.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("booking_object", bookingModel);
        intent.putExtras(bundle);

        mContext.startService(intent);
        view.serviceStarted();
    }


}
