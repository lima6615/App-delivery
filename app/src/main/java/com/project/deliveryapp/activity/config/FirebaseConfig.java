package com.project.deliveryapp.activity.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

public class FirebaseConfig {

    private static FirebaseFirestore firestore;
    private static FirebaseAuth auth;
    private static FirebaseStorage storage;

    public static String getIdUsuario() {
        FirebaseAuth fireAuth = getAuth();
        return fireAuth.getCurrentUser().getUid();
    }

    public static FirebaseFirestore getFirestore() {
        if (firestore == null) {
            firestore = FirebaseFirestore.getInstance();
        }
        return firestore;
    }

    public static FirebaseAuth getAuth() {
        if (auth == null) {
            auth = FirebaseAuth.getInstance();
        }
        return auth;
    }

    public static FirebaseStorage getStorage() {
        if (storage == null) {
            storage = FirebaseStorage.getInstance();
        }
        return storage;
    }
}
