/*
 * Copyright (c) 2024.
 * Luomy EQua
 * Under Awesomeness Studios
 */

package awesomenessstudios.schoolprojects.buzortutorialplatform.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object Common {

    val mAuth = FirebaseAuth.getInstance()
    val fireStoreDB = Firebase.firestore.batch()


    const val LOCATION_PERMISSION_REQUEST_CODE = 1001


    private const val PARENTS_REF = "MindSpark Parents"
    private const val CHILDREN_REF = "MindSpark Children"
    private const val QUIZ_REF = "MindSpark Quizzes"
    private const val VIDEOS_REF = "MindSpark Videos"
    private const val OUTDOOR_TASKS_REF = "MindSpark Outdoor Tasks"
    private const val SCORES_REF = "MindSpark Scores"


    val parentsCollectionRef = Firebase.firestore.collection(PARENTS_REF)
    val childrenCollectionRef = Firebase.firestore.collection(CHILDREN_REF)
    val quizCollectionRef = Firebase.firestore.collection(QUIZ_REF)
    val videoCollectionRef = Firebase.firestore.collection(VIDEOS_REF)
    val outdoorTaskCollectionRef = Firebase.firestore.collection(OUTDOOR_TASKS_REF)
    val scoresCollectionRef = Firebase.firestore.collection(SCORES_REF)


    fun logout() {
        mAuth.signOut()
    }


}
