package com.amst.proyecto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private EditText txtnombre,txtmail,txtpassword;
    private Button inicio,existe;
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    private String nombre,mail,password;

    private VideoView videoBG;
    MediaPlayer mMediaPlayer;
    int mCurrentVideoPosition;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mAuth=FirebaseAuth.getInstance();
        mDatabase= FirebaseDatabase.getInstance().getReference();



        Videothing();

        txtnombre=(EditText) findViewById(R.id.NameUsuario);
        txtmail=(EditText)findViewById(R.id.EmailUsuario);
        txtpassword=(EditText)findViewById(R.id.PasswordUsuario);
        inicio=(Button)findViewById(R.id.btnRegistro);
        existe=(Button)findViewById(R.id.btnExisteUsuario);

        inicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nombre=txtnombre.getText().toString();
                mail=txtmail.getText().toString();
                password=txtpassword.getText().toString();

                if (!nombre.isEmpty() && !mail.isEmpty() && !password.isEmpty()){
                    if (password.length()>=6){
                        registerUser();
                    }
                    else{
                        Toast.makeText(MainActivity.this,"Password al menos 6 caracteres", Toast.LENGTH_SHORT).show();

                    }

                }
                else{
                    Toast.makeText(MainActivity.this,"Debe completar los campos", Toast.LENGTH_SHORT).show();
                }
            }
            });
        existe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent (MainActivity.this,Inicio_sesion.class ));
            }
        });

    }


    private void Videothing(){
        videoBG = (VideoView) findViewById(R.id.videoView);
        Uri uri = Uri.parse("android.resource://" // First start with this,
                + getPackageName() // then retrieve your package name,
                + "/" // add a slash,
                + R.raw.fire);
        videoBG.setVideoURI(uri);
        // Start the VideoView
        videoBG.start();
        videoBG.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mMediaPlayer = mediaPlayer;
                // We want our video to play over and over so we set looping to true.
                mMediaPlayer.setLooping(true);
                // We then seek to the current posistion if it has been set and play the video.
                if (mCurrentVideoPosition != 0) {
                    mMediaPlayer.seekTo(mCurrentVideoPosition);
                    mMediaPlayer.start();
                }
            }
        });
    }

    private void registerUser() {
        mAuth.createUserWithEmailAndPassword(mail,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
               if (task.isSuccessful()){
                   Map<String,Object> map=new HashMap<>();
                   map.put("name",nombre);
                   map.put("email",mail);
                   map.put("password",password);


                   String id= mAuth.getCurrentUser().getUid();

                   mDatabase.child("Usuario").child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                       @Override
                       public void onComplete(@NonNull Task<Void> task2) {
                           if (task2.isSuccessful()){
                               startActivity(new Intent(MainActivity.this,Inicio_sesion.class));
                               finish();

                           }
                           else{
                               Toast.makeText(MainActivity.this,"No se pudo crear los datos correctamente", Toast.LENGTH_SHORT).show();

                           }
                       }
                   });
               }
               else{
                   Toast.makeText(MainActivity.this,"No se pudo registrar este usuario", Toast.LENGTH_SHORT).show();

               }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Capture the current video position and pause the video.
        mCurrentVideoPosition = mMediaPlayer.getCurrentPosition();
        videoBG.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Restart the video when resuming the Activity
        videoBG.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // When the Activity is destroyed, release our MediaPlayer and set it to null.
        mMediaPlayer.release();
        mMediaPlayer = null;
    }


}
