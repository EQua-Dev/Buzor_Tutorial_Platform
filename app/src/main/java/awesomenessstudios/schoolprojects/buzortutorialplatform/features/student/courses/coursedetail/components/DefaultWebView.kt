package awesomenessstudios.schoolprojects.buzortutorialplatform.features.student.courses.coursedetail.components

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun DefaultWebView(url: String) {
    val context = LocalContext.current
    val webView = remember { WebView(context).apply {
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        webViewClient = WebViewClient()
    } }

    AndroidView(
        factory = { webView },
        update = { view ->
            view.loadUrl(url)
        }
    )
}