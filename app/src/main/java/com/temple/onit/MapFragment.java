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
import android.widget.TextView;

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
    private TextView textView;
    private MapFragmentInterface parentActivity;
    private LatLng currentLatLng;
    private int state = 0;
    private String textViewText = "";

    public MapFragment() {
        // Required empty public constructor

    }


    public static MapFragment newInstance(int state, LatLng latLng) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putInt("state", state);
        args.putParcelable("latlng", latLng);
        fragment.setArguments(args);
        return fragment;
    }
    public static MapFragment newInstance(int state){
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putInt("state", state);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            state = getArguments().getInt("state");
            switch (state){
                case 0:
                    textViewText = "Please Select Destination Location";
                    break;
                case 1:
                    textViewText = "Please Select Starting Location";
                    break;
                case 2:
                    textViewText = "Please select reminder location";
            }
            if(getArguments().getParcelable("latlng") != null){
                this.currentLatLng = (LatLng) getArguments().getParcelable("latlng");
            }
        }



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
                    parentActivity.saveLocation(currentLatLng, state);
                }
            });
            textView = view.findViewById(R.id.mapTextView);
            textView.setText(textViewText);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof MapFragmentInterface){
            parentActivity = (MapFragmentInterface) context;
        }
        else{
            throw new ClassCastException("Must Implement MapFragmentInterface");
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapAPI = googleMap;
        LatLng temple = new LatLng(39.981142, -75.156161);

        if(currentLatLng == null){
            currentLatLng = temple;
        }
        mapAPI.addMarker(new MarkerOptions().position(currentLatLng));

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
    public interface MapFragmentInterface{
        void saveLocation(LatLng latLng, int state);
    }
}