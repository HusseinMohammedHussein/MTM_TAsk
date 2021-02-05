package com.e.mtmtask;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;

import com.e.mtmtask.Adapters.DestinationLocationAdapter;
import com.e.mtmtask.Adapters.SourceLocationAdapter;
import com.e.mtmtask.Models.SourceLocationPojo;
import com.e.mtmtask.ViewModels.DestinationLocationViewModel;
import com.e.mtmtask.ViewModels.FirestoreDataViewModel;
import com.e.mtmtask.ViewModels.SourceLocationViewModel;
import com.e.mtmtask.databinding.ActivityMainBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tapadoo.alerter.Alerter;

import timber.log.Timber;

@SuppressLint("MissingPermission")
public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    /*Initial Activity*/
    private ActivityMainBinding binding;
    private AppBarConfiguration mAppBarConfiguration;

    /*Initial Google service*/
    private FirebaseFirestore firestore;
    private GoogleMap mMap;

    /*Initial Model*/
    private SourceLocationPojo sourceLocation;

    /*Adapters*/
    private SourceLocationAdapter mSourceLocationAdapter;
    private DestinationLocationAdapter mDestinationLocationAdapter;

    /*ViewModels*/
    private SourceLocationViewModel mSourceLocationViewModel;
    private FirestoreDataViewModel mSourceDataViewModel;
    private DestinationLocationViewModel mDestinationLocationViewModel;

    /*Instance Vars*/
    public static final int DEFAULT_ZOOM = 15;
    public static final int SECOND_ICON = 1;
    public static final int THIRD_ICON = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        // Setup Toolbar
        setSupportActionBar(binding.contentMain.includeAppbar.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        methods();// get Methods scope in class
        binding.contentMain.includeAppbar.toolbar.setNavigationIcon(R.drawable.ic_menu); //setNavigationIcon
    }

    private void methods() {
        initial();
        defaultMode();
        setupRecyclerView();
        setupMapFragment();
        startLoading();
        setupLeftSlideBar();
//        setDestinationData(); disable because don't have Google Billing Account
        getDestinationSelected();
        sourceLocDataObserver();
        setupSourceLocationViewModel();
        getSelectedLocation();
    }

    private void initial() {
        //Initial FirebaseFirestore
        firestore = FirebaseFirestore.getInstance();

        // Initial Adapters
        mSourceLocationAdapter = new SourceLocationAdapter();
        mDestinationLocationAdapter = new DestinationLocationAdapter();

        //Initial ViewModels
        mSourceLocationViewModel = new ViewModelProvider(this).get(SourceLocationViewModel.class);
        mSourceDataViewModel = new ViewModelProvider(this).get(FirestoreDataViewModel.class);
        mDestinationLocationViewModel = new ViewModelProvider(this).get(DestinationLocationViewModel.class);
    }

    private void setupRecyclerView() {
        binding.contentMain.includeYourLocList.rvYourLocList.setHasFixedSize(true);
        LayoutManager sourceLayoutManager = new LinearLayoutManager(this);
        binding.contentMain.includeYourLocList.rvYourLocList.setLayoutManager(sourceLayoutManager);
        binding.contentMain.includeYourLocList.rvYourLocList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        LayoutManager destinationLayoutManager = new LinearLayoutManager(this);
        binding.contentMain.includeDestinationList.rvDestinationList.setHasFixedSize(true);
        binding.contentMain.includeDestinationList.rvDestinationList.setLayoutManager(destinationLayoutManager);
        binding.contentMain.includeDestinationList.rvDestinationList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    private void setupMapFragment() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void setupLeftSlideBar() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(SECOND_ICON).setOnMenuItemClickListener(item -> {
            Alerter.create(this)
                    .setText(item.getTitle().toString())
                    .setBackgroundColorInt(getResources().getColor(R.color.darkerGray))
                    .show();
            return true;
        });

        navigationView.getMenu().getItem(THIRD_ICON).setOnMenuItemClickListener(item -> {
            Alerter.create(this)
                    .setText(item.getTitle().toString())
                    .setBackgroundColorInt(getResources().getColor(R.color.darkerGray))
                    .show();
            return true;
        });

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            binding.contentMain.includeAppbar.toolbar.setNavigationIcon(R.drawable.ic_menu);
        });
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    /**
     * SourceLocation
     */
    private void setupSourceLocationViewModel() {
        mSourceLocationViewModel.getCurrentLocationResponseMutableLiveData().observe(this, location -> {
            Timber.d("initial: ");
            stopLoading();
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng((location.getLatitude()),
                            location.getLongitude()), DEFAULT_ZOOM));

            mMap.addMarker(new MarkerOptions()
                    .title(getString(R.string.my_location))
                    .position(new LatLng(location.getLatitude(), location.getLongitude())));
        });
    }

    private void sourceLocDataObserver() {
        mSourceDataViewModel.getFirestoreData(firestore).observe(this, sourceLocationPojos -> {
            mSourceLocationAdapter.setResponseList(sourceLocationPojos);
            binding.contentMain.includeYourLocList.rvYourLocList.setAdapter(mSourceLocationAdapter);
        });
    }

    private void getSelectedLocation() {
        binding.contentMain.etYourLocation.setOnClickListener(v -> {
            displaySourceLocationList();
            mSourceLocationAdapter.setOnItemClickListener(pojo -> {
                sourceLocation = pojo;
                Timber.d("getInputText: %s", sourceLocation.getName());
                binding.contentMain.etYourLocation.setText(sourceLocation.getName());
                binding.contentMain.includeYourLocList.rvYourLocList.setVisibility(View.GONE);
            });
        });
    }

    /**
     * DestinationLocation
     */

    /**
     * this setDestinationData() &&  destinationObserver() are disable because don't have Google billing account.
     */
    private void setDestinationData() {
        destinationTextWatcher();
        binding.contentMain.includeDestinationList.tvDone.setOnClickListener(v -> {
            String getInoutText = binding.contentMain.etDestination.getText().toString().trim();
            String[] fields = getResources().getStringArray(R.array.fields);
            String key = getString(R.string.google_maps_key);
            String inputType = getString(R.string.intput_type);
//            destinationObserver(getInoutText, inputType, fields, key);
            Timber.d("getText: %s", getInoutText);
            binding.contentMain.includeDestinationList.rvDestinationList.setVisibility(View.VISIBLE);
        });
    }

    private void destinationObserver(String input, String inputType, String[] fields, String key) {
        mDestinationLocationViewModel.getDestinationDataMutable(input, inputType, fields, key).observe(this, destinationLocationPojo -> {
            mDestinationLocationAdapter.setResponseList(destinationLocationPojo.getCandidates());
            Timber.d("getCandidates-Name: %s", destinationLocationPojo.getCandidates());
            binding.contentMain.includeDestinationList.rvDestinationList.setAdapter(mDestinationLocationAdapter);
            displayDestinationLocationList();
        });
    }

    //use getDestinationSelected() instead of destinationObserver()
    private void getDestinationSelected() {
        binding.contentMain.etDestination.setOnClickListener(v -> {
            displaySourceLocationList();
            mSourceLocationAdapter.setOnItemClickListener(pojo -> {
                sourceLocation = pojo;
                Timber.d("getInputText: %s", sourceLocation.getName());
                binding.contentMain.etDestination.setText(sourceLocation.getName());
                binding.contentMain.includeYourLocList.rvYourLocList.setVisibility(View.GONE);
            });
        });
    }

    // Add Watcher Destination EditText
    private void destinationTextWatcher() {
        binding.contentMain.includeDestinationList.tvDone.setVisibility(View.GONE);
        binding.contentMain.etDestination.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Visible Done TextView
                binding.contentMain.includeDestinationList.tvDone.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    /**
     * Loading from loading.xml
     */
    private void startLoading() {
        binding.contentMain.loading.loading.setVisibility(View.VISIBLE);
    }

    private void stopLoading() {
        binding.contentMain.loading.loading.setVisibility(View.GONE);
    }

    private void displaySourceLocationList() {
        binding.contentMain.includeYourLocList.rvYourLocList.setVisibility(View.VISIBLE);
        binding.contentMain.includeDestinationList.rvDestinationList.setVisibility(View.GONE);
    }

    private void displayDestinationLocationList() {
        binding.contentMain.includeYourLocList.rvYourLocList.setVisibility(View.GONE);
        binding.contentMain.includeDestinationList.rvDestinationList.setVisibility(View.VISIBLE);
    }

    private void defaultMode() {
        binding.contentMain.includeYourLocList.rvYourLocList.setVisibility(View.GONE);
        binding.contentMain.includeDestinationList.rvDestinationList.setVisibility(View.GONE);
    }


    @Override
    protected void onResume() {
        super.onResume();
        mSourceLocationViewModel.requestCurrentLocation(this);
        Timber.d("onResume: ");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;
        Timber.d("onMapReady:");
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}