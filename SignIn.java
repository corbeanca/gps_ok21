package a_barbu.gps_agenda;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignIn extends AppCompatActivity {

    private SignInButton mGoogleBtn;
    private static final int RC_SIGN_IN = 1;
    static GoogleApiClient mGoogleApiClient;
    static FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    static FirebaseDatabase database= FirebaseDatabase.getInstance();
    static FirebaseUser user;
    DatabaseReference Ref = database.getInstance().getReference();
    private static final String TAG = "MAIN_ACTIVITY";
    static String Name ;
    static String Email ;
    static Uri Photo;
    static boolean first;
    static int sw;
    String pres;
//    String em = "alexandrubarbu93@gmail,com";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_main);

        if( getIntent().getBooleanExtra("Exit me", false)){
            android.os.Process.killProcess(android.os.Process.myPid());
            super.onDestroy();
           // finish();
            return; // add this to prevent from doing unnecessary stuffs
        }
        sw=0;
        mAuth = FirebaseAuth.getInstance();
        mGoogleBtn = (SignInButton) findViewById( R.id.googleBtn);
        mAuthListener = new FirebaseAuth.AuthStateListener()
        {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                first=ShowPrefB("first");

                if( firebaseAuth.getCurrentUser() != null ) {
                    if (sw==1) {

                      //nu e pentru aici. ar trebui pus unde face scrierea de 'first_use'  \/
                        startActivity(new Intent(SignIn.this, Config.class));

                    } else if(sw==2) {

                        Toast.makeText(SignIn.this, "Welcome back", Toast.LENGTH_SHORT).show();
                        Toast.makeText(SignIn.this, "User present. Settings have been imported!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SignIn.this, Principal.class));

                    }
                }
            }
        };
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(SignIn.this, "Error on connection", Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }
    private boolean ShowPrefB(String key) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        return sp.getBoolean(key,true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    private void signIn() {
        if (mGoogleApiClient.hasConnectedApi(Auth.GOOGLE_SIGN_IN_API)) {
            mGoogleApiClient.clearDefaultAccountAndReconnect();
        }
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {

                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                Email = account.getEmail();
                String em = Email.replace(".", ",");
                Email = em;

                firebaseAuthWithGoogle(account);
              //

            } else {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(SignIn.this, "Google SignIn failed. Try again!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, SignIn.class));
            }
        }
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount account) {

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                   //     Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        user =  mAuth.getCurrentUser();

                        Name = account.getDisplayName();
                        Email = account.getEmail();
                        Photo = account.getPhotoUrl();
                        String em = Email.replace(".", ",");
                        Email = em;
                        SavePref("Name",Name);
                        SavePref("Email",Email);
                        SavePref("Photo",Photo.toString());
sw=2;
         //               first = CheckPresent();
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(SignIn.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    public void SavePref(String key, String value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }
    public void SavePref(String key) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key,true);
        editor.commit();
    }

    public boolean CheckPresent (){
        DatabaseReference Rstart= Ref.child(Email).child("present");
        Rstart.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                pres = dataSnapshot.getValue(String.class);
//                TextView h1 = (TextView) findViewById(R.id.hour_start);
//                h1.setText(hour_s1);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        if (pres.equals("true")){
                sw=2;
                return false;
                }
            else
                {Ref.child(Email).child("present").setValue("true");
                    sw=1;
                    Toast.makeText(SignIn.this, "Welcome! Configure the settings ", Toast.LENGTH_SHORT).show();
                return true;
        }
    }
}



