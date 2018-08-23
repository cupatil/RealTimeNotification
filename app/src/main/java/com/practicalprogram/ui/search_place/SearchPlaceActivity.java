/*
 * Copyright (C) 2015 Google Inc. All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.practicalprogram.ui.search_place;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.practicalprogram.R;
import com.practicalprogram.utils.ProjectUtilities;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchPlaceActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener {


    protected GoogleApiClient mGoogleApiClient;
    @BindView(R.id.autocomplete_places)
    AutoCompleteTextView mAutocompleteView;
    String place_name = "";

    private PlaceAutocompleteAdapter mAdapter;

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully
                places.release();
                return;
            }

            try {
                // Get the Place object from the buffer.
                final Place place = places.get(0);
                LatLng queriedLocation = place.getLatLng();
                Log.v("Latitude is", "" + queriedLocation.latitude);
                Log.v("Longitude is", "" + queriedLocation.longitude);

                Intent i = new Intent();
                //i.putExtra("place_name", place.getName());
                i.putExtra("place_name", place_name);
                i.putExtra("place_latitude", "" + queriedLocation.latitude);
                i.putExtra("place_longitude", "" + queriedLocation.longitude);
                setResult(100, i);
                finish();
            } catch (Exception e) {
                Toast.makeText(SearchPlaceActivity.this, "Place query did not complete.", Toast.LENGTH_SHORT).show();
                mAutocompleteView.setText("");
            }


            places.release();
        }
    };

    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            ProjectUtilities.hideKeyboard(SearchPlaceActivity.this);

            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a AutocompletePrediction from which we
             read the place ID and title.
              */
            final AutocompletePrediction item = mAdapter.getItem(position);
            final String placeId = item.getPlaceId();
            place_name = item.getPrimaryText(new StyleSpan(Typeface.BOLD)) + "," + item.getSecondaryText(new StyleSpan(Typeface.BOLD));

            //final CharSequence primaryText = item.getPrimaryText(null);
            final CharSequence primaryText = item.getFullText(null);

            /*
             Issue a request to the Places Geo LoginDataItem API to retrieve a Place object with additional
             details about the place.
              */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);


        }
    };

    private static Spanned formatPlaceDetails(Resources res, CharSequence name, String id,
                                              CharSequence address, CharSequence phoneNumber, Uri websiteUri) {
        return Html.fromHtml(res.getString(R.string.place_details, name, id, address, phoneNumber,
                websiteUri));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mGoogleApiClient = new GoogleApiClient.Builder(SearchPlaceActivity.this)
                .enableAutoManage(this, 0 /* clientId */, this)
                .addApi(Places.GEO_DATA_API)
                .build();

        setContentView(R.layout.activity_google_places);

        ButterKnife.bind(this);
        // Retrieve the AutoCompleteTextView that will display Place suggestions.

        mAutocompleteView.setOnEditorActionListener(new AutoCompleteTextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    //do Whatever you Want to do
                    ProjectUtilities.hideKeyboard(SearchPlaceActivity.this);
                }
                return true;
            }


        });

        // Set up the adapter that will retrieve suggestions from the Places Geo LoginDataItem API that cover
        // the entire world.
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_GEOCODE) //TYPE_FILTER_GEOCODE, TYPE_FILTER_ADDRESS
                .build();
        mAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient, null,
                typeFilter);
        mAutocompleteView.setAdapter(mAdapter);
        // Register a listener that receives callbacks when a suggestion has been selected
        mAutocompleteView.setOnItemClickListener(mAutocompleteClickListener);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Toast.makeText(this,
                "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();
    }

}
