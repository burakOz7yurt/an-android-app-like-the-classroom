package com.example.plproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.Toolbar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button logIn;
    Button signUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logIn=findViewById(R.id.logIn);
        signUp=findViewById(R.id.signUp);


        logIn.setOnClickListener(this);
        signUp.setOnClickListener(this);


    }


    @Override
    public void onClick(View view) {
       if(view.getId()==logIn.getId())
       {
           Intent lgn=new Intent(MainActivity.this,LogIn.class);
           startActivity(lgn);
       }
       else  if(view.getId()==signUp.getId())
       {
           Intent sgp=new Intent(MainActivity.this,SignUp.class);
           startActivity(sgp);
       }

    }


}
