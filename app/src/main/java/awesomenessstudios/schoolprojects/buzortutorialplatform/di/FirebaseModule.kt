package awesomenessstudios.schoolprojects.buzortutorialplatform.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage = FirebaseStorage.getInstance()
/*
    @Provides
    @Singleton
    fun provideOfficialsRepository(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore,
        storage: FirebaseStorage
    ): OfficialsRepository {
        return OfficialsRepositoryImpl(auth, firestore, storage)
    }

    @Provides
    @Singleton
    fun provideCitizensRepository(
        firestore: FirebaseFirestore,
        storage: FirebaseStorage
    ): CitizenRepository {
        return CitizenRepositoryImpl(firestore, storage)
    }*/
}
