package com.slaythetrash;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.slaythetrash.Models.PreferenceManager;
import com.slaythetrash.Models.User;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private Button startGameButton;
    private Dialog currentDialog;
    private Handler mHandler;
    private ViewGroup rootLayout;
    private Runnable mRunnable;
    FirebaseAuth auth;
    FirebaseDatabase db;
    DatabaseReference users;

    FrameLayout root;
    LinearLayout notifications1;
    LinearLayout notifications2;
    private int[] TrashImages = {
            R.drawable.plastic_cup,
            R.drawable.plastic_bag1,
            R.drawable.plastic_bag2,
            R.drawable.glass_bottle1,
            R.drawable.glass_bottle2,
            R.drawable.papers,
            R.drawable.cardboard
    };
    private int[] CloudImages = {
            R.drawable.cloud_1,
            R.drawable.cloud_2,
            R.drawable.cloud_3,
            R.drawable.cloud_4
    };
    private void startFlyingClouds() {
        mHandler = new Handler();
        mRunnable = new Runnable() {
            @Override
            public void run() {
                addFlyingCloud();
                mHandler.postDelayed(this, 5000);
            }
        };
        mHandler.post(mRunnable);
    }

    private void addFlyingCloud() {
        ImageView cloudImageView = new ImageView(this);
        int randomIndex = new Random().nextInt(CloudImages.length);
        cloudImageView.setImageResource(CloudImages[randomIndex]);
        int imageWidth = 200;
        int imageHeight = 200;
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        int xPosition = -imageWidth+200;
        int yPosition = new Random().nextInt(screenWidth / 2);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(imageWidth, imageHeight);
        layoutParams.leftMargin = xPosition;
        layoutParams.topMargin = yPosition;
        cloudImageView.setLayoutParams(layoutParams);
        cloudImageView.setTranslationZ(-2);
        Animation flyAnimation = AnimationUtils.loadAnimation(this, R.anim.fly_animation);
        flyAnimation.setDuration(20000);
        cloudImageView.startAnimation(flyAnimation);

        rootLayout = findViewById(R.id.layout);
        rootLayout.addView(cloudImageView);
    }
    private void startFallingTrash() {
        mHandler = new Handler();
        mRunnable = new Runnable() {
            @Override
            public void run() {
                addFallingTrash();
                mHandler.postDelayed(this, 2000);
            }
        };
        mHandler.post(mRunnable);
    }
    private void addFallingTrash() {
        ImageView trashImageView = new ImageView(this);
        int randomIndex = new Random().nextInt(TrashImages.length);
        trashImageView.setImageResource(TrashImages[randomIndex]);
        int imageWidth = 200;
        int imageHeight = 200;
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int xPosition = new Random().nextInt(screenWidth - imageWidth);
        int yPosition = -imageHeight;

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(imageWidth, imageHeight);
        layoutParams.leftMargin = xPosition;
        layoutParams.topMargin = yPosition;
        trashImageView.setLayoutParams(layoutParams);
        trashImageView.setTranslationZ(-1);
        Animation fallAnimation = AnimationUtils.loadAnimation(this, R.anim.fall_animation);
        fallAnimation.setDuration(20000);
        trashImageView.startAnimation(fallAnimation);
        rootLayout = findViewById(R.id.layout);
        root.addView(trashImageView);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startGameButton = findViewById(R.id.loginLinkTextView);
        root = findViewById(R.id.layout);
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        users = db.getReference("Users");
        FirebaseAuth auth = FirebaseAuth.getInstance();
        startFallingTrash();
        startFlyingClouds();
        startGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (auth.getCurrentUser() != null) {

                    setContentView(R.layout.welcome_window);

                    Button btnLogout = findViewById(R.id.btn_logout);
                    Button btnContinue = findViewById(R.id.btn_continue);

                    btnLogout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PreferenceManager preferenceManager = new PreferenceManager(getApplicationContext());
                            preferenceManager.setUserId(null);
                            FirebaseAuth.getInstance().signOut();
                            startActivity(new Intent(MainActivity.this, MainActivity.class));

                        }
                    });

                    btnContinue.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(MainActivity.this, Game.class));
                            finish();
                        }
                    });
                }
                else{showLoginDialog();}
            }
        });
    }

    private void showRegistrationDialog() {
        // setContentView(R.layout.register_window);
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        currentDialog = dialog.create();
        dialog.setTitle("Регистрация");
        dialog.setMessage("Заполните регистрационные поля");
        LayoutInflater inflater = LayoutInflater.from(this);
        View register_window = inflater.inflate(R.layout.register_window, null);
        dialog.setView(register_window);
        notifications1 = register_window.findViewById(R.id.layout);
        final MaterialEditText login = register_window.findViewById(R.id.loginField);
        final MaterialEditText password = register_window.findViewById(R.id.passwordField);
        final AlertDialog registerDialog = dialog.create();
        Button SkipButton = register_window.findViewById(R.id.skipButton);
        SkipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGame();
            }
        });
        Button AuthorizationButton = register_window.findViewById(R.id.registerButton);
        AuthorizationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(login.getText().toString())) {
                    Snackbar.make(notifications1, "Введите почту", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (password.getText().toString().length() < 5) {
                    Snackbar.make(notifications1, "Пароль должен содержать не менее 5-ти символов", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                auth.createUserWithEmailAndPassword(login.getText().toString(), password.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        User user = new User();
                        user.setLogin(login.getText().toString());
                        user.setPassword(password.getText().toString());

                        users.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Snackbar.make(notifications1, "Пользователь добавлен", Snackbar.LENGTH_SHORT).show();
                                startGame();
                            }
                        });
                    }
                });

            }
        });

        TextView loginLinkTextView = register_window.findViewById(R.id.loginLinkTextView);
        loginLinkTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDialog(registerDialog);
                showLoginDialog();
            }
        });
        registerDialog.show();
    }
    private void closeDialog(DialogInterface dialogInterface) {
        if (dialogInterface != null) {
            dialogInterface.dismiss();
        }
    }

    private void showLoginDialog() {
     //   setContentView(R.layout.sign_window);
        //currentDialog = new Dialog(MainActivity.this);
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Авторизация");
        dialog.setMessage("Введите вашу почту и пароль");
        LayoutInflater inflater = LayoutInflater.from(this);
        View sign_window = inflater.inflate(R.layout.sign_window,null);
        dialog.setView(sign_window);
        notifications2 = sign_window.findViewById(R.id.layout);
        final MaterialEditText login = sign_window.findViewById(R.id.loginField);
        final MaterialEditText password = sign_window.findViewById(R.id.passwordField);
        final AlertDialog registerDialog = dialog.create();
        Button SkipButton = sign_window.findViewById(R.id.skipButton);
        SkipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGame();
            }
            });

        Button AuthorizationButton = sign_window.findViewById(R.id.authorizationButton);
        AuthorizationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(login.getText().toString())) {
                    Snackbar.make(notifications2, "Введите почту", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (password.getText().toString().length() < 5) {
                    Snackbar.make(notifications2, "Пароль должен содержать не менее 5-ти символов", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                auth.signInWithEmailAndPassword(login.getText().toString(), password.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                startActivity(new Intent(MainActivity.this, Game.class));//Сделать выбор уровней
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Snackbar.make(notifications2, "Ошибка авторизации. " + e.getMessage(), Snackbar.LENGTH_SHORT).show();
                            }
                        });
            }
        });
        TextView loginLinkTextView = sign_window.findViewById(R.id.loginLinkTextView);
        loginLinkTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDialog(registerDialog);
                    showRegistrationDialog();
                }
        });
        registerDialog.show();
    }
    private void startGame() {
        Intent intent = new Intent(MainActivity.this, Game.class);
        startActivity(intent);
    }
}