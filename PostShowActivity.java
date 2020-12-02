package com.example.plproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PostShowActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private String postCode;
    private String courseCode;

    private TextView postNameForShow;
    private TextView postTermForShow;
    private TextView textPostSubject;
    private TextView editPostName;
    private TextView editPostTerm;
    private TextView editPostSubject;
    private TextView textPostShow;
    private TextView textAddPost;
    private TextView textPostDataUpdate;

    private Button btnPostUpdate;
    private Button btnPostDataUpdate;
    private ImageButton buttonAddPost;

    private ScrollView scrollPostShow;
    private ScrollView scrollPostUpdate;

    private LinearLayout layoutPostInfoUpdate;
    private LinearLayout addPostLinerLayout;

    final private PostList postList=new PostList();
    private FirebaseDatabase db= FirebaseDatabase.getInstance();
    DatabaseReference dbRefCourses=db.getReference(ConstantVariables.COURSES);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_show);

        postCode=getIntent().getExtras().get("postCode").toString();
        courseCode=getIntent().getExtras().get("courseCode").toString();

        toolbar=findViewById(R.id.postShowLayout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(postCode);

        postNameForShow=findViewById(R.id.postNameForShow);
        postTermForShow=findViewById(R.id.postTermForShow);
        textPostSubject=findViewById(R.id.textPostSubject);

        editPostName=findViewById(R.id.editPostName);
        editPostTerm=findViewById(R.id.editPostTerm);
        editPostSubject=findViewById(R.id.editPostSubject);
        textPostShow=findViewById(R.id.textPostShow);
        textAddPost=findViewById(R.id.textAddPost);
        textPostDataUpdate=findViewById(R.id.textPostDataUpdate);

        //bilgiler görünsün diye
        getAndShowInfo(courseCode,postCode);

        btnPostUpdate=findViewById(R.id.btnPostUpdate);
        buttonAddPost=findViewById(R.id.buttonAddPost);
        btnPostDataUpdate=findViewById(R.id.btnPostDataUpdate);

        scrollPostShow=findViewById(R.id.scrollPostShow);
        scrollPostUpdate=findViewById(R.id.scrollPostUpdate);

        layoutPostInfoUpdate=findViewById(R.id.layoutPostInfoUpdate);
        addPostLinerLayout=findViewById(R.id.addPostLinerLayout);

        layoutPostInfoUpdate.setVisibility(View.INVISIBLE);
        scrollPostUpdate.setVisibility(View.INVISIBLE);

        btnPostUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postNameForShow.setText(editPostName.getText().toString());
                postTermForShow.setText(editPostTerm.getText().toString());
                textPostSubject.setText(editPostSubject.getText().toString());

                scrollPostShow.setVisibility(View.VISIBLE);
                textPostShow.setVisibility(View.VISIBLE);
                layoutPostInfoUpdate.setVisibility(View.INVISIBLE);
            }
        });

        buttonAddPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String postDataForAdd=textAddPost.getText().toString()+"\n";
                if(LogIn.personType.equals(ConstantVariables.STUDENT))
                    postDataForAdd="student:"+TeacherPage.myEmail+">>"+postDataForAdd;
                else
                    postDataForAdd="teacher:"+TeacherPage.myEmail+">>"+postDataForAdd;

                addNewDataForPost(postCode,courseCode,TeacherPage.myEmail,postDataForAdd);
                textAddPost.setText("");
            }
        });

        btnPostDataUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatePostData(courseCode,postCode,textPostDataUpdate.getText().toString());
                scrollPostShow.setVisibility(View.VISIBLE);
                scrollPostUpdate.setVisibility(View.INVISIBLE);
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(LogIn.personType.equals(ConstantVariables.TEACHER))
            getMenuInflater().inflate(R.menu.menu_post,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        if(item.getItemId()==R.id.updatePostInfo)
        {
            scrollPostShow.setVisibility(View.INVISIBLE);
            textPostShow.setVisibility(View.INVISIBLE);
            layoutPostInfoUpdate.setVisibility(View.VISIBLE);
        }
        else  if(item.getItemId()==R.id.updatePostStreamData)
        {
            deletePostDemand();
           /* scrollPostShow.setVisibility(View.INVISIBLE);
            scrollPostUpdate.setVisibility(View.VISIBLE);*/
        }


        return true;
    }
    private void updatePostData(final String courseCode,final String postCode,final String updatedData) {
        dbRefCourses.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> keys=dataSnapshot.getChildren();
                for(DataSnapshot key:keys) {
                    if (key.child(ConstantVariables.CODE).getValue().toString().equals(courseCode)) {
                        DatabaseReference dbRefPosts=key.child(ConstantVariables.POSTS).getRef();
                        dbRefPosts.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Iterable<DataSnapshot> keys=dataSnapshot.getChildren();
                                for(DataSnapshot key:keys) {
                                    if (key.child(ConstantVariables.CODE).getValue().toString().equals(postCode)) {
                                        DatabaseReference dbRef=key.child(ConstantVariables.DATA).getRef();

                                        dbRef.setValue(updatedData);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addNewDataForPost(final String postCode, final String courseCode, final String myEmail, final String postDataForAdd) {
        dbRefCourses.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> keys=dataSnapshot.getChildren();
                for(DataSnapshot key:keys) {
                    if (key.child(ConstantVariables.CODE).getValue().toString().equals(courseCode)) {
                        DatabaseReference dbRefPosts=key.child(ConstantVariables.POSTS).getRef();
                        dbRefPosts.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Iterable<DataSnapshot> keys=dataSnapshot.getChildren();
                                for(DataSnapshot key:keys) {
                                    if (key.child(ConstantVariables.CODE).getValue().toString().equals(postCode)) {
                                        DatabaseReference dbRef=key.child(ConstantVariables.DATA).getRef();
                                        postList.addNewPostNode(new PostNode(postDataForAdd));
                                        dbRef.setValue(postList.getListDataForDataBase());
                                        textPostShow.setText(postList.getListData());//stream data

                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void deletePostDemand() {
        AlertDialog.Builder builder=new AlertDialog.Builder(PostShowActivity.this,R.style.alertDialog);
        builder.setTitle("please enter number of post to delete");

        final EditText receiverEmailField=new EditText(PostShowActivity.this);
        receiverEmailField.setHint("1");
        builder.setView(receiverEmailField);

        builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String receiverEmail=receiverEmailField.getText().toString();
                int postNumber=Integer.parseInt(receiverEmail);
                if(TextUtils.isEmpty(receiverEmail))
                {
                    Toast.makeText(PostShowActivity.this,"number of post doesnt be empty ",Toast.LENGTH_LONG).show();
                }
                else
                {
                    deletePost(courseCode,postCode,postNumber);
                }
            }
        });
        builder.show();
    }

    private void deletePost(final String courseCode,final String postCode, final int postNumber) {
        dbRefCourses.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                {
                    Iterable<DataSnapshot> keys=dataSnapshot.getChildren();
                    for(DataSnapshot key:keys)
                    {
                        if(key.child(ConstantVariables.CODE).getValue().toString().equals(courseCode))
                        {
                            DatabaseReference dbRef=key.child(ConstantVariables.POSTS).getRef();
                            dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    Iterable<DataSnapshot> keys=dataSnapshot.getChildren();
                                    for(DataSnapshot key:keys)
                                    {
                                        if(key.child(ConstantVariables.CODE).getValue().toString().equals(postCode))
                                        {
                                            postList.deletePostNode(postNumber);
                                            DatabaseReference dbRef=key.child(ConstantVariables.DATA).getRef();
                                            dbRef.setValue(postList.getListDataForDataBase());
                                            textPostShow.setText(postList.getListData());//stream data

                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getAndShowInfo(final String courseCode,final String postCode) {
        dbRefCourses.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                {
                    Iterable<DataSnapshot> keys=dataSnapshot.getChildren();
                    for(DataSnapshot key:keys)
                    {
                        if(key.child(ConstantVariables.CODE).getValue().toString().equals(courseCode))
                        {
                            DatabaseReference dbRef=key.child(ConstantVariables.POSTS).getRef();
                            dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    Iterable<DataSnapshot> keys=dataSnapshot.getChildren();
                                    for(DataSnapshot key:keys)
                                    {
                                        if(key.child(ConstantVariables.CODE).getValue().toString().equals(postCode))
                                        {


                                            postNameForShow.setText(key.child(ConstantVariables.NAME).getValue().toString());
                                            postTermForShow.setText(key.child(ConstantVariables.TERM).getValue().toString());
                                            textPostSubject.setText(key.child(ConstantVariables.SUBJECT).getValue().toString());

                                            editPostName.setText(key.child(ConstantVariables.NAME).getValue().toString());
                                            editPostTerm.setText(key.child(ConstantVariables.TERM).getValue().toString());
                                            editPostSubject.setText(key.child(ConstantVariables.SUBJECT).getValue().toString());

                                            postList.parsePostToPostNodes(key.child(ConstantVariables.DATA).getValue().toString());
                                            textPostShow.setText(postList.getListData());//stream data


                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
