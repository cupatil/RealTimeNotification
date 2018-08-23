package com.practicalprogram.service;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.practicalprogram.R;
import com.practicalprogram.ui.add_booking.AddBookingActivity;
import com.practicalprogram.ui.add_booking.model.BookingModel;

public class Getlocation extends Service implements LocationListener {

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 40;// 40 meters
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 2;
    protected LocationManager locationManager;
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;
    int flag = 0;// notifier var
    Location location;
    Location mylocation = new Location("");
    Location dest_location = new Location("");
    float distance;
    double latitude;
    double longitude;

    private BookingModel bookingModel;

    // update location within a time period of 2 minutes
    private Context Context;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        flag = 0;
        stopSelf();
        stopUsingGPS();
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startid) {

        Context = this;
        mylocation = getLocation(Context);

        Bundle bundle = intent.getExtras();
        bookingModel = (BookingModel) (bundle != null ? bundle.getParcelable("booking_object") : new BookingModel());

        Double msg = mylocation.getLatitude();
        dest_location.setLatitude(Double.parseDouble(bookingModel.getDestination_latitude()));
        dest_location.setLongitude(Double.parseDouble(bookingModel.getDestination_longitude()));


        return START_NOT_STICKY;
    }

    @SuppressLint("MissingPermission")
    public Location getLocation(Context Context) {

        try {
            locationManager = (LocationManager) Context
                    .getSystemService(LOCATION_SERVICE);

            isGPSEnabled = locationManager != null && locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);


            isNetworkEnabled = locationManager != null && locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // No network provider is enabled
                Toast.makeText(this, "Enable Network or GPS", Toast.LENGTH_LONG).show();
            } else {
                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);


                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }

        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

    public void stopUsingGPS() {
        if (locationManager != null) locationManager.removeUpdates(Getlocation.this);
    }


    @Override
    public void onLocationChanged(Location location) {


        mylocation = getLocation(Context);
        distance = mylocation.distanceTo(dest_location);
        if (flag == 0) {
            if ((distance / 1000) < 1) {

                NotificationManager mNotificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel("default",
                            "practical",
                            NotificationManager.IMPORTANCE_DEFAULT);
                    channel.setDescription("Real time notification");
                    mNotificationManager.createNotificationChannel(channel);
                }
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), "default")
                        .setSmallIcon(R.mipmap.ic_launcher) // notification icon
                        .setContentTitle("Real time notification") // title for notification
                        .setContentText("You are within 1 km from your destination!!")// message for notification
                        .setAutoCancel(true); // clear notification after click

                Intent intent = new Intent(getApplicationContext(), AddBookingActivity.class);
                intent.setAction("notification");
                Bundle bundle = new Bundle();
                bundle.putParcelable("booking_object", bookingModel);
                intent.putExtras(bundle);

                // set intent so it does not start a new activity
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                mBuilder.setContentIntent(pi);
                int randomPIN = (int) (Math.random() * 9000) + 1000;
                sendLocalBroadcast(String.valueOf(randomPIN));
                mNotificationManager.notify(randomPIN, mBuilder.build());

                flag = 1;
                onDestroy();
            }
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    /**
     * This method sends the local broadcast to activity
     */
    void sendLocalBroadcast(String notificationId) {

        Intent intent = new Intent("notification");

        intent.putExtra("notificationId", notificationId);

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

}