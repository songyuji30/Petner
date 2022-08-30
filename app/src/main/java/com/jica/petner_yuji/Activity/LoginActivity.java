package com.jica.petner_yuji.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jica.petner_yuji.R;


public class LoginActivity extends AppCompatActivity {

    private Button LoginBtn;
    private TextView tv_SignUp, et_loginId, et_loginPw;
    private Boolean sign = true;

    // 파이어베이스 데이터베이스
    private FirebaseDatabase database;

    //DatabaseReference는 데이터베이스의 특정 위치로 연결하는 거라고 생각하면 된다.
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 회원가입에서 값 받아오기
        sign = getIntent().getBooleanExtra("sign",true);
        System.out.println("받아온 값"+sign);

        if(sign){
            //처음에만 로딩 화면 띄우기(회원가입하고 넘어올 때는 안뜨게!)
            Intent load = new Intent(this, LoadingActivity.class);
            startActivity(load);
        }

        LoginBtn = findViewById(R.id.btn_login);
        et_loginId = findViewById(R.id.et_loginId);
        et_loginPw = findViewById(R.id.et_loginPw);

        // 회원가입 액티비티로 전환
        tv_SignUp = findViewById(R.id.tv_SignUp);
        tv_SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(intent);
            }
        });

        // 로그인
        LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });


    }

    


    private void login(){

        // 파이어베이스 데이터베이스 연동
        database = FirebaseDatabase.getInstance();

        //현재 연결은 데이터베이스에만 딱 연결해놓고
        //키값(테이블 또는 속성)의 위치 까지는 들어가지는 않은 모습이다.
        databaseReference = database.getReference();

        String userID = et_loginId.getText().toString();
        String userPass = et_loginPw.getText().toString();


        //해당 칸 입력 안했을 경우
        if (userID.equals("")) {
            Toast.makeText(getApplicationContext(),"아이디를 입력해주세요.",Toast.LENGTH_SHORT).show();//토스트 메세지 출력
            return;
        } else if(userPass.equals("")){
            Toast.makeText(getApplicationContext(),"비밀번호를 입력해주세요.",Toast.LENGTH_SHORT).show();//토스트 메세지 출력
            return;
        }


        databaseReference.child("users").child(userID).child("userPass").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // 파이어베이스에서 해당 아이디값의 비밀번호를 얻어와서 저장
                String value = dataSnapshot.getValue(String.class);
                
                if(userPass.equals(value)){
                    Toast.makeText(getApplicationContext(),"로그인에 성공하였습니다.",Toast.LENGTH_SHORT).show();//토스트 메세지 출력
                    Intent intentUserID = new Intent(LoginActivity.this, MainActivity.class);
                    // 메인 엑티비티로 userID 값 전달
                    intentUserID.putExtra("userID", userID);
                    startActivity(intentUserID);
                }else{
                    Toast.makeText(getApplicationContext(),"입력값을 확인하세요.",Toast.LENGTH_SHORT).show();//토스트 메세지 출력
                    return;
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),"아이디를 확인하세요.",Toast.LENGTH_SHORT).show();//토스트 메세지 출력
            }
        });

    }

}
