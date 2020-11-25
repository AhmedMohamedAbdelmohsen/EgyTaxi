package com.ahmedabdelmohsen.egytaxi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.transition.Fade;
import android.view.View;
import android.widget.Toast;

import com.ahmedabdelmohsen.egytaxi.databinding.ActivitySignInBinding;
import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

public class SignInActivity extends AppCompatActivity {

    private ActivitySignInBinding binding;
    private View view;
    private final static int LOGIN_REQUEST_CODE = 1196;
    private List<AuthUI.IdpConfig> providers;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        binding = ActivitySignInBinding.inflate(getLayoutInflater());
//        view = binding.getRoot();
//        setContentView(view);

        setCustomStatusBar(); //set custom status bar
        init();
    }

    private void init() {
        providers = Arrays.asList(
                new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        firebaseAuth = FirebaseAuth.getInstance();
        listener = myFirebaseAuth -> {
            FirebaseUser user = myFirebaseAuth.getCurrentUser();
            if (user != null) {
                Toast.makeText(this, "Welcome: " + FirebaseAuth.getInstance().getCurrentUser().getUid(), Toast.LENGTH_SHORT).show();
            } else {
                setLoginUI();
            }
        };
    }

    private void setLoginUI() {
        AuthMethodPickerLayout pickerLayout = new AuthMethodPickerLayout
                .Builder(R.layout.activity_sign_in)
                .setPhoneButtonId(R.id.btn_phone)
                .setGoogleButtonId(R.id.btn_gmail)
                .build();

        startActivityForResult(AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAuthMethodPickerLayout(pickerLayout)
                .setIsSmartLockEnabled(false)
                .setAvailableProviders(providers)
                .build(), LOGIN_REQUEST_CODE);
    }

    private void setCustomStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
        }
        getWindow().setStatusBarColor(ContextCompat.getColor(SignInActivity.this, R.color.white));// set status background white
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN_REQUEST_CODE) {

            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            } else {
                Toast.makeText(this, "Error: " + response.getError().getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(listener);
    }

    @Override
    protected void onStop() {
        if (firebaseAuth != null && listener != null) {
            firebaseAuth.removeAuthStateListener(listener);
        }
        super.onStop();
    }
}