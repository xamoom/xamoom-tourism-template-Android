package com.android.xamoom.tourismtemplate.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.xamoom.tourismtemplate.utils.Analytics.GoogleAnalyticsSender;
import com.android.xamoom.tourismtemplate.utils.ExtensionsKt;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.gms.analytics.Tracker;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.appcompat.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.xamoom.tourismtemplate.Globals;
import com.android.xamoom.tourismtemplate.R;
import com.android.xamoom.tourismtemplate.modules.AppModule;
import com.android.xamoom.tourismtemplate.modules.DaggerMapFragmentComponent;
import com.android.xamoom.tourismtemplate.modules.MapFragmentModul;
import com.android.xamoom.tourismtemplate.utils.Analytics.AnalyticsUtil;
import com.android.xamoom.tourismtemplate.utils.ApiCallback;
import com.android.xamoom.tourismtemplate.utils.ApiUtil;
import com.android.xamoom.tourismtemplate.utils.BestLocationListener;
import com.android.xamoom.tourismtemplate.utils.BestLocationProvider;
import com.android.xamoom.tourismtemplate.view.presenter.MapFragmentContract;
import com.android.xamoom.tourismtemplate.view.presenter.MapFragmentPresenter;
import com.bumptech.glide.Glide;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.xamoom.android.xamoomcontentblocks.ViewHolders.ContentBlock9ViewHolderUtils;
import com.xamoom.android.xamoomsdk.Helpers.ColorHelper;
import com.xamoom.android.xamoomsdk.Resource.Content;
import com.xamoom.android.xamoomsdk.Resource.Spot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import javax.inject.Inject;

import at.rags.morpheus.Error;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MapFragment extends Fragment implements MapFragmentContract.View, com.mapbox.mapboxsdk.maps.OnMapReadyCallback {

  @BindView(R.id.map_center_bounds_button) FloatingActionButton centerBoundsButton;
  @BindView(R.id.map_center_map_button) FloatingActionButton centerButton;
  @BindView(R.id.map_detail_view) View mapDetailView;
  @BindView(R.id.map_detail_title_text_view) TextView mapDetailTitleTextView;
  @BindView(R.id.map_detail_image_view) ImageView mapDetailImageView;
  @BindView(R.id.map_detail_description_text_view) TextView mapDetailDescriptionTextView;
  @BindView(R.id.map_detail_more_button) Button mapDetailMoreButton;
  @BindView(R.id.map_detail_navigation_button) Button mapDetailNavigateButton;
  //@BindView(R.id.filter_button) Button filterButton;
  @BindView(R.id.map_progress_bar) ProgressBar progressBar;
  @BindView(R.id.mapView) MapView mapView;

  @Inject MapFragmentPresenter presenter;
  @Inject BestLocationProvider locationProvider;
  @Inject SharedPreferences sharedPreferences;
  @Inject Tracker mTracker;

  private MapFragmentListener listener;
  private Location currentLocation;
  private ArrayList<Spot> spots;
  private BottomSheetBehavior bottomSheetBehavior;
  private Bitmap customMarker;
  private Spot activeSpot = null;
  private MapboxMap mapboxMap = null;
  private boolean closeBottomSheet = false;
  private boolean reloadSpot = true;
  private Style mapboxStyle = null;
  private static final int REQUEST_LOCATION = 0;

  public MapFragment() {
    // Required empty public constructor
  }

  public static MapFragment newInstance() {
    MapFragment fragment = new MapFragment();
    Bundle args = new Bundle();
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    AnalyticsUtil.Companion.reportContentView("Map", Globals.ANALYTICS_CONTENT_TYPE_SCREEN, "",
            null);
  }

  @SuppressLint("ClickableViewAccessibility")
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_map, container, false);

    DaggerMapFragmentComponent.builder()
            .mapFragmentModul(new MapFragmentModul(this, getContext()))
            .appModule(new AppModule(getContext()))
            .build()
            .inject(this);
    new GoogleAnalyticsSender(mTracker).reportContentView("Android Map screen", "", "", null);new GoogleAnalyticsSender(mTracker).reportContentView("Android Filter screen", "", "", null);

    ButterKnife.bind(this, view);

    mapDetailView.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        mapDetailView.onTouchEvent(event);
        return true;
      }
    });

    bottomSheetBehavior = BottomSheetBehavior.from(mapDetailView);
    bottomSheetBehavior.setHideable(true);
    bottomSheetBehavior.setSkipCollapsed(true);
    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    bottomSheetBehavior.setBottomSheetCallback(bottomSheetCallback);

    mapView.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        v.onTouchEvent(event);

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
          closeBottomSheet = true;
        }

        if (event.getAction() == MotionEvent.ACTION_MOVE) {
          closeBottomSheet = true;
        }

        if (event.getAction() == MotionEvent.ACTION_UP) {
          if (closeBottomSheet) {
            hideDetailWindow();
          } else {
            closeBottomSheet = true;
          }
        }
        return true;
      }
    });

    centerButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        centerOnUser();
      }
    });

    centerBoundsButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        centerMapToSpotBounds();
        hideDetailWindow();
      }
    });

    /*
    filterButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        listener.openFilterActivity(presenter.getMapFilters(), presenter.getSelectedTags());
      }
    });
    */

    customizeMapDetailButtons();

    return view;
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    mapView.onCreate(savedInstanceState);
  }

  @Override
  public void onStart() {
    super.onStart();
    locationProvider.startLocationUpdatesWithListener(locationListener);
    mapView.onStart();
  }

  @Override
  public void onResume() {
    super.onResume();

    if (reloadSpot) {
      hideDetailWindow();

      progressBar.setVisibility(View.VISIBLE);
      presenter.downloadSpots();
    }

    setCurrentLocation();

    reloadSpot = true;

    mapView.onResume();
  }

  @Override
  public void onPause() {
    super.onPause();
    mapView.onPause();
  }

  @Override
  public void onStop() {
    super.onStop();
    mapView.onStop();
    locationProvider.stopLocationUpdates();
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    mapView.onDestroy();
  }

  @Override
  public void onSaveInstanceState(@NonNull Bundle outState) {
    super.onSaveInstanceState(outState);
    mapView.onSaveInstanceState(outState);
  }

  @Override
  public void onLowMemory() {
    super.onLowMemory();
    mapView.onLowMemory();
  }

  private void setCurrentLocation() {

    Drawable icon = this.getActivity().getResources().getDrawable(R.drawable.ic_user_location);
    Drawable newIcon = icon.getConstantState().newDrawable();
    newIcon.mutate().setColorFilter(Color.parseColor("#D3D3D3"), PorterDuff.Mode.SRC_ATOP);

    if (currentLocation != null) {
      newIcon.mutate().setColorFilter(getActivity().getResources().getColor(R.color.color_map_button), PorterDuff.Mode.SRC_ATOP);
    }

    centerButton.setImageDrawable(newIcon);
  }

  private BottomSheetBehavior.BottomSheetCallback bottomSheetCallback = new BottomSheetBehavior.BottomSheetCallback() {
    private float lastOffset;

    @Override
    public void onStateChanged(@NonNull View bottomSheet, int newState) {

    }

    @Override
    public void onSlide(@NonNull View bottomSheet, float slideOffset) {
      if (lastOffset == 0.0) {
        lastOffset = slideOffset;
        return;
      }

      lastOffset = slideOffset;
    }
  };

  private BestLocationListener locationListener = new BestLocationListener() {
    @Override
    public void onLocationUpdate(Location location, BestLocationProvider.LocationType type, boolean isFresh) {
      currentLocation = location;
      setCurrentLocation();

      if (mapboxStyle != null && currentLocation != null) {
        enableLocationComponent(mapboxStyle);
      }
    }

    @Override
    public void onLocationUpdateTimeoutExceeded(BestLocationProvider.LocationType type) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
  };

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof MapFragmentListener) {
      listener = (MapFragmentListener) context;
    } else {
      throw new RuntimeException(context.toString()
              + " must implement MapFragmentListener");
    }
  }

  @Override
  public void onDetach() {
    super.onDetach();
    listener = null;
  }

  @Override
  public void onMapReady(@NonNull final MapboxMap mapboxMap) {
    this.mapboxMap = mapboxMap;
    mapboxMap.setStyle(new Style.Builder().fromUri(Globals.MAPBOX_STYLE_URL), new Style.OnStyleLoaded() {
      @Override
      public void onStyleLoaded(@NonNull Style style) {

        mapboxStyle = style;
        final ArrayList<com.mapbox.mapboxsdk.annotations.Marker> markers = new ArrayList();
        if (customMarker == null) {
          customMarker = getBitmapFromVectorDrawable(getContext(), R.drawable.default_marker);
          customMarker = Bitmap.createScaledBitmap(customMarker, (int) dipToPixels(getContext(), 20),
                  (int) dipToPixels(getContext(), 30), false);
        }

        for (Spot spot : spots) {
          MarkerOptions marker = new MarkerOptions()
                  .setPosition(new LatLng(spot.getLocation().getLatitude(), spot.getLocation().getLongitude()))
                  .setTitle(spot.getName())
                  .setIcon(IconFactory.getInstance(getContext()).fromBitmap(customMarker));
          markers.add(mapboxMap.addMarker(marker));
        }

        centerMapToSpotBounds();

        mapboxMap.setOnMarkerClickListener(new MapboxMap.OnMarkerClickListener() {
          @Override
          public boolean onMarkerClick(@NonNull Marker marker) {
            int index = markers.indexOf(marker);
            Spot selectedSpot = spots.get(index);
            showSpotDetail(selectedSpot);
            return true;
          }
        });

        enableLocationComponent(style);
      }
    });
  }

  @SuppressWarnings( {"MissingPermission"})
  private void enableLocationComponent(@NonNull Style loadedMapStyle) {
    if (PermissionsManager.areLocationPermissionsGranted(getContext())) {
      LocationComponent locationComponent = mapboxMap.getLocationComponent();

      locationComponent.activateLocationComponent(getContext(), loadedMapStyle);

      locationComponent.setLocationComponentEnabled(true);
    }
  }

  private void customizeMapDetailButtons() {
    mapDetailMoreButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.color_primary)));
    mapDetailNavigateButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.color_primary)));
    mapDetailMoreButton.setTextColor(ColorHelper.getInstance().getBarFontColor());
    mapDetailNavigateButton.setTextColor(ColorHelper.getInstance().getBarFontColor());
  }

  private void centerMapToSpotBounds() {
    if (spots.size() > 1) {
      mapboxMap.animateCamera(ContentBlock9ViewHolderUtils.zoomToDisplayAllSpots(spots, 50));
    } else {
      if(spots.size() > 0) {
        Spot spot = spots.get(0);
        CameraPosition position = new CameraPosition.Builder().target(new LatLng(spot.getLocation().getLatitude(), spot.getLocation().getLongitude())).zoom(16.0).tilt(0.0).build();
        mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));
      }
    }
  }

  private void showSpotDetail(final Spot spot) {
    activeSpot = spot;

    CameraPosition position = new CameraPosition.Builder().target(new LatLng(spot.getLocation().getLatitude(), spot.getLocation().getLongitude())).zoom(16.0).tilt(0.0).build();
    mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));

    mapDetailTitleTextView.setText(spot.getName());
    mapDetailDescriptionTextView.setText(spot.getDescription());

    String imageUrl = spot.getPublicImageUrl();
    if(imageUrl != null) {
      if(imageUrl.endsWith(".gif")) {
        Glide.with(getContext())
                .load(imageUrl)
                .asGif()
                .placeholder(R.drawable.placeholder)
                .into(mapDetailImageView);

      } else {
        Glide.with(getContext())
                .load(imageUrl)
                .asBitmap()
                .placeholder(R.drawable.placeholder)
                .into(new SimpleTarget<Bitmap>() {
                  @RequiresApi(api = Build.VERSION_CODES.P)
                  @Override
                  public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    int size = ExtensionsKt.sizeOf(resource);
                    if(size > 2000000) {
                      mapDetailImageView.setImageBitmap(ExtensionsKt.getResizedBitmap(resource, 1000));
                    } else mapDetailImageView.setImageBitmap(resource);
                  }
                });
      }
    } else {
      Glide.with(getContext())
              .load(R.drawable.placeholder)
              .asBitmap()
              .into(mapDetailImageView);
    }

    if (spot.getContent() != null && spot.getContent().getId() != null) {
      mapDetailMoreButton.setVisibility(View.VISIBLE);
      String contentId = spot.getContent().getId();
      mapDetailMoreButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          reloadSpot = false;
          listener.didClickContent(spot.getContent());
        }
      });
    } else {
      mapDetailMoreButton.setVisibility(View.GONE);
    }

    mapDetailNavigateButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        navigateToSpot(spot);
      }
    });

    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
  }

  private void navigateToSpot(Spot spot) {
    double lat = spot.getLocation().getLatitude();
    double lon = spot.getLocation().getLongitude();

    String mode = "";

    int prefNavigation = sharedPreferences.getInt(Globals.PREF_NAVIGATION_PREFERRED_TYPE, 0);
    switch (prefNavigation) {
      case Globals.PREF_NAVIGATION_TYPE_WALKING:
        mode = "w";
        break;
      case Globals.PREF_NAVIGATION_TYPE_BIKE:
        mode = "b";
        break;
      case Globals.PREF_NAVIGATION_TYPE_CAR:
        mode = "d";
        break;
    }

    String urlString = String.format(Locale.US, "google.navigation:q=%f,%f&mode=%s", lat, lon, mode);
    Uri gmmIntentUri = Uri.parse(urlString);
    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
    mapIntent.setPackage("com.google.android.apps.maps");
    if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
      startActivity(mapIntent);
    }
  }

  private void centerOnUser() {

      int fineLocationPermission = ContextCompat.checkSelfPermission(this.getActivity(),
              Manifest.permission.ACCESS_FINE_LOCATION);
      int locationPermission = ContextCompat.checkSelfPermission(this.getActivity(),
              Manifest.permission.ACCESS_COARSE_LOCATION);

      if (fineLocationPermission != PackageManager.PERMISSION_GRANTED
              && locationPermission != PackageManager.PERMISSION_GRANTED) {
        Drawable icon = this.getActivity().getResources().getDrawable(R.drawable.ic_user_location);
        Drawable newIcon = icon.getConstantState().newDrawable();
        newIcon.mutate().setColorFilter(Color.parseColor("#D3D3D3"), PorterDuff.Mode.SRC_ATOP);
        centerButton.setImageDrawable(newIcon);
      }

      if (fineLocationPermission != PackageManager.PERMISSION_GRANTED && locationPermission != PackageManager.PERMISSION_GRANTED) {
        if (ActivityCompat.checkSelfPermission(this.getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this.getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
          requestLocationPermission();
        } else {
          CameraPosition position = new CameraPosition.Builder().target(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())).zoom(16.0).tilt(0.0).build();
          mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));
        }
      } else {
        if (currentLocation != null) {
          CameraPosition position = new CameraPosition.Builder().target(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())).zoom(16.0).tilt(0.0).build();
          mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));
        } else {
          AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
          builder.setTitle(getString(R.string.no_location_alert_title));
          builder.setMessage(R.string.no_location_alert_message);
          // Add the buttons
          builder.setPositiveButton(R.string.no_location_alert_settings, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
              startActivityForResult(new Intent(Settings.ACTION_SETTINGS), 0, null);
            }
          });
          builder.setNegativeButton(R.string.no_location_alert_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
              // User cancelled the dialog
            }
          });
          AlertDialog dialog = builder.create();
          dialog.show();
        }
      }
      return;
  }

  //Permissions
  private void requestLocationPermission() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
      return;
    }

    if (ActivityCompat.shouldShowRequestPermissionRationale(this.getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) &&
            ActivityCompat.shouldShowRequestPermissionRationale(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
      //will displayed if the user denied the permission first time.

      AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
      builder.setTitle(getString(R.string.alert_permission_location_title));
      builder.setMessage(R.string.alert_permission_location_message);
      // Add the buttons
      builder.setPositiveButton(R.string.alert_permission_location_ok_button, new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int id) {
          requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                  REQUEST_LOCATION);
        }
      });
      builder.setNegativeButton(R.string.alert_permission_location_cancel_button, new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int id) {
          // User cancelled the dialog
        }
      });
      AlertDialog dialog = builder.create();
      dialog.show();
    } else {
      // Location permission has not been granted yet. Request it directly.
      this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
              REQUEST_LOCATION);
    }
  }

  public float dipToPixels(Context context, float dipValue) {
    DisplayMetrics metrics = context.getResources().getDisplayMetrics();
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
  }

  public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
    Drawable drawable = ResourcesCompat.getDrawable(context.getResources(), drawableId, null);

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
      drawable = (DrawableCompat.wrap(drawable)).mutate();
    }

    Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
            drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(bitmap);
    drawable.setBounds(0, 0, canvas.getWidth(), drawable.getIntrinsicHeight());
    drawable.draw(canvas);

    return bitmap;
  }

  private void hideDetailWindow() {
    activeSpot = null;
    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
  }

  public void setSelectedTags(ArrayList<String> selectedTags) {
    presenter.setSelectedTags(selectedTags);
    AnalyticsUtil.Companion.reportCustomEvent("Filter", "Changed filters", null, null);
  }

  private void initMap() {
    mapView.getMapAsync(this);
  }

  @Override
  public void didDownloadSpots(ArrayList<Spot> spots) {
    this.spots = spots;
    progressBar.setVisibility(View.GONE);

    if (spots.size() > 0) {
      ApiUtil.getInstance().loadStyle(spots.get(0).getSystem().getId(), new ApiCallback.ObjectCallback<com.xamoom.android.xamoomsdk.Resource.Style, Error>() {
        @Override
        public void finish(com.xamoom.android.xamoomsdk.Resource.Style result, Error error) {

          if (getActivity() == null || getActivity().getApplicationContext() == null) {
            return;
          }

          customMarker = ContentBlock9ViewHolderUtils.getIcon(result.getCustomMarker(), getActivity().getApplicationContext());
          initMap();
        }
      });
    } else {
      initMap();
    }
  }

  @Override
  public void startLoading() {
    progressBar.setVisibility(View.VISIBLE);
  }

  @Override
  public void stopLoading() {
    progressBar.setVisibility(View.GONE);
  }

  @Override
  public void didLoadStyle() {
    mapboxMap.clear();
    presenter.downloadSpots();
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    switch (requestCode) {
      case REQUEST_LOCATION:
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          if (ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationProvider.startLocationUpdatesWithListener(locationListener);
          }
        }
        break;
      default:
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
  }

  public interface MapFragmentListener {
    void didClickContent(Content content);
    void openFilterActivity(HashMap<String, HashMap<String, String>> mapFilter, ArrayList<String> selectedTags);
  }
}
