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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignIn extends AppCompatActivity {

    private SignInButton mGoogleBtn;
    private static final int RC_SIGN_IN = 1;
    static GoogleApiClient mGoogleApiClient;
    static FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    static FirebaseDatabase database;
    static FirebaseUser user;
    private static final String TAG = "MAIN_ACTIVITY";
    static String Name = null;
    static String Email = null;
    static Uri Photo=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        mGoogleBtn = (SignInButton) findViewById( R.id.googleBtn);
        mAuthListener = new FirebaseAuth.AuthStateListener()
        {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                boolean first_signIn = isFirst_signIn();


                if( firebaseAuth.getCurrentUser() != null )
                 if (first_signIn)
                {
                      Toast.makeText(SignIn.this,"first sign in, import settings", Toast.LENGTH_LONG).show();
                    change_use();
                    signOut();
                    startActivity(new Intent(SignIn.this,Config.class));
                }
                else{
             //       Toast.makeText(SignIn.this, "Welcome back " + Name +"! Settings have been imported", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SignIn.this,Principal.class));}
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
    private boolean isFirst_signIn (){
        SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(this);
        boolean first = sp.getBoolean("first_use", true);
        if (first)
            return true;
                else return false;
    }
    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public static void signOut() {
        mAuth.signOut();
        Auth.GoogleSignInApi.signOut(mGoogleApiClient);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {


                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);


                Toast.makeText(SignIn.this, "acum trebuie introdus un nou user in baza ", Toast.LENGTH_SHORT).show();
//                database = FirebaseDatabase.getInstance();
//                user=mAuth.getCurrentUser();
//                String userID = user.getUid();
//
//
//                User userLoc = new User(Name, Email);
//                DatabaseReference Ref = database.getReference().child("Users").child(userID);
//                Ref.setValue(userLoc);
//
//                startActivity(new Intent(SignIn.this, Config.class));
//                Toast.makeText(SignIn.this, "Welcome " + Name +"! First time set up ", Toast.LENGTH_SHORT).show();


            } else {
                // Google Sign In failed, update UI appropriately

            }
        }
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount account) {

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        user =  mAuth.getCurrentUser();



                        Name = account.getDisplayName();
                        Email = account.getEmail();
                        Photo = account.getPhotoUrl();

                        SavePref("Name",Name);
                        SavePref("Email",Email);
                        SavePref("Photo",Photo.toString());

                        // check if user present

                        //DatabaseReference ref2=Ref.child("userName");
                      //  Toast.makeText(SignIn.this, "user test  " + ref2, Toast.LENGTH_LONG).show();


                            //Ref = Ref.child(Email);
                           // Toast.makeText(SignIn.this, "Welcome back " + Name +"! Settings have been imported", Toast.LENGTH_SHORT).show();
                          //  isFirst_signIn();
                        //    startActivity(new Intent(SignIn.this,Principal.class));


                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(SignIn.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    private void change_use(){
        SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean("first_use", false);
        ed.commit();
    }

    public void SavePref(String key, String value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

}



