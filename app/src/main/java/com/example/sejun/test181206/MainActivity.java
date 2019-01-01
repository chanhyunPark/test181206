package com.example.sejun.test181206;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothA2dp;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity
{
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DrawerLayout layoutDrawr;
    private NavigationView navigationView;
    private ConstraintLayout navRightMain;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference userColRef = db.collection("user");

    //다이어로그
    private ProgressDialog dialog;

    //코멘트
    private CollectionReference commentColRef = db.collection("comment");
    private RecyclerAdapter adapter;
    private RecyclerView recyclerView;
    private ArrayList<RecyclerItem> mItems = new ArrayList<>();

    private String myNickName;
    private String myEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dialog = new ProgressDialog(MainActivity.this);
        dialog.setMessage("등록중.....");
        dialog.setCancelable(false);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        layoutDrawr = (DrawerLayout) findViewById(R.id.activityMain);
        navigationView = (NavigationView) findViewById(R.id.rightNavView);

        navRightMain = (ConstraintLayout) navigationView.getHeaderView(0);


        if(user.getDisplayName() != null)
        {
            TextView navRightTitleNickName = (TextView) navRightMain.findViewById(R.id.titleNickname);
            navRightTitleNickName.setText(user.getDisplayName());
        }

        if(user.getEmail() != null)
        {
            TextView navRightTitleEmail = (TextView) navRightMain.findViewById(R.id.titleEmail);
            navRightTitleEmail.setText(user.getEmail());
        }

        if(myNickName != null)
        {
            TextView navRightMainNickName = (TextView) navRightMain.findViewById(R.id.txtNIckName);
            navRightMainNickName.setText(user.getDisplayName());
        }

        if(myEmail != null)
        {
            TextView navRightMainEmail = (TextView) navRightMain.findViewById(R.id.txtEmail);
            navRightMainEmail.setText(user.getEmail());
        }

        userColRef.document(user.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>()
                {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot)
                    {
                        if(documentSnapshot.exists())
                        {
                            Log.d("Main Tag", String.valueOf(documentSnapshot.getData()));
                            myNickName = new String(documentSnapshot.getData().get("nickname").toString());
                            myEmail = new String(documentSnapshot.getData().get("email").toString());

                            if(myNickName != null)
                            {
                                TextView navRightMainNickName = (TextView) navRightMain.findViewById(R.id.txtNIckName);
                                navRightMainNickName.setText(user.getDisplayName());
                            }

                            if(myEmail != null)
                            {
                                TextView navRightMainEmail = (TextView) navRightMain.findViewById(R.id.txtEmail);
                                navRightMainEmail.setText(user.getEmail());
                            }
                        }
                    }
                });

        findViewById(R.id.btnCommentComplete).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                commentUpload();
            }
        });

        setRecyclerView();
    }

    @Override
    public void onBackPressed()
    {
        if(layoutDrawr.isDrawerOpen(navigationView))
        {
            layoutDrawr.closeDrawer(navigationView);
            return;
        }
    }

    public void commentUpload()
    {
        EditText editText = (EditText) findViewById(R.id.EditTextComment);

        if(editText.getText().length() == 0)
        {
            Toast.makeText(this, "댓글 달아 주시길 바랍니다", Toast.LENGTH_SHORT).show();
            return;
        }

        dialog.show();
        Map<String, Object> data = new HashMap<>();
        data.put("comment", editText.getText().toString());

        if(myNickName == null)
            data.put("nickname",mAuth.getCurrentUser().getDisplayName());
        else
            data.put("nickname",myNickName);

        data.put("userId", mAuth.getUid());
        data.put("timestamp", new Date().getTime());


        commentColRef
                .add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>()
                {
                    @Override
                    public void onSuccess(DocumentReference documentReference)
                    {
                        Toast.makeText(MainActivity.this, "댓글 등록 완료", Toast.LENGTH_SHORT).show();
                        setData();
                        dialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        Log.w("실패 태그", "Error adding doc");
                        dialog.dismiss();
                    }
                });
    }

    private void setData()
    {
        mItems.clear();
        commentColRef
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task)
                    {
                        if (task.isSuccessful())
                        {
                            int taskSize = task.getResult().size();
                            int count = 0;

                            for(DocumentSnapshot documentSnapshot : task.getResult())
                            {
                                String comment = documentSnapshot.getData().get("comment").toString();
                                String nickname = documentSnapshot.getData().get("nickname").toString();
                                String userId = documentSnapshot.getData().get("userId").toString();
                                Date date = new Date((long) documentSnapshot.getData().get("timestamp"));

                                mItems.add(new RecyclerItem(nickname, comment, date.toString()));
                                count++;
                            }

                            //어댑터 함수 호출해 데이터 변경 체크
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
    }


    private void setRecyclerView()
    {
        adapter = new RecyclerAdapter(mItems);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewBulletin);
        recyclerView.setAdapter(adapter);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(getApplicationContext(), new LinearLayoutManager(this).getOrientation());

        recyclerView.addItemDecoration(dividerItemDecoration);

        setData();
    }
}
