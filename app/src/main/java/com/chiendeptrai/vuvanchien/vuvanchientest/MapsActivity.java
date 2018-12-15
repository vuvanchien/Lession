package com.chiendeptrai.vuvanchien.vuvanchientest;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Marker markers;
    private Database database;
    private Cursor cursor;
    private ArrayList<com.chiendeptrai.vuvanchien.vuvanchientest.LatLng> latLngs;
    private Button btnThem, btnSua, btnXoa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        btnThem = findViewById(R.id.btnthem);
        btnSua = findViewById(R.id.btnSua);
        btnXoa = findViewById(R.id.btnXoa);

        btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
add();
            }
        });
        btnSua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        btnXoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        database = new Database(this);
        latLngs = new ArrayList<>();
        latLngs.clear();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
//
//        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        cursor = database.getdata();
        if (cursor.moveToNext()) {
            cursor.moveToFirst();
            do {
                final LatLng sydney = new LatLng(Double.parseDouble(cursor.getString(1)), Double.parseDouble(cursor.getString(2)));
                latLngs.add(new com.chiendeptrai.vuvanchien.vuvanchientest.LatLng(cursor.getInt(0),cursor.getString(3)));
                mMap.getUiSettings().setZoomControlsEnabled(true);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15));
                mMap.addMarker(new MarkerOptions().position(sydney).title(cursor.getString(3)));
                Log.e("POSITION", cursor.getString(0));
            } while (cursor.moveToNext());

        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker.equals(markers)) {
                    return true;
                }
                return false;
            }
        });
    }

    public void Sua(final Marker marker) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialogsua);
        final EditText edtnames;
        final EditText edtvido;
        final EditText edtkingdo;
        final Button btndialogSua;
        final Button btndialogHuy;

        edtnames = dialog.findViewById(R.id.edtnames);
        edtvido = dialog.findViewById(R.id.edtvido);
        edtkingdo = dialog.findViewById(R.id.edtkingdo);
        btndialogSua = dialog.findViewById(R.id.btndialogSua);
        btndialogHuy = dialog.findViewById(R.id.btndialogHuy);
        final LatLng latLng = marker.getPosition();
        String n = marker.getId().substring(1);
        edtvido.setText(latLng.latitude + "");
        edtkingdo.setText(latLng.longitude + "");
        edtnames.setText(latLngs.get(Integer.parseInt(n)).getName());
        btndialogSua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String latitude = edtvido.getText().toString();
                String longitude = edtkingdo.getText().toString();
                String name = edtnames.getText().toString();
                String index = marker.getId().substring(1);
                if (latitude.equals("") || longitude.equals("") || name.equals("")) {
                    Toast.makeText(MapsActivity.this, "Nhap du lieu! khong duoc bo trong!!!", Toast.LENGTH_SHORT).show();
                } else {
                    final LatLng sydney1 = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                    if (!latLng.toString().equals(sydney1.toString())) {
                        database.update(name, latitude, longitude, latLngs.get(Integer.parseInt(index)).getId());
                        mMap.addMarker(new MarkerOptions().position(sydney1).title(cursor.getColumnName(3)));
                        Intent intent = new Intent(MapsActivity.this, MainActivity.class);
                        startActivity(intent);
                        Toast.makeText(MapsActivity.this, "Sửa thành công !", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MapsActivity.this, "Nhap du!", Toast.LENGTH_SHORT).show();
                    }

                }
                dialog.dismiss();
                btndialogHuy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();

                    }
                });
            }

        });

    }
    public void add(){
        final EditText edtkd = findViewById(R.id.edtLongitube);
        final EditText edtvd = findViewById(R.id.edtLatitude);
        final EditText edtname = findViewById(R.id.edtname);

        String latitude = edtkd.getText().toString();
        String longitude = edtvd.getText().toString();
        String namee = edtname.getText().toString();
        cursor = database.getdata();
        if (!latitude.isEmpty() && !longitude.isEmpty() && !namee.isEmpty()) {
            final LatLng sydney1 = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
            boolean a = false;
            if (cursor.moveToNext()) {
                cursor.moveToFirst();
                do {
                    final LatLng sydney = new LatLng(Double.parseDouble(cursor.getString(1)), Double.parseDouble(cursor.getString(2)));
                    if (sydney.toString().equals(sydney1.toString())) {
                        a = true;
                        break;
                    }
                } while (cursor.moveToNext());

            }
            if (a == false) {
                mMap.addMarker(new MarkerOptions().position(sydney1).title(cursor.getColumnName(3)));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney1));
                database.insert(namee,latitude, longitude);
                Toast.makeText(MapsActivity.this, "Thêm thành công", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MapsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        } else {
            Toast.makeText(MapsActivity.this, "Khong bo chong!!", Toast.LENGTH_SHORT).show();
        }
    }
}
