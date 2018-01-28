/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.firebase.quickstart.fcm;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mFirebaseDatabaseReference;
    private DatabaseReference mFirebaseDatabaseReferenceUid;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    public static final int RC_SIGN_IN = 1;
    public static String mUserName;
    public static int accountBalance = 0;
    public static boolean pushPreference = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       Button preferenceButton = findViewById(R.id.preferencesButton);
        preferenceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.google.firebase.quickstart.fcm.PreferencesActivity");
                startActivity(intent);
            }
        });

        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null) {
                    onSignedInInitialize(user.getDisplayName(),user.getUid());
                }
                else{
                    onSignedOutCleanup();
                    startActivityForResult(
                            // Get an instance of AuthUI based on the default app
                            AuthUI.getInstance().createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .build(),
                            RC_SIGN_IN);
                }

            }
        };
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode == RC_SIGN_IN){
            if(resultCode == RESULT_OK){
                Toast.makeText(this,"Signed In!",Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED){
                Toast.makeText(this,"Sign in is cancelled",Toast.LENGTH_SHORT);
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.sign_out_menu:
                onSignedOutCleanup();
                AuthUI.getInstance().signOut(this);
                return true;
            default:return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    protected void onSignedInInitialize(String userDisplayName, final String userId) {
        mUserName = userDisplayName;

        mFirebaseDatabase = FirebaseDatabase.getInstance();


        mFirebaseDatabaseReferenceUid = mFirebaseDatabase.getReference().child("users/"+userId);
        mFirebaseDatabaseReferenceUid.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    TextView balView = (TextView) findViewById(R.id.balanceTextView);
                    MyUser user = dataSnapshot.getValue(MyUser.class);

                        accountBalance = user.getUserAccountBalance();
                        pushPreference = user.getPushPreference();
                        balView.setText(""+accountBalance);
                        String token = FirebaseInstanceId.getInstance().getToken();
                        mFirebaseDatabaseReferenceUid.child("userDeviceToken").setValue(token);
                }
                else {
                    mFirebaseDatabaseReference = mFirebaseDatabase.getReference().child("users");
                    String token = FirebaseInstanceId.getInstance().getToken();
                    MyUser myUser = new MyUser(FirebaseInstanceId.getInstance().getToken().toString(),MainActivity.mUserName, pushPreference,MainActivity.accountBalance);
                    mFirebaseDatabaseReference.child(userId).setValue(myUser);
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    protected  void onSignedOutCleanup(){
        mUserName = "";
        accountBalance = 0;
        pushPreference = false;


    }


}
