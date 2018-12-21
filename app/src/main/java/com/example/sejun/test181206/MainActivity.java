package com.example.sejun.test181206;

import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity
{
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DrawerLayout layoutDrawr;
    private NavigationView navigationView;
    private ConstraintLayout navRightMain;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference userColRef = db.collection("user");

    private String myNickName;
    private String myEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
}
