package awesomenessstudios.schoolprojects.buzortutorialplatform.di

import android.content.Context
import android.location.Geocoder
import awesomenessstudios.schoolprojects.buzortutorialplatform.BuildConfig
import awesomenessstudios.schoolprojects.buzortutorialplatform.utils.LocationUtils
import awesomenessstudios.schoolprojects.buzortutorialplatform.utils.OpenAIService
import awesomenessstudios.schoolprojects.buzortutorialplatform.utils.UserPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.Locale
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideUserPreferences(@ApplicationContext context: Context): UserPreferences {
        return UserPreferences(context)
    }

    @Provides
    @Singleton
    fun provideLocationUtils(
        @ApplicationContext context: Context,
        geocoder: Geocoder
    ): LocationUtils {
        return LocationUtils(context, geocoder)
    }

    @Provides
    @Singleton
    fun provideGeocoder(@ApplicationContext context: Context): Geocoder {
        return Geocoder(
            context,
            Locale.getDefault()
        ) // Initialize Geocoder with the application context
    }

    @Provides
    @Singleton
    fun provideOpenAIService(): OpenAIService {
        val apiKey = BuildConfig.OPEN_AI_KEY // Replace with your OpenAI API key
        return OpenAIService(apiKey)
    }
}