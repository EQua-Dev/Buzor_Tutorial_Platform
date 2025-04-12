package awesomenessstudios.schoolprojects.buzortutorialplatform.di

import awesomenessstudios.schoolprojects.buzortutorialplatform.repositories.coursesrepo.CourseRepository
import awesomenessstudios.schoolprojects.buzortutorialplatform.repositories.coursesrepo.CourseRepositoryImpl
import awesomenessstudios.schoolprojects.buzortutorialplatform.repositories.teacherrepo.TeacherRepository
import awesomenessstudios.schoolprojects.buzortutorialplatform.repositories.teacherrepo.TeacherRepositoryImpl
import awesomenessstudios.schoolprojects.buzortutorialplatform.repositories.walletrepo.WalletRepository
import awesomenessstudios.schoolprojects.buzortutorialplatform.repositories.walletrepo.WalletRepositoryImpl
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

    @Provides
    @Singleton
    fun provideCourseRepository(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore,
        storage: FirebaseStorage
    ): CourseRepository {
        return CourseRepositoryImpl(firestore)
    }
    @Provides
    @Singleton
    fun provideTeacherRepository(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore,
        storage: FirebaseStorage
    ): TeacherRepository {
        return TeacherRepositoryImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideWalletRepository(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore,
        storage: FirebaseStorage
    ): WalletRepository {
        return WalletRepositoryImpl(firestore)
    }
    /*
        @Provides
        @Singleton
        fun provideCitizensRepository(
            firestore: FirebaseFirestore,
            storage: FirebaseStorage
        ): CitizenRepository {
            return CitizenRepositoryImpl(firestore, storage)
        }*/
}
