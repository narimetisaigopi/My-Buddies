package com.programmingintelugu.mybuddies;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.programmingintelugu.mybuddies.Adapters.RecyclerviewPostsAdapter;
import com.programmingintelugu.mybuddies.Models.Post;
import com.programmingintelugu.mybuddies.Utilities.FirebaseUtilities;
import com.programmingintelugu.mybuddies.myinterfaces.RefreshList;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements RefreshList {

    FirebaseAuth firebaseAuth;

    FloatingActionButton floatingActionButton;
    TextView statusView;

    FirebaseUtilities firebaseUtilities;

    ImageView chooseImage;

    TextInputEditText titleTextInputEditText,descriptionTextInputEditText;
    Button post;

    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;

    ListenerRegistration listenerRegistration;

    List<Post> postList;

    ProgressDialog progressDialog;

    RecyclerviewPostsAdapter recyclerviewPostsAdapter;

    boolean isFirstTimeLoaded = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUtilities = new FirebaseUtilities(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait... ");
        progressDialog.setCanceledOnTouchOutside(false);

        postList = new ArrayList();

        titleTextInputEditText = findViewById(R.id.titleEditText);
        descriptionTextInputEditText = findViewById(R.id.descriptionEditText);

        floatingActionButton = findViewById(R.id.fab);
        statusView  = findViewById(R.id.status);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,PostActivity.class);
                startActivity(intent);
            }
        });
    }


    void fetchPosts(){
        postList.clear();
        progressDialog.show();
        CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("posts");
        listenerRegistration = collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null){
                    progressDialog.dismiss();
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }


                if (queryDocumentSnapshots!=null && queryDocumentSnapshots.size() > 0){
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()){

                        postList.add(documentSnapshot.toObject(Post.class));
                    }
                }

                recyclerviewPostsAdapter = new RecyclerviewPostsAdapter(MainActivity.this,postList,MainActivity.this);
                recyclerview.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                recyclerview.setAdapter(recyclerviewPostsAdapter);

                progressDialog.dismiss();
            }
        });
    }
    void fetchPosts2(){
        postList.clear();
        progressDialog.show();
        CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("posts");
        listenerRegistration = collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null){
                    progressDialog.dismiss();
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }


                if (queryDocumentSnapshots!=null && queryDocumentSnapshots.size() > 0){
                    for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()){
                        if (documentChange.getType() == DocumentChange.Type.ADDED){
                            if (!isFirstTimeLoaded){
                                // new item added
                                postList.add(documentChange.getNewIndex(), documentChange.getDocument().toObject(Post.class));
                                recyclerviewPostsAdapter.notifyItemInserted(documentChange.getNewIndex());
                                recyclerviewPostsAdapter.notifyItemRangeChanged(0, postList.size());
                            }else {
                                postList.add(documentChange.getDocument().toObject(Post.class));
                            }

                        }
                        if (documentChange.getType() == DocumentChange.Type.MODIFIED){
                            Post post = documentChange.getDocument().toObject(Post.class);
                            if (documentChange.getNewIndex() == documentChange.getOldIndex()){
                                // Item changed but remained in same position
                                postList.set(documentChange.getOldIndex(),post);
                                recyclerviewPostsAdapter.notifyItemChanged(documentChange.getOldIndex());
                            }else {
                                // Item changed and changed position
                                postList.remove(documentChange.getOldIndex());
                                postList.set(documentChange.getNewIndex(),post);
                                recyclerviewPostsAdapter.notifyItemMoved(documentChange.getOldIndex(),documentChange.getNewIndex());
                            }

                            recyclerviewPostsAdapter.notifyDataSetChanged();
                        }
                        if (documentChange.getType() == DocumentChange.Type.REMOVED){
                            // item deleted
                            postList.remove(documentChange.getOldIndex());
                            recyclerviewPostsAdapter.notifyItemRemoved(documentChange.getOldIndex());
                        }
                    }

                    isFirstTimeLoaded = false;
                }

                recyclerviewPostsAdapter = new RecyclerviewPostsAdapter(MainActivity.this,postList,MainActivity.this);
                recyclerview.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                recyclerview.setAdapter(recyclerviewPostsAdapter);

                progressDialog.dismiss();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_layout_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout){
            firebaseUtilities.logout();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (listenerRegistration!=null){
            listenerRegistration.remove();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkUserLoggedInOrNot();
    }

    void checkUserLoggedInOrNot(){
        if (firebaseAuth.getCurrentUser() == null){
            statusView.setVisibility(View.GONE);
            Intent intent = new Intent(MainActivity.this,RegisterActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }else{
            statusView.setText("Logged as : "+firebaseAuth.getCurrentUser().getEmail());
            if (listenerRegistration == null)
                fetchPosts2();
        }
    }

    @Override
    public void refreshList() {

    }
}
