package com.programmingintelugu.mybuddies;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.programmingintelugu.mybuddies.Models.Post;
import com.programmingintelugu.mybuddies.Utilities.FirebaseUtilities;
import com.programmingintelugu.mybuddies.Utilities.Utility;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PostActivity extends AppCompatActivity {

    @BindView(R.id.chooseImage) ImageView chooseImage;
    @BindView(R.id.titleEditText) TextInputEditText titleEditText;
    @BindView(R.id.descriptionEditText)
    TextInputEditText descriptionEditText;

    @BindView(R.id.action)
    Button action;


    int PICK_IMAGE = 12;
    int STORAGE_PERMISSIONS = 12;

    Uri selectedURI = null;

    Utility utility;

    FirebaseUtilities firebaseUtilities;

    Post post = null;

    boolean isUpdate = false;
    boolean isImageUpdate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        ButterKnife.bind(this);
        utility = new Utility(this);
        firebaseUtilities = new FirebaseUtilities(this);
        if (getIntent().getExtras() != null){
            isUpdate = true;
            post = (Post) getIntent().getExtras().getSerializable("data");
            Glide.with(this).load(post.getImageURL()).into(chooseImage);
            titleEditText.setText(post.getTitle());
            descriptionEditText.setText(post.getDescription());
            action.setText("Update");
        }else {
            isUpdate = false;
            action.setText("Post");
        }

    }

    @OnClick(R.id.chooseImage)
    void chooseImage(){
        selectedURI = null;
        checkRunTimePermissions();

    }

    void checkRunTimePermissions(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE},STORAGE_PERMISSIONS);
            }else {
                pickImage();
            }
        }else{
            pickImage();
        }
    }

    void pickImage(){
        if (isUpdate){
            isImageUpdate = true;
        }
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Pick image"),PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE){
            selectedURI = data.getData();
            chooseImage.setImageURI(selectedURI);
            //Glide.with(this).load(selectedURI).into(chooseImage);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSIONS){
            if (permissions.length > 0){
                pickImage();
            }
        }
    }

    @OnClick(R.id.action)
    void post(){
        if (!isUpdate){
            if (selectedURI == null){
                Toast.makeText(this, "Pick an image", Toast.LENGTH_SHORT).show();
                return;
            }

        }
        if (isUpdate && isImageUpdate && selectedURI == null){
            Toast.makeText(this, "Pick an image", Toast.LENGTH_SHORT).show();
            return;
        }



        if (titleEditText.getText().toString().length() == 0){
            Toast.makeText(this, "Title Required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (descriptionEditText.getText().toString().length() < 10){
            Toast.makeText(this, "Description must be more than 10 characters.", Toast.LENGTH_SHORT).show();
            return;
        }


        if (isUpdate){
            post.setTitle(titleEditText.getText().toString());
            post.setDescription(descriptionEditText.getText().toString());
            if (isImageUpdate)
                firebaseUtilities.deleteImageFirst(selectedURI,post,true);
            else
                firebaseUtilities.updatePost(post);
        }else{
            post = new Post();
            post.setTitle(titleEditText.getText().toString());
            post.setDescription(descriptionEditText.getText().toString());
            firebaseUtilities.uploadImage(selectedURI,post,false);
        }


    }
}
