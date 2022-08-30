package com.jica.petner_yuji.Activity;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jica.petner_yuji.R;
import com.jica.petner_yuji.model.Post;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


// 주요 기능 1. 글 작성하기
//         2. 이미지- 촬영, 갤러리에서 사진 가져오기
//         3. (제목, 업로드된 이미지, 내용) 글 작성 완료하면 DB에 저장하기
public class WritePostActivity extends AppCompatActivity {

    private Button btn_image, btn_pic, btn_picGal, btn_back;
    private TextView et_title, et_content, btn_upload;
    private Intent intent;
    private AlertDialog dialog;
    private ImageView comeimage;
    private View buttonsLayout;


    //사진
    final static int TAKE_PICTURE = 1;
    final static int GET_GALLERY_IMAGE = 2;
    private Uri selectedImageUri;

    //이미지 절대경로
    private String path;
    //확장자 얻어오기
    private String img;

    //메인 액티비티에서 받아올 userID
    private String userID;

    // 파이어베이스 데이터베이스 연동
    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    //DatabaseReference는 데이터베이스의 특정 위치로 연결하는 거라고 생각하면 된다.
    //현재 연결은 데이터베이스에만 딱 연결해놓고
    //키값(테이블 또는 속성)의 위치 까지는 들어가지는 않은 모습이다.
    private DatabaseReference databaseReference = database.getReference();


    private FirebaseAuth mAuth;
    private String valueUserName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writepost);

        // 메인 액티비티에서 userID값 받아오기
        userID = getIntent().getStringExtra("userID");
        System.out.println("main에서 받아온 아디 값: " + userID);


        btn_upload = findViewById(R.id.btn_upload);
        btn_image = findViewById(R.id.btn_image);
        btn_pic = findViewById(R.id.btn_pic);
        btn_picGal = findViewById(R.id.btn_picGal);
        et_title = findViewById(R.id.et_title);
        et_content = findViewById(R.id.et_content);


        // 파이어베이스 익명으로 로그인
        mAuth = FirebaseAuth.getInstance();
        onStart();

        // 마시멜로우 이상일 경우에는 권한 체크 후 권한 요청
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    ==checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)){}
            else{
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},1 );
            }
        }

        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent main = new Intent(WritePostActivity.this, MainActivity.class);
                main.putExtra("userID", userID);
                startActivity(main);
            }
        });

        //버튼 누르면 숨겨있던 버튼 등장(촬영, 갤러리)
        comeimage = findViewById(R.id.comeimage);
        buttonsLayout = findViewById(R.id.buttonsLayout);
        btn_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonsLayout.setVisibility(View.VISIBLE);
            }
        });


        //이미지 가져오기 누르면? - 갤러리에서 가져오기
        btn_picGal = findViewById(R.id.btn_picGal);
        btn_picGal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, GET_GALLERY_IMAGE);
                buttonsLayout.setVisibility(View.GONE);
                btn_image.setText("");


            }
        });


        //이미지 가져오기 누르면? - 촬영하기
        btn_pic = findViewById(R.id.btn_pic);
        btn_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, TAKE_PICTURE);

                buttonsLayout.setVisibility(View.GONE);
                btn_image.setText("");
            }
        });

        // 글 DB에 저장하기
        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickUpload();
                selectedImageUri = null;
            }
        });


    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // do your stuff
        } else {
            signInAnonymously();
        }
    }

    private void signInAnonymously() {
        mAuth.signInAnonymously().addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        // do your stuff
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.e(TAG, "signInAnonymously:FAILURE", exception);
                    }
                });
    }


    // 권한 요청
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
            Log.d("로그", "Permission: " + permissions[0] + " was " + grantResults[0]);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 사진 촬영 완료 후 응답
        if (requestCode == TAKE_PICTURE) {
            if (resultCode == RESULT_OK && data.hasExtra("data")) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");

                comeimage.setImageBitmap(bitmap);

                String imageSaveUri = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "사진 저장", "찍은 사진이 저장되었습니다.");

                selectedImageUri = Uri.parse(imageSaveUri);
                path = getRealPathFromURI(selectedImageUri); //절대경로 구하기
                img = path.substring(path.lastIndexOf(".")); //확장자구하기
                Log.d(TAG, "카메라 - " + selectedImageUri);
                Log.d(TAG, "카메라 - 경로" + path);
                Log.d(TAG, "이미지 확장자 - " + img);

            }
        }

        // 갤러리에서 이미지 가져온 후의 응답
        else if (requestCode == GET_GALLERY_IMAGE) {
            if (resultCode == RESULT_OK && data.getData() != null) {
                selectedImageUri = data.getData();
                Log.d(TAG, "갤러리 - " + selectedImageUri);
                Log.d(TAG, "갤러리 - 경로" + getRealPathFromURI(selectedImageUri));

                path = getRealPathFromURI(selectedImageUri); //절대경로 구하기
                img = path.substring(path.lastIndexOf(".")); //확장자구하기
                Log.d(TAG, "이미지 확장자 - " + img);

                Glide.with(this)
                        .load(getRealPathFromURI(selectedImageUri))
                        .into(comeimage);

            }
        }
    }

    // 절대 경로로 변경
    private String getRealPathFromURI(Uri contentURI) {


        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);

        if (cursor == null) { // Source is Dropbox or other similar local file path
            path = contentURI.getPath();

        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            path = cursor.getString(idx);
            cursor.close();
        }

        return path;
    }


    // 파이어베이스 업로드 함수
    public void clickUpload() {

        // 1. FirebaseStorage을 관리하는 객체 얻어오기
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageRef = firebaseStorage.getReference();


        database.getReference().child("users").child(userID).child("userName").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // 파이어베이스에서 해당 아이디값의 닉네임 얻어와서 저장
                valueUserName = snapshot.getValue(String.class);
                System.out.println("@!#!#!@$" + valueUserName);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        Uri file = Uri.fromFile(new File(path)); // 절대경로uri를 file에 할당
        Log.d(TAG, "photo file : " + file);

        // 원래 확장자는 파일의 실제 확장자를 얻어와서 사용해야함. 그러려면 이미지의 절대 주소를 구해야함. -> path로 구함

        StorageReference imgRef = storageRef.child("posts/" + file.getLastPathSegment());
        // posts라는 폴더에 절대경로 파일 저장

        // 참조 객체를 통해 이미지 파일 업로드
        // 업로드 결과를 받고 싶다면 아래와 같이 UploadTask를 사용하면 된다.
            UploadTask uploadTask = imgRef.putFile(file);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        Post postModel = new Post();

                        String title = et_title.getText().toString();
                        String imgname = file.getLastPathSegment();
                        String content = et_content.getText().toString();

                        LocalDateTime now = LocalDateTime.now();
                        String date = now.format(DateTimeFormatter.ofPattern("yyMMddHHmmss"));
                        Log.i(TAG,"현재 날짜와 시간 : "+date);

                        postModel.setUserID(userID);
                        postModel.setTitle(title);
                        postModel.setImg(uri.toString());
                        postModel.setImgname(imgname);
                        postModel.setContent(content);
                        postModel.setDate(date);
                        postModel.setUserName(valueUserName);

                        // 게시글 내용 저장
                        database.getReference().child("posts").push()
                                .setValue(postModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getApplicationContext(), "게시물 저장에 성공하였습니다.", Toast.LENGTH_SHORT).show();//토스메세지 출력
                                        Intent intent = new Intent(WritePostActivity.this, MainActivity.class);
                                        intent.putExtra("userID", userID);
                                        startActivity(intent);
                                    }
                                });

                    }
                });

            }
        });
    }
}
