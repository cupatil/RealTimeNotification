package com.practicalprogram.ui.add_booking;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.practicalprogram.R;
import com.practicalprogram.ui.add_booking.model.BookingModel;
import com.practicalprogram.ui.search_place.SearchPlaceActivity;
import com.practicalprogram.utils.ProjectUtilities;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddBookingActivity extends AppCompatActivity implements IAddBookingView {

    public final int PICKUP = 100, DESTINATION = 101;
    private final int REQUEST_PERMISSION = 100;
    @BindView(R.id.etPickuplocation)
    EditText etPickuplocation;
    @BindView(R.id.etDropLocation)
    EditText etDropLocation;
    @BindView(R.id.btnStartStop)
    Button btnStartStop;
    IAddBookingPresenter iAddBookingPresenter;
    private BookingModel bookingModel = new BookingModel();
    private BroadcastReceiver mMessageReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_booking);

        ButterKnife.bind(this);

        if (getIntent().getAction() != null && getIntent().getAction().equals("notification")) {
            bookingModel = (BookingModel) getIntent().getExtras().getParcelable("booking_object");
            etPickuplocation.setText(bookingModel.getSource_address());
            etDropLocation.setText(bookingModel.getDestination_address());
            btnStartStop.setText(getString(R.string.stop));
            btnStartStop.setVisibility(View.VISIBLE);
        }

        iAddBookingPresenter = new AddBookingPresenterImp(AddBookingActivity.this, this);
    }


    @OnClick({R.id.etPickuplocation, R.id.etDropLocation})
    void onClick(View view) {

        if (ProjectUtilities.checkInternetAvailable(AddBookingActivity.this)) {

            Intent intent = new Intent(AddBookingActivity.this, SearchPlaceActivity.class);
            switch (view.getId()) {
                case R.id.etPickuplocation:

                    startActivityForResult(intent, PICKUP);
                    break;
                case R.id.etDropLocation:
                    startActivityForResult(intent, DESTINATION);
                    break;
                default:
                    break;
            }
        } else {
            ProjectUtilities.internetDialog(AddBookingActivity.this);
        }
    }

    @OnClick(R.id.btnStartStop)
    void onSubmitButtonClick() {

        if (ProjectUtilities.checkPermission(AddBookingActivity.this)) {
            buttonClickPerform();
        } else {
            ActivityCompat.requestPermissions(AddBookingActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_PERMISSION);

        }


    }

    void buttonClickPerform() {
        if (btnStartStop.getText().equals(getString(R.string.start))) {

            iAddBookingPresenter.startService(bookingModel);
        } else {
            etPickuplocation.setText("");
            etDropLocation.setText("");
            btnStartStop.setVisibility(View.GONE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == 100 && (requestCode == 100 || requestCode == 101)) {

            String location_name = data.getStringExtra("place_name");
            String latitudeLocal = data.getStringExtra("place_latitude");
            String longitudeLocal = data.getStringExtra("place_longitude");

            if (requestCode == 100) {
                etPickuplocation.setText(location_name);
                bookingModel.setSource_address(location_name);
                bookingModel.setSource_latitude(latitudeLocal);
                bookingModel.setSource_longitude(longitudeLocal);
            } else {
                etDropLocation.setText(location_name);
                bookingModel.setDestination_address(location_name);
                bookingModel.setDestination_latitude(latitudeLocal);
                bookingModel.setDestination_longitude(longitudeLocal);
            }

            if (!bookingModel.getDestination_address().isEmpty() && !bookingModel.getSource_address().isEmpty()) {
                btnStartStop.setText(getString(R.string.start));
                btnStartStop.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void serviceStarted() {
        btnStartStop.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                btnStartStop.setText(getString(R.string.stop));
                btnStartStop.setVisibility(View.VISIBLE);

                String sNotificationId = intent.getStringExtra("notificationId");
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(Integer.parseInt(sNotificationId));
                ProjectUtilities.showAlertDialog(AddBookingActivity.this, getString(R.string.msg_with_in_one_km));
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("notification"));


        super.onResume();

    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == REQUEST_PERMISSION) {

            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                buttonClickPerform();

            } else {
                Toast.makeText(this, "Please enable location permission.", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}
