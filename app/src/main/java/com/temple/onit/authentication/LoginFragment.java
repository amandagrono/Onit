package com.temple.onit.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.*;
import com.temple.onit.OnitApplication;
import com.temple.onit.account.AccountManager;
import com.temple.onit.dashboard.DashboardActivity;
import com.temple.onit.R;
import com.temple.onit.databinding.FragmentLoginBinding;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;


public class LoginFragment extends Fragment implements View.OnClickListener, AccountManager.AccountListener{

    private FragmentLoginBinding fragmentLoginBinding;
    private NavController controller;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInAccount account;
    private static final String PASSWORD = "password";
    private View root;
    private static final int SIGN_IN_CHANNEL = 9001;

    private void googleSignIn(){
        Intent googleSignInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(googleSignInIntent, SIGN_IN_CHANNEL);
    }

    /**
     * retrieve google sign in options for client
     */
    private void googleSignInClient(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);
    }

    /**
     * login using google, called by google service
     * @param idToken
     */
    private void firebaseAuthWithGoogle(String idToken){
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Log.i("google log in", "onComplete: successful log in");
                            user = mAuth.getCurrentUser();
                            onItLogin();
                        }else{
                            Toast.makeText(getContext(), "GOOGLE LOGIN INVALID", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * handle login with user email and password
     * @param email user input
     * @param password user input
     */
    private void emailSignIn(String email, String password){
        Log.i("password", "emailSignIn: password\t" + password);
        try {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        user = mAuth.getCurrentUser();
                        onItLogin();
                    } else {
                        Toast.makeText(getContext(), "LOGIN INVALID", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }catch (Exception e){
            Toast.makeText(getContext(), "LOGIN INVALID", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Handle incoming intentForResult
     *
     * SIGN_IN_CHANNEL: 9001
     *  - channel for google result, if passed sign in with account id
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SIGN_IN_CHANNEL){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try{
                account = task.getResult(ApiException.class);
                Log.d("GOOGLE", "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(requireContext(), "Sign in Failed", Toast.LENGTH_SHORT).show();
                Log.w("GOOGLE", "Google sign in failed", e);
                // ...
            }
        }
    }

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(getActivity());
        mAuth = FirebaseAuth.getInstance();
        googleSignInClient();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentLoginBinding = FragmentLoginBinding.inflate(inflater);
        View view = fragmentLoginBinding.getRoot();
        this.root = view;

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        controller = Navigation.findNavController(view);
        account = GoogleSignIn.getLastSignedInAccount(requireActivity());
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
            launchMain();
        }

        fragmentLoginBinding.RegisterHereTextView.setOnClickListener(this);
        fragmentLoginBinding.LoginButton.setOnClickListener(this);
        fragmentLoginBinding.RegisterHereTextView.setOnClickListener(this);
        fragmentLoginBinding.GoogleSignInButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id){
            case R.id._registerHereTextView:
                controller.navigate(R.id.action_loginFragment2_to_registerFragment2);
                break;
            case R.id._loginButton:
                emailSignIn(fragmentLoginBinding.LoginEmailEditText.getText().toString(),
                        fragmentLoginBinding.LoginPasswordEditText.getText().toString());
                break;
            case R.id._googleSignInButton:
                Log.i("google sign in", "onClick: google");
                googleSignIn();
                break;
        }
    }

    public void onItLogin(){
        OnitApplication.instance.accountManager = new AccountManager(requireContext(), this);
        Log.d("OnitLogin", String.valueOf(Objects.isNull(account)));
        if(account == null){
            OnitApplication.instance.getAccountManager().addUser(this, user.getUid(), PASSWORD, PASSWORD, getContext(), user.getEmail());
        }
        else{
            OnitApplication.instance.getAccountManager().addUser(this, user.getUid(), PASSWORD , PASSWORD, getContext(), account.getEmail());
        }
        Log.i("loginAccountManager", "account " + user.getUid() + " pass: " + PASSWORD);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentLoginBinding = null;
    }

    public void launchMain(){

        Intent intent = new Intent(getContext(), DashboardActivity.class);

        // checking if there was a notification for a reminder request.
        if(requireActivity().getIntent().getExtras() != null){
            intent.putExtras(requireActivity().getIntent().getExtras());
        }

        startActivity(intent);
        requireActivity().finish();
    }

    @Override
    public void onLoginResponse(boolean loggedIn) {
        if (loggedIn) {
            launchMain();
        } else {
            Toast.makeText(getContext(), "SERVER DOWN, TRY AGAIN LATER", Toast.LENGTH_SHORT);
        }
    }

    @Override
    public void onLoginFailed(boolean loggedIn) {

    }

}