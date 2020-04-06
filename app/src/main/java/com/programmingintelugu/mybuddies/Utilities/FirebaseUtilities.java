package com.programmingintelugu.mybuddies.Utilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.programmingintelugu.mybuddies.LoginActivity;
import com.programmingintelugu.mybuddies.MainActivity;
import com.programmingintelugu.mybuddies.Models.Post;
import com.programmingintelugu.mybuddies.RegisterActivity;

public class FirebaseUtilities {
    Context context;

    public FirebaseAuth firebaseAuth;
    FirebaseStorage firebaseStorage;
    FirebaseFirestore firebaseFirestore;

    ProgressDialog progressDialog;

    Utility utility;

    Activity activity;

    public FirebaseUtilities(Context context){
        this.context = context;
        utility = new Utility(context);
        activity = (Activity) context;
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait... ");
        progressDialog.setCanceledOnTouchOutside(false);
    }

   public void registerWithEmail(String email,String password){
        progressDialog.show();
       progressDialog.setMessage("Creating account ...");
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()){
                    Toast.makeText(context, "Account created successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(intent);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
                progressDialog.dismiss();
                Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void loginWithEmail(String email,String password){
        progressDialog.show();
        progressDialog.setMessage("Logging ...");
        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()){
                    Toast.makeText(context, "Login successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(intent);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
                progressDialog.dismiss();
                Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void uploadImage(Uri uri, Post post,boolean isFromUpdate){
        progressDialog.show();
        progressDialog.setMessage("Image Uploading ...");
      StorageReference reference =  firebaseStorage.getReference().child("Posts").child("image_"+utility.getDateTimeStamp()+".jpg");
      reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
          @Override
          public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
              reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                  @Override
                  public void onSuccess(Uri uri) {
                        progressDialog.dismiss();
                        post.setImageURL(uri.toString());
                        if (isFromUpdate){
                            updatePost(post);
                        }else{
                            postStatus(post);
                        }

                  }
              });
          }
      }).addOnFailureListener(new OnFailureListener() {
          @Override
          public void onFailure(@NonNull Exception e) {
              progressDialog.dismiss();
              e.printStackTrace();
              Toast.makeText(context, "Image upload failed due to "+e.getMessage(), Toast.LENGTH_SHORT).show();
          }
      });
    }

    public void postStatus(Post post){
        progressDialog.show();
        progressDialog.setMessage("Post is uploading ...");
        DocumentReference documentReference = firebaseFirestore.collection("posts").document();
        post.setDocID(documentReference.getId());
        documentReference.set(post).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    progressDialog.dismiss();
                    Toast.makeText(context, "Posted successfully", Toast.LENGTH_SHORT).show();
                    activity.finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                e.printStackTrace();
                Toast.makeText(context, "Image upload failed due to "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void deleteImageFirst(Uri uri,Post post,boolean isFromUpdate){
        progressDialog.show();
        progressDialog.setMessage("Image Deleting ...");
        FirebaseStorage.getInstance().getReferenceFromUrl(post.getImageURL()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressDialog.dismiss();
                if (isFromUpdate){
                    uploadImage(uri,post,true);
                }else{
                    deletePost(post);
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
                progressDialog.dismiss();
            }
        });
    }

    public void deletePost(Post post){
        progressDialog.show();
        progressDialog.setMessage("Post Deleting ...");
        firebaseFirestore.collection("posts").document(post.getDocID()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()){
                    Toast.makeText(context, "Post Deleted Successfully.", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                e.printStackTrace();
                Toast.makeText(context, "failed due to "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updatePost(Post post){
        progressDialog.show();
        progressDialog.setMessage("Post Updating ...");
        firebaseFirestore.collection("posts").document(post.getDocID()).update(post.toMap()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()){
                    Toast.makeText(context, "Post Updated Successfully.", Toast.LENGTH_SHORT).show();
                    activity.finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                e.printStackTrace();
                Toast.makeText(context, "failed due to "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



   public void logout(){
        new AlertDialog.Builder(context)
                .setTitle("Logout")
                .setMessage("Do you want to logout?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        firebaseAuth.signOut();
                        Intent intent = new Intent(context, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(intent);
                        activity.finish();



                    }
                }).setNegativeButton("No",null).create().show();
    }
}
