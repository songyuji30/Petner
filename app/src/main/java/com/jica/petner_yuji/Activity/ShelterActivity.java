package com.jica.petner_yuji.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.jica.petner_yuji.API.AnimalAPI;
import com.jica.petner_yuji.API.ShelterAPI;
import com.jica.petner_yuji.Adapter.RecyclerViewHeight;
import com.jica.petner_yuji.Adapter.ShelterAdapter;
import com.jica.petner_yuji.R;
import com.jica.petner_yuji.model.Shelter;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class ShelterActivity extends AppCompatActivity {


    private ArrayList<Shelter> shelterArrayList;
    ShelterAdapter shelterAdapter;
    RecyclerView shelter_list;

    private String userID;

    Button btn_home, btn_upload, btn_notice, btn_profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shelter);

        // 액티비티에서 userID값 받아오기
        userID = getIntent().getStringExtra("userID");
        Log.i("받아온 main의 아디 값", userID);


        btn_home = findViewById(R.id.btn_home);
        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent home = new Intent(ShelterActivity.this, MainActivity.class);
                // 메인 액티비티에 userID값 넘겨주기
                home.putExtra("userID", userID);
                startActivity(home);
            }
        });

        btn_upload = findViewById(R.id.btn_upload);
        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent post = new Intent(ShelterActivity.this, WritePostActivity.class);
                // 포스트작성 액티비티에 userID값 넘겨주기
                post.putExtra("userID", userID);
                startActivity(post);
            }
        });

        btn_notice = findViewById(R.id.btn_notice);
        btn_notice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent notice = new Intent(ShelterActivity.this, AnimalMapActivity.class);
                // 포스트작성 액티비티에 userID값 넘겨주기
                notice.putExtra("userID", userID);
                startActivity(notice);
            }
        });

        btn_profile = findViewById(R.id.btn_profile);
        btn_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 마이페이지 액티비티에 userID값 넘겨주기
                Intent profile = new Intent(ShelterActivity.this, MyPageActivity.class);
                profile.putExtra("userID", userID);
                startActivity(profile);

            }
        });



        try {
            // 주소 파싱해오기
            ShelterAPI shelterAPI = new ShelterAPI();
            shelterArrayList = shelterAPI.execute().get();
            System.out.println("파싱해온 값: " + shelterArrayList);

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }




        shelter_list = findViewById(R.id.shelter_list);
        shelterAdapter = new ShelterAdapter(shelterArrayList);
        shelter_list.setAdapter(shelterAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        shelter_list.setLayoutManager(linearLayoutManager);
        shelter_list.addItemDecoration(new RecyclerViewHeight(5));



    }
}