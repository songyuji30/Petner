package com.jica.petner_yuji.Activity;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jica.petner_yuji.API.AnimalAPI;
import com.jica.petner_yuji.GlideApp;
import com.jica.petner_yuji.R;
import com.jica.petner_yuji.databinding.ActivityAnimalMapBinding;
import com.jica.petner_yuji.model.Animal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class AnimalMapActivity extends FragmentActivity implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback {

    private GoogleMap mMap;
    private Marker currentMarker = null;
    private AlertDialog dialog;


    private ActivityAnimalMapBinding binding;
    private String userID;
    Button btn_home, btn_upload, btn_protect, btn_profile;
    ImageView map_zoomout, map_zoomin;


    // onRequestPermissionsResult에서 수신된 결과에서 ActivityCompat.requestPermissions를 사용한 퍼미션 요청을 구별하기 위해 사용됩니다.
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    boolean needRequest = false;

    // 앱을 실행하기 위해 필요한 퍼미션을 정의합니다.
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};  // 외부 저장소


    // 파싱해온 값 담기
    ArrayList animalArrayList;

    Animal animal;
    LinearLayout here;
    String happenPlace, kindCd, specialMark, careNm, careAddr, careTel, popfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 액티비티에서 userID값 받아오기
        userID = getIntent().getStringExtra("userID");
        Log.i("받아온 main의 아디 값", userID);

        binding = ActivityAnimalMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //유기동물 정보창
        here = findViewById(R.id.here);

        btn_home = findViewById(R.id.btn_home);
        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent home = new Intent(AnimalMapActivity.this, MainActivity.class);
                // 메인 액티비티에 userID값 넘겨주기
                home.putExtra("userID", userID);
                startActivity(home);
            }
        });

        btn_upload = findViewById(R.id.btn_upload);
        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent post = new Intent(AnimalMapActivity.this, WritePostActivity.class);
                // 포스트작성 액티비티에 userID값 넘겨주기
                post.putExtra("userID", userID);
                startActivity(post);
            }
        });

        btn_protect = findViewById(R.id.btn_protect);
        btn_protect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent protect = new Intent(AnimalMapActivity.this, ShelterActivity.class);
                // 보호소 액티비티에 userID값 넘겨주기
                protect.putExtra("userID", userID);
                startActivity(protect);
            }
        });

        btn_profile = findViewById(R.id.btn_profile);
        btn_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent profile = new Intent(AnimalMapActivity.this, MyPageActivity.class);
                profile.putExtra("userID", userID);
                startActivity(profile);

            }
        });

        map_zoomin = findViewById(R.id.map_zoomin);
        map_zoomin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        map_zoomout = findViewById(R.id.map_zoomout);
        map_zoomout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Context context = this;

        //런타임 퍼미션 요청 대화상자나 GPS 활성 요청 대화상자 보이기전에
        //지도의 초기위치를 전주로 이동
        setDefaultLocation();

        //권한 얻기
        startLocationService();

        try {
            // 주소 파싱해오기
            AnimalAPI m = new AnimalAPI();
            animalArrayList = m.execute().get();
            System.out.println("파싱해온 값: " + animalArrayList);


        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        // 파싱한 주소로 지오코딩 이용하여 마커찍기
        for (int a = 0; a < animalArrayList.size() - 1; a++) {
            Location location = addrToPoint(context, animalArrayList, a);
            Log.d("", "위도랑 경도 값" + location.getLatitude() + ", " + location.getLongitude());

            MarkerOptions markerOptions = new MarkerOptions();
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

            //desertionNo 얻어오기
            String deNo = animalArrayList.get(a).toString().substring(20,35);

            markerOptions.position(latLng);
            BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.here);
            Bitmap b = bitmapdraw.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, 110, 110, false);
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
            markerOptions.title("I'm here ૮ ･ ﻌ･ა");    // 마커 옵션 추가
            mMap.addMarker(markerOptions).setTag(deNo);  // 마커 등록
            mMap.setOnMarkerClickListener(markerClickListener);
            mMap.setOnMapClickListener(mapClickListener);

        }
    }




    GoogleMap.OnMarkerClickListener markerClickListener = new GoogleMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(@NonNull Marker marker) {

            System.out.println("Marker의 Tag값: "+marker.getTag());
//            Toast.makeText(AnimalMapActivity.this, "마커를 누르셨습니다." ,Toast.LENGTH_SHORT).show();
            String deNo = (String) marker.getTag();

            //유기동물 정보창 뜨게 하기
            here.setVisibility(View.VISIBLE);
            
            TextView happenPlace = findViewById(R.id.happenPlace);
            TextView kindCd = findViewById(R.id.kindCd);
            TextView specialMark = findViewById(R.id.specialMark);
            TextView careNm = findViewById(R.id.careNm);
            TextView careAddr = findViewById(R.id.careAddr);
            TextView careTel = findViewById(R.id.careTel);
            ImageView popfile = findViewById(R.id.popfile);

            //정보 띄워주기
            animal = find(animalArrayList,deNo);
            happenPlace.setText(animal.getHappenPlace());    // 발견 장소
            kindCd.setText(animal.getKindCd());         // 품종
            specialMark.setText(animal.getSpecialMark());    // 특징
            careNm.setText(animal.getCareNm());         // 보호소 이름
            careAddr.setText(animal.getCareAddr());         // 보호소 주소
            careTel.setText(animal.getCareTel());        // 보호소 전화번호
            Uri uri = Uri.parse(animal.getPopfile());
            GlideApp.with(AnimalMapActivity.this).load(uri)
                    .error(R.drawable.frog)
                    .into(popfile);

            return false;
        }
    };

    GoogleMap.OnMapClickListener mapClickListener = new GoogleMap.OnMapClickListener() {
        @Override
        public void onMapClick(@NonNull LatLng latLng) {
            
            //정보창 다시 감추기
            here.setVisibility(View.GONE);
        }
    };

    private Animal find(ArrayList<Animal> animalArrayList, String deNo){

        int size = animalArrayList.size();
        for(int i=0; i<=size; i++) {
            String str = animalArrayList.get(i).getDesertionNo();
            if (deNo.equals(str)) {
                happenPlace= animalArrayList.get(i).getHappenPlace();    // 발견 장소
                kindCd=animalArrayList.get(i).getKindCd();         // 품종
                specialMark=animalArrayList.get(i).getSpecialMark();    // 특징
                careNm=animalArrayList.get(i).getCareNm();         // 보호소 이름
                careAddr=animalArrayList.get(i).getCareAddr();         // 보호소 이름
                careTel=animalArrayList.get(i).getCareTel();        // 보호소 전화번호
                popfile = animalArrayList.get(i).getPopfile();

                animal = new Animal(happenPlace, kindCd, specialMark, careNm, careAddr, careTel, popfile);
                Log.d("아아아아아 이미지는 왜 안떠", animalArrayList.get(i).getPopfile());
                break;
            }
        }
        return animal;

    }



    private void startLocationService() {

        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {

            // 2. 이미 퍼미션을 가지고 있음

        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다 -> 2가지 경우(3-1, 4-1)

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])) {

                AlertDialog.Builder builder = new AlertDialog.Builder(AnimalMapActivity.this);
                dialog = builder.setMessage("이 앱을 실행하려면 위치 접근 권한이 필요합니다.").setNegativeButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                        ActivityCompat.requestPermissions(AnimalMapActivity.this, REQUIRED_PERMISSIONS,
                                PERMISSIONS_REQUEST_CODE);

                    }
                }).create();
                dialog.show();

            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_REQUEST_CODE && grantResults.length == REQUIRED_PERMISSIONS.length) {
            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면
            boolean check_result = true;

            // 모든 퍼미션을 허용했는지 체크합니다.
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }

            if (check_result) {
                // 퍼미션을 허용
            } else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다. 2가지 경우가 있습니다.
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {

                    // 사용자가 거부만 선택한 경우에는 앱을 다시 실행하여 허용을 선택하면 앱을 사용할 수 있습니다.
                    AlertDialog.Builder builder = new AlertDialog.Builder(AnimalMapActivity.this);
                    dialog = builder.setMessage("퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요. ").setNegativeButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }).create();
                    dialog.show();

                } else {
                    // "다시 묻지 않음"을 사용자가 체크하고 거부를 선택한 경우에는 설정(앱 정보)에서 퍼미션을 허용해야 앱을 사용할 수 있습니다.
                    AlertDialog.Builder builder = new AlertDialog.Builder(AnimalMapActivity.this);
                    dialog = builder.setMessage("퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ").setNegativeButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }).create();
                    dialog.show();
                }
            }

        }
    }

    public void setDefaultLocation() {

        //디폴트 위치, Jeonju 시청
        LatLng DEFAULT_LOCATION = new LatLng(35.824125820574636, 127.14818761080556);
//        String markerTitle = "위치정보 가져올 수 없음";
//        String markerSnippet = "위치 퍼미션과 GPS 활성 여부 확인하세요";
//
//        if (currentMarker != null) currentMarker.remove();
//
//        MarkerOptions markerOptions = new MarkerOptions();
//        markerOptions.position(DEFAULT_LOCATION);
//        markerOptions.title(markerTitle);
//        markerOptions.snippet(markerSnippet);
//        markerOptions.draggable(true);
//        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
//        currentMarker = mMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 13);
        mMap.moveCamera(cameraUpdate);

    }

    // 지오코딩(주소 -> 위도, 경도 값 찾기)
    private Location addrToPoint(Context context, ArrayList<Animal> animalArrayList, int a) {

        Location location = new Location("");
        Geocoder geocoder = new Geocoder(context);
        List<Address> addresses = null;
        String str;

        str = "전주"+animalArrayList.get(a).getHappenPlace();
        System.out.println("발견 장소:" + str);

        try {
            addresses = geocoder.getFromLocationName(str, 2);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses != null) {
            for (int i = 0; i < addresses.size(); i++) {
                Address lating = addresses.get(i);
                location.setLatitude(lating.getLatitude());
                location.setLongitude(lating.getLongitude());
            }
        }
        return location;
    }


}