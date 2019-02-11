package com.iquitosplay.dellatemarker;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback , GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;

    private Marker marcador;
    private static final float camera_zoom = 15;

    ImageView imgmarker;
    private BottomSheetBehavior mBottomSheetBehavior1;
    LinearLayout tapactionlayout;
    View white_forground_view;
    View bottomSheet;
    TextView txtnombre_local, txtDireccion, txtHorario;

    ArrayList<Puntos> listaPuntos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        View headerLayout1 = findViewById(R.id.bottomJsoft);
        imgmarker = (ImageView) findViewById(R.id.ImgMarker);

        txtnombre_local = (TextView) findViewById(R.id.txtNombreLocal);
        txtDireccion = (TextView) findViewById(R.id.txtDireccion);
        txtHorario = (TextView) findViewById(R.id.txtHorario);
        tapactionlayout = (LinearLayout) findViewById(R.id.tap_action_layout);
        bottomSheet = findViewById(R.id.bottomJsoft);
        mBottomSheetBehavior1 = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior1.setPeekHeight(120);
        mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
        mBottomSheetBehavior1.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    tapactionlayout.setVisibility(View.VISIBLE);
                }
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    tapactionlayout.setVisibility(View.GONE);
                }
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    tapactionlayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });

        tapactionlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBottomSheetBehavior1.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }
        });


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

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        ObtenerPuntos();
    }


    private void ObtenerPuntos() {

        class getResult extends AsyncTask<Void, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(MapsActivity.this, "Cargando", "Por favor espere...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                showResult(s);
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                HashMap<String, String> params2 = new HashMap<>();
                //      params2.put("usuario",txtuser.getText().toString());
                //    params2.put("clave",txtclave.getText().toString());
                String s = rh.sendGetRequest("http://fundamazonia.org/escaneamepe/jsonLugares.php");// SEDES
                return s;
            }
        }
        getResult ge = new getResult();
        ge.execute();

    }

    private void showResult(String json) {
        JSONArray result = null;
        try {
            listaPuntos = new ArrayList<>();
            result = new JSONArray(json);

            if (result != null) {
                for (int i = 0; i < result.length(); i++) {
                    JSONObject jsonObject = result.getJSONObject(i);
                    Puntos puntos = new Puntos(jsonObject);
                    listaPuntos.add(puntos);
                }

                CargarPuntosAMapa();

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @TargetApi(Build.VERSION_CODES.N)
    private void CargarPuntosAMapa() {

        mMap.clear();

        if (listaPuntos.size() > 0) {

            LatLng ultpos = null;
            for (int i = 0; i < listaPuntos.size(); i++) {

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(new LatLng((Double.parseDouble(listaPuntos.get(i).getLatitud()))
                        , (Double.parseDouble(listaPuntos.get(i).getLongitud()))))
                        .title(listaPuntos.get(i).getNombreLugar());
                //.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));

                Marker marcador_ = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng((Double.parseDouble(listaPuntos.get(i).getLatitud()))
                                , (Double.parseDouble(listaPuntos.get(i).getLongitud()))))
                        //   .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
                        .title(listaPuntos.get(i).getNombreLugar())

                );
                marcador_.setTag(listaPuntos.get(i));
                //  marcador.setTag(i);

                ultpos = new LatLng((Double.parseDouble(listaPuntos.get(i).getLatitud()))
                        , (Double.parseDouble(listaPuntos.get(i).getLongitud())));
            }

            VolverPosicion(ultpos);

        } else {
            Toast.makeText(this, "Lo sentimos, no tenemos agentes en la Unidad Seleccionada", Toast.LENGTH_SHORT).show();
        }
        mMap.setOnMarkerClickListener(this);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        try {
            Integer clickCount = (Integer) marker.getTag();
            //  return true;

        } catch (Exception ex) {
            Puntos info = new Puntos();
            info = (Puntos) marker.getTag();
            String url__img = info.getImagenLugar();
            Glide.with(this)
                    .load(url__img)//Config.URL_IMAGES_PUBLIC + url__img+".jpg")
                    .crossFade()
                    .centerCrop()
                    .placeholder(R.drawable.placeholder)
                    //.diskCacheStrategy(DiskCacheStrategy.ALL)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(imgmarker);

            txtnombre_local.setText(info.getNombreLugar());
            txtDireccion.setText(info.getDescripcionLugar());
            txtHorario.setText(info.getDescripcionLugar());
            //, txtDireccion, txtHorario
            mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_EXPANDED);

        }
        return false;
    }
    private void VolverPosicion(LatLng miLatLng) {

        //     mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        CameraPosition camPos = new CameraPosition.Builder()
                .target(miLatLng)   //Centramos el mapa en Madrid
                .zoom(camera_zoom)         //Establecemos el zoom en 19
                .bearing(45)      //Establecemos la orientación con el noreste arriba
                .tilt(70)         //Bajamos el punto de vista de la cámara 70 grados
                .build();

        CameraUpdate miUbicacion = CameraUpdateFactory.newCameraPosition(camPos);
        mMap.animateCamera(miUbicacion);
    }
}
