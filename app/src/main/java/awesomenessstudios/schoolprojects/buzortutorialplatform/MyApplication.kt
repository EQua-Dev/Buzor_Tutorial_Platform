
package awesomenessstudios.schoolprojects.buzortutorialplatform

import android.app.Application
import androidx.multidex.BuildConfig
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
    }
}