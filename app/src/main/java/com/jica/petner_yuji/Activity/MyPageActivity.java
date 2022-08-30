package com.jica.petner_yuji.Activity;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jica.petner_yuji.Adapter.RecyclerViewAdapter;
import com.jica.petner_yuji.Adapter.RecyclerViewWidth;
import com.jica.petner_yuji.GlideApp;
import com.jica.petner_yuji.model.MyPageGallery;
import com.jica.petner_yuji.R;

import java.io.InputStream;
import java.util.ArrayList;

public class MyPageActivity extends AppCompatActivity {

    private ImageView profileImg;
    private RecyclerView img_recView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private ArrayList<MyPageGallery> imglistArrayList;

    Button btn_notice, btn_upload, btn_protect, btn_home;


    //프로필 사진 요청 코드
    private static final int REQUEST_CODE = 0;


    private String userID;

    private Button btn_edit;
    private TextView et_username;

    private AlertDialog dialog;
    private String uri;

    // 파이어 스토리지 접근하기 위해 사용
    private FirebaseStorage storage;

    private FirebaseDatabase database;
    private DatabaseReference databaseImglist;
    private DatabaseReference lmglist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);


        //아이디 값 가져오기
        userID = getIntent().getStringExtra("userID");
        Log.i("마이페이지 아디 값", userID);

        profileImg = findViewById(R.id.profileImg);
        et_username = findViewById(R.id.et_username);
        et_username.setText(userID);


        btn_notice = findViewById(R.id.btn_notice);
        btn_notice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent notice = new Intent(MyPageActivity.this, AnimalMapActivity.class);
                // 포스트작성 액티비티에 userID값 넘겨주기
                notice.putExtra("userID", userID);
                startActivity(notice);
            }
        });

        btn_upload = findViewById(R.id.btn_upload);
        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent post = new Intent(MyPageActivity.this, WritePostActivity.class);
                // 포스트작성 액티비티에 userID값 넘겨주기
                post.putExtra("userID", userID);
                startActivity(post);
            }
        });

        btn_protect = findViewById(R.id.btn_protect);
        btn_protect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent protect = new Intent(MyPageActivity.this, ShelterActivity.class);
                // 보호소 액티비티에 userID값 넘겨주기
                protect.putExtra("userID", userID);
                startActivity(protect);
            }
        });

        btn_home = findViewById(R.id.btn_home);
        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 포스트작성 액티비티에 userID값 넘겨주기
                Intent post = new Intent(MyPageActivity.this, MainActivity.class);
                post.putExtra("userID", userID);
                startActivity(post);

            }
        });



        storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();
        StorageReference path = storageReference.child("photo/"+userID+".png");
        path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                GlideApp.with(MyPageActivity.this).load(uri).into(profileImg);
            }
        });

        // EDIT 버튼 누르면 갤러리에서 프로필 사진 가져오기
        btn_edit = findViewById(R.id.btn_edit);
        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQUEST_CODE);
            }

        });

        firstInit();

        recyclerViewAdapter = new RecyclerViewAdapter(imglistArrayList);
        img_recView.setAdapter(recyclerViewAdapter);
        //이미지가 가로로 나오게 하기
        img_recView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        img_recView.addItemDecoration(new RecyclerViewWidth(50));


        lmglist = FirebaseDatabase.getInstance().getReference();
        databaseImglist = lmglist.child("posts");
        databaseImglist.orderByChild("userID").equalTo(userID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                MyPageGallery list_img = snapshot.getValue(MyPageGallery.class);
                Log.d("TAG", list_img.toString());
                imglistArrayList.add(list_img);

                recyclerViewAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }



    //    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                    Uri file = data.getData();
                    StorageReference storageRef = storage.getReference();
                    // 스토리지의 photo 폴더에 userID.png로 저장됨
                    StorageReference riversRef = storageRef.child("photo/"+userID+".png");
                    UploadTask uploadTask = riversRef.putFile(file);
                    try {
                        InputStream in = getContentResolver().openInputStream(data.getData());
                        Bitmap img = BitmapFactory.decodeStream(in);
                        in.close();
                        profileImg.setImageBitmap(img);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //스토리지에 정상적으로 이미지 파일을 업로드할 수 없을 때 아래 코드 수행
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(MyPageActivity.this, "사진이 정상적으로 업로드 되지 않았습니다." ,Toast.LENGTH_SHORT).show();
                            Log.i(TAG,"사진 업로드 실패!");
                        }
                        //스토리지에 정상적으로 이미지 파일이 업로드 됐을 때 아래 코드 수행
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(MyPageActivity.this, "사진이 정상적으로 업로드 되었습니다." ,Toast.LENGTH_SHORT).show();
                            Log.i(TAG,"사진 업로드 성공!!");
                        }
                    });
                }
            }
    }

    
    public void firstInit() {
        img_recView = (RecyclerView) findViewById(R.id.img_recView);
        imglistArrayList = new ArrayList<>();
    }

    public void addItem(String img) {
        MyPageGallery item = new MyPageGallery();

        item.setImg(img);

        imglistArrayList.add(item);
    }
}