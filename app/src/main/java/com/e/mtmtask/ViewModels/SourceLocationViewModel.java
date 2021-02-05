package com.e.mtmtask.ViewModels;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Looper;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.e.mtmtask.R;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.tapadoo.alerter.Alerter;

import java.util.List;

import lombok.Getter;
import timber.log.Timber;

/**
 * Created by Hussein on 04/02/2021
 */

@SuppressLint("MissingPermission")
public class SourceLocationViewModel extends ViewModel {
    public static final int REQUEST_CHECK_SETTINGS = 4;
    @Getter
    private final MutableLiveData<Location> currentLocationResponseMutableLiveData = new MutableLiveData<>();
    MutableLiveData<Boolean> requestLocationPermission = new MutableLiveData<>();
    MutableLiveData<Boolean> requestEnableLocationPermission = new MutableLiveData<>();
    LocationRequest locationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setNumUpdates(1)
            .setInterval(0)
            .setFastestInterval(1);

    Activity activity;

    public void requestCurrentLocation(Activity activity) {
        this.activity = activity;
        Dexter.withActivity(activity)
                .withPermissions(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (report.areAllPermissionsGranted()) {
                    onLocationPermissionGranted();
                } else {
                    onPermissionDenied();
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<com.karumi.dexter.listener.PermissionRequest> list, PermissionToken permissionToken) {

            }
        }).check();
    }

    private void onLocationPermissionGranted() {
        Timber.d("onLocationPermissionGranted: ");
        requestLocationPermission.setValue(true);
        requestLocationUpdate();
    }


    private void requestLocationUpdate() {
        Timber.d("requestLocationUpdate: ");

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)
                .setAlwaysShow(true);
        LocationServices.getSettingsClient(activity)
                .checkLocationSettings(builder.build())
                .addOnFailureListener(this::onUnableRequestingLocation)
                .addOnSuccessListener(this::onAbleRequestingLocation);
    }

    private void onAbleRequestingLocation(LocationSettingsResponse locationSettingsResponse) {
        Timber.d("onAbleRequestingLocation");
        requestEnableLocationPermission.setValue(true);
        //locationRequest
        FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
        LocationCallback mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                onCurrentLocationResponse(locationResult.getLastLocation());
            }
        };
        mFusedLocationClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.myLooper());
    }

    public void onUnableRequestingLocation(@NonNull Exception e) {
        Timber.d("onUnableRequestingLocation");
        requestEnableLocationPermission.setValue(false);
        int statusCode = ((ApiException) e).getStatusCode();
        switch (statusCode) {
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED: {
                // LocationPojo settings are not satisfied. But could be fixed by showing the
                // user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    ResolvableApiException rae = (ResolvableApiException) e;
                    startResolutionForResult(rae, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException ee) {
                    // Ignore the error.
                } catch (ClassCastException ee) {
                    // Ignore, should be an impossible error.
                }
                break;
            }
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE: {
                onUnableRequestingEverLocation();
            }
            break;
        }
    }

    private void onCurrentLocationResponse(Location location) {
        Timber.d("locationPojo: %s ", location.toString());
        currentLocationResponseMutableLiveData.setValue(location);
    }

    private void onPermissionDenied() {
        requestLocationPermission.setValue(false);
        activity.finish();
    }

    public void startResolutionForResult(ResolvableApiException rae, int code) throws IntentSender.SendIntentException {
        rae.startResolutionForResult(activity, code);
    }

    private void onUnableRequestingEverLocation() {
        startLocationSettingActivity();
    }

    public void startLocationSettingActivity() {
        activity.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
    }
}
