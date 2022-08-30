package com.jica.petner_yuji.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.jica.petner_yuji.Adapter.RecyclerMainAdapter;
import com.jica.petner_yuji.Adapter.RecyclerViewHeight;
import com.jica.petner_yuji.R;
import com.jica.petner_yuji.model.Post;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    RecyclerMainAdapter recyclerMainAdapter;
    ArrayList<Post> list_itemArrayList;
    RecyclerView recyclerView;
    private AlertDialog dialog;

    String userID;

    // 파이어베이스 데이터베이스
    private FirebaseDatabase database;

    //DatabaseReference는 데이터베이스의 특정 위치로 연결하는 거라고 생각하면 된다.
    private DatabaseReference databasePostlist;

    ImageButton menu;
    TextView tw_title;
    Button btn_notice, btn_upload, btn_protect, btn_profile;
    private Object String;

    @Override
    protected void onStart() {
        super.onStart();
      refresh();
    }

    private void refresh(){
        if (recyclerMainAdapter == null) return;
        recyclerView.setAdapter(recyclerMainAdapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // 액티비티에서 userID값 받아오기
        userID = getIntent().getStringExtra("userID");
        Log.i("받아온 main의 아디 값", userID);

        recyclerView = findViewById(R.id.my_listView);
        list_itemArrayList = new ArrayList<>();
        recyclerMainAdapter = new RecyclerMainAdapter(list_itemArrayList);
        recyclerView.setAdapter(recyclerMainAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        // 역순으로 뜨게 해서 최신글부터 보여주기
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new RecyclerViewHeight(5));


//      파이어베이스 데이터베이스 연동
        database = FirebaseDatabase.getInstance();

//        databaseUserlist = database.getReference().child("users");

        //현재 연결은 데이터베이스에만 딱 연결해놓고
        //키값(테이블 또는 속성)의 위치 까지는 들어가지는 않은 모습이다.
        databasePostlist = database.getReference().child("posts");
        //시간 순으로 정렬하여 최근 글 최대 13개 불러오기
        Query limitpost = databasePostlist.orderByChild("date").limitToLast(13);

        //데이터가 쌓이기 때문에 비워주기
        list_itemArrayList.clear();

        limitpost.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable java.lang.String previousChildName) {

                int cnt = (int) snapshot.getChildrenCount();
                System.out.println("속성 개수: " + cnt);

                Post postModel = snapshot.getValue(Post.class);
                System.out.println("게시글 하나" + postModel);
                list_itemArrayList.add(postModel);

                recyclerMainAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable java.lang.String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable java.lang.String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btn_notice = findViewById(R.id.btn_notice);
        btn_notice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent notice = new Intent(MainActivity.this, AnimalMapActivity.class);
                // 포스트작성 액티비티에 userID값 넘겨주기
                notice.putExtra("userID", userID);
                startActivity(notice);
            }
        });

        btn_upload = findViewById(R.id.btn_upload);
        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent post = new Intent(MainActivity.this, WritePostActivity.class);
                // 포스트작성 액티비티에 userID값 넘겨주기
                post.putExtra("userID", userID);
                startActivity(post);
            }
        });

        btn_protect = findViewById(R.id.btn_protect);
        btn_protect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent protect = new Intent(MainActivity.this, ShelterActivity.class);
                // 보호소 액티비티에 userID값 넘겨주기
                protect.putExtra("userID", userID);
                startActivity(protect);
            }
        });

        btn_profile = findViewById(R.id.btn_profile);
        btn_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent post = new Intent(MainActivity.this, MyPageActivity.class);
                post.putExtra("userID", userID);
                startActivity(post);

            }
        });

    }
}