package com.jica.petner_yuji.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.jica.petner_yuji.R;
import com.jica.petner_yuji.model.User;

public class SignUpActivity extends AppCompatActivity {

    private Button btn_comfirm, btn_signUp;
    private TextView tv_SignIn,et_username, et_id, et_pw, et_pwConfirm;
    private Intent intent;
    private boolean validate = false;
    private AlertDialog dialog;


    // 파이어베이스 데이터베이스 연동
    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    //DatabaseReference는 데이터베이스의 특정 위치로 연결하는 거라고 생각하면 된다.
    //현재 연결은 데이터베이스에만 딱 연결해놓고
    //키값(테이블 또는 속성)의 위치 까지는 들어가지는 않은 모습이다.
    private DatabaseReference databaseReference = database.getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        et_id = findViewById(R.id.et_id);
        et_pw = findViewById(R.id.et_pw);
        et_username = findViewById(R.id.et_username);
        et_pwConfirm = findViewById(R.id.et_pwConfirm);
        btn_signUp = findViewById(R.id.btn_signUp);


        //아이디 중복 확인
        btn_comfirm = findViewById(R.id.btn_confirm);
        btn_comfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirm();
            }
        });

        //회원가입
        btn_signUp = findViewById(R.id.btn_signUp);
        btn_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signup();
            }
        });

        //로그인 화면으로 전환
        tv_SignIn = findViewById(R.id.tv_SignIn);
        tv_SignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.putExtra("sign",false);
                startActivity(intent);
            }
        });

    }

    //아이디 중복 확인
    private void confirm() {
        //입력된 아이디 값 가져오기
        String userID = et_id.getText().toString();

        if (validate) {
            return; //검증 완료
        }

        if (et_id.equals("")) {
            Toast.makeText(getApplicationContext(), "아이디를 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        databaseReference.child("users").child(userID).child("userID").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // 파이어베이스에서 아이디 값을 얻어와서 value에 저장
                String value = dataSnapshot.getValue(String.class);

                if (value != null) {
                    Toast.makeText(getApplicationContext(), "이미 존재하는 아이디입니다. 다시 입력하세요.", Toast.LENGTH_SHORT).show();//토스메세지 출력
                    Log.e("존재하는 아이디", "아이디: " + userID);
                } else {
                    Toast.makeText(getApplicationContext(), "사용할 수 있는 아이디입니다.", Toast.LENGTH_SHORT).show();//토스메세지 출력
                    et_id.setEnabled(false); // 아이디값 고정
                    validate = true; //검증 완료
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    //회원가입
    private void signup() {

        String userID = et_id.getText().toString();
        String userPass = et_pw.getText().toString();
        String userName = et_username.getText().toString();
        String PassCheck = et_pwConfirm.getText().toString();

        //한 칸이라도 입력 안했을 경우 alert
        if (userID.equals("") || userPass.equals("") || userName.equals("")) {
            Toast.makeText(getApplicationContext(),"모두 입력해주세요.",Toast.LENGTH_SHORT).show();//토스트 메세지 출력
            return;
        }

        if (validate) {
            //비밀번호 체크
            if(userPass.equals(PassCheck)){
                Toast.makeText(getApplicationContext(),"회원가입에 성공하였습니다.",Toast.LENGTH_SHORT).show();//토스트 메세지 출력
                Log.e("회원가입 성공", "아이디: " + userID);
                addUserModel(userName, userID, userPass);
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                intent.putExtra("sign",false);
                startActivity(intent);
            }else{
                Toast.makeText(getApplicationContext(),"비밀번호를 확인해주세요.",Toast.LENGTH_SHORT).show();//토스트 메세지 출력
                return;
            }
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
            dialog = builder.setMessage("중복된 아이디가 있는지 확인하세요.").setNegativeButton("확인", null).create();
            dialog.show();
            return;

        }



    }

    public void addUserModel(String userName, String userID, String userPass) {

        //userModel.java에서 선언했던 함수.
        User userModel = new User(userName, userID, userPass);

        //child는 해당 키 위치로 이동하는 함수입니다.
        //키가 없는데 "zoo"와 name같이 값을 지정한 경우 자동으로 생성합니다.
        databaseReference.child("users").child(userID).setValue(userModel);

    }


}

