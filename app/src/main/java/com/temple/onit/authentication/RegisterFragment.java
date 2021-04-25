package com.temple.onit.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.temple.onit.OnitApplication;
import com.temple.onit.account.AccountManager;
import com.temple.onit.dashboard.DashboardActivity;
import com.temple.onit.R;
import com.temple.onit.databinding.FragmentRegisterBinding;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment implements View.OnClickListener{
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FragmentRegisterBinding fragmentRegisterBinding;
    NavController navController;

    // can register if username and password fit constraints
    boolean canRegister = false;

    public RegisterFragment() {
        // Required empty public constructor
    }

    public static RegisterFragment newInstance() {
        RegisterFragment fragment = new RegisterFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentRegisterBinding = FragmentRegisterBinding.inflate(inflater);
        View view = fragmentRegisterBinding.getRoot();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        fragmentRegisterBinding.RegisterButton.setOnClickListener(this);
        fragmentRegisterBinding.RegisterEmailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            /**
             * check that email is standard format, throw error if not
             */
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String email = fragmentRegisterBinding.RegisterEmailEditText.getText().toString();
                TextInputLayout emailTextLayout = fragmentRegisterBinding.EmailTextInputLayout;
                if (charSequence.length() > 0 && email.length() > 0 ) {

                    if (!email.contains("@") || !email.endsWith(".com")) {
                        emailTextLayout.setError("USE VALID EMAIL");
                        canRegister = false;
                    }else{
                        canRegister = true;
                        emailTextLayout.setError(null);
                    }
                } else{
                    emailTextLayout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        fragmentRegisterBinding.RegisterPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            /**
             * check that password is at least size 4
             */
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (fragmentRegisterBinding.RegisterPasswordEditText.length() < 4){
                    fragmentRegisterBinding.PasswrodTextInputLayout.setError("PASSWORD LENGTH LESS THAN 4");
                    canRegister = false;
                }else{
                    fragmentRegisterBinding.PasswrodTextInputLayout.setError(null);
                    canRegister = true;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id._registerButton :
                if (canRegister) {
                    emailRegister(fragmentRegisterBinding.RegisterEmailEditText.getText().toString(),
                            fragmentRegisterBinding.RegisterPasswordEditText.getText().toString());

                }else {
                    Toast.makeText(getContext(), "EMAIL OR PASSWORD BAD FORMAT", Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }

    /**
     * register with firebase authentication using email/password
     * @param email input
     * @param password input
     */
    private void emailRegister(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    launchMain();
                } else {
                    Objects.requireNonNull(task.getException()).printStackTrace();
                    Toast.makeText(getContext(), "REGISTRATION FAILED, USER MAY ALREADY EXIST", Toast.LENGTH_SHORT).show();
                    //  Log.i("register failed", "onComplete: " + task.getResult().toString());
                }
            }
        });


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentRegisterBinding = null;
    }



    public void launchMain(){
        Intent intent = new Intent(getContext(), DashboardActivity.class);
        startActivity(intent);
    }
}