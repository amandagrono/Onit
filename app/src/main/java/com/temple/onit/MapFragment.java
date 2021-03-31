package com.temple.onit;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mapAPI;
    private Button button;
    private MapFragmentInterface smartAlarmActivity;
    private LatLng currentLatLng;

    public MapFragment() {
        // Required empty public constructor

    }


    public static MapFragment newInstance(String param1, String param2) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(getActivity() != null){

            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            button = view.findViewById(R.id.save_alarm_button);
            if(mapFragment != null) {
                mapFragment.getMapAsync(this);
            }
            button.setOnClickListener(v -> {
                if(!(currentLatLng == null)){
                    smartAlarmActivity.saveLocation(currentLatLng);
                }
            });
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof MapFragmentInterface){
            smartAlarmActivity = (MapFragmentInterface) context;
        }
        else{
            throw new ClassCastException("Must Implement MapFragmentInterface");
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapAPI = googleMap;
        LatLng temple = new LatLng(39.981142, -75.156161);
        mapAPI.addMarker(new MarkerOptions().position(temple).title("Temple"));

        CameraUpdate center = CameraUpdateFactory.newLatLngZoom(temple, 15);
        mapAPI.animateCamera(center);
        Log.d("Map Ready", "Map Ready");

        mapAPI.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                Log.d("LatLng", latLng.toString());
                mapAPI.clear();
                mapAPI.addMarker(new MarkerOptions().position(latLng));
                currentLatLng = latLng;
            }
        });
    }
    interface MapFragmentInterface{
        void saveLocation(LatLng latLng);
    }
}