package com.example.mobileliarsdice;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class deleteActivity extends AppCompatActivity {

    Button cancel;
    Button delete;
    EditText email;
    EditText password;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);
        //GETS RID OF THE TOP BAR
        try{
            this.getSupportActionBar().hide();
        }catch(Exception e){}


        cancel = findViewById(R.id.cancel);
        delete = findViewById(R.id.delete);
        email = findViewById(R.id.lblEmail);
        password = findViewById(R.id.lblPassword);

        pd = new ProgressDialog(deleteActivity.this, R.color.colorPrimary);
        pd.setIndeterminate(true);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                setResult(RESULT_CANCELED);
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email_ = email.getText().toString();
                String password_ = password.getText().toString();
                if(validateEmailAndPassword(email_, password_)){
                    signIn(email_, password_);
                }
            }
        });

    }



    /**
     * Validates email and password based on firebase rules.
     *
     * @param email
     * @param password
     */
    private Boolean validateEmailAndPassword(String email, String password) {

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Utilities.createToast("Enter a valid email address.", deleteActivity.this);
            return false;
        }

        if (password.isEmpty() || password.length() < 5) {
            Utilities.createToast("The password must be 6 characters or longer.", deleteActivity.this);
            return false;
        }

        return true;
    }

    private void signIn(String email, String password){
        pd.setMessage("Verifying your credentials...");
        FirebaseAuth mAuth = FireBaseGlobals.getAuth();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(deleteActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            deleteUser();
                        } else {
                            //error
                            pd.dismiss();
                            Utilities.createToast("Authentication Error. Make sure your password and email are correct", deleteActivity.this);

                        }

                    }
                });
    }

    public void deleteUser(){
        //delete the user from firebase!
        pd.setMessage("Deleting your account...");
        final String _id = UserGlobals.mUser.getId();
        FireBaseGlobals.getUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    pd.dismiss();
                    //need to delete the fucking user object!

                    DatabaseReference userRef = FireBaseGlobals.getDataBase().getReference("USERS").child(_id);
                    //delete the user!
                    userRef.removeValue();
                    pd.dismiss();
                    //inform
                    Utilities.createToast("YOUR ACCOUNT WAS DELETED", deleteActivity.this);
                    // clean memory
                    Utilities.deleteCredentials(FireBaseGlobals.getContext());
                    //logout
                    FireBaseGlobals.logOut();
                    //return
                    setResult(RESULT_OK);
                    //delete this acctivity.
                    finish();
                }else{
                    pd.dismiss();
                    Utilities.createToast("PLEASE TRY AGAIN!", deleteActivity.this);
                }
            }
        });
    }

}
