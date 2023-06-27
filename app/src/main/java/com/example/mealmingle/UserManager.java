package com.example.mealmingle;

import android.util.Log;

import androidx.annotation.Nullable;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class UserManager {

    public static FirebaseUser user;
    public static FirebaseFirestore db;

    public static String uid;

    public static DocumentSnapshot userData;

    public static DocumentSnapshot initUser (FirebaseUser u) {
        user = u;
        uid = user.getUid();
        // check in /users in firebase Firestore, if user exists, do nothing, else create user
        // users : {
        //  uid : {
        //      type: "HOTELMANAGER" or "USER" or "NGO"
        //      }
        //  }

        if (db == null)
            db = FirebaseFirestore.getInstance();

        // attach realtime listner
        db.collection("users").document(user.getUid()).addSnapshotListener((value, error) -> {
            if (error != null)
                Log.d("UserManager", error.toString());
            else {
                Log.d("UserManager", value.toString());
                if (!value.exists()) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("type", "USER");
                    // set data and also add in userData from newly set data
                    db.collection("users").document(user.getUid()).set(data).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            db.collection("users").document(user.getUid()).get().addOnCompleteListener(task2 -> {
                                if (task2.isSuccessful()) {
                                    userData = task2.getResult();
                                }
                            });
                        }
                    });
                }
                else {
                    userData = value;
                }
            }
        });

        return userData;

    }

    public static DocumentSnapshot getUserData () {
        return userData;
    }

}
