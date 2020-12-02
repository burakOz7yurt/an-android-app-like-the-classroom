package com.example.plproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class LogIn extends AppCompatActivity implements View.OnClickListener {
    TextView emailText;
    TextView passwordText;
    Button loginBtn;
    CheckBox studentCheckForLogin;
    CheckBox teacherCheckForLogin;
    static String personType;

    FirebaseDatabase db=FirebaseDatabase.getInstance();;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        emailText=findViewById(R.id.loginEmailText);
        passwordText=findViewById(R.id.loginPasswordText);
        loginBtn=findViewById(R.id.studentLoginBtn);

        studentCheckForLogin=findViewById(R.id.studentCheckBoxForLogin);
        teacherCheckForLogin=findViewById(R.id.teacherCheckBoxForLogin);


        studentCheckForLogin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(teacherCheckForLogin.isChecked() && studentCheckForLogin.isChecked())
                {
                    teacherCheckForLogin.setChecked(false);

                }
            }
        });
        teacherCheckForLogin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(studentCheckForLogin.isChecked() && teacherCheckForLogin.isChecked()  )
                {
                    studentCheckForLogin.setChecked(false);

                }
            }
        });

        loginBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if(studentCheckForLogin.isChecked())
        {
            dataControl(true);
        }

        else if(teacherCheckForLogin.isChecked())
        {
            dataControl(false);

        }
        else
            alert("Please select person type",null);

    }
    public void dataControl(Boolean typeP)
    {
        DatabaseReference dbRef;
        if(typeP)
        {
                dbRef=db.getReference(ConstantVariables.STUDENTS);
            personType=ConstantVariables.STUDENT;
        }
        else
        {
            dbRef=db.getReference(ConstantVariables.TEACHERS);
            personType=ConstantVariables.TEACHER;
        }


        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean flag=false;
                Iterable<DataSnapshot> keys=dataSnapshot.getChildren();
                for(DataSnapshot key:keys)
                {
                    if(key.child(ConstantVariables.EMAIL).getValue().toString().equals(emailText.getText().toString())&&key.child(ConstantVariables.PASSWORD).getValue().toString().equals(passwordText.getText().toString()))
                    {

                      //  if(teacherCheckForLogin.isChecked())
                        {
                            Intent lgn=new Intent(LogIn.this,TeacherPage.class);
                          //  Intent intent=new Intent(getApplicationContext(),TeacherPage.class);
                            lgn.putExtra(ConstantVariables.EMAIL,emailText.getText().toString());
                            startActivity(lgn);
                        }

                        flag=true;
                    }
                }

                if(!flag)
                {
                    alert("no such record found",null);
                }


            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    public void alert(String settitle,String setmessage)
    {
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(this);
        alertDialog.setTitle(settitle)
                .setMessage(setmessage)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //
                    }
                }).show();
    }

}