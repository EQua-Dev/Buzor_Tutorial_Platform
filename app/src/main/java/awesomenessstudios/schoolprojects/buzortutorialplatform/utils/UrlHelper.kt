package awesomenessstudios.schoolprojects.buzortutorialplatform.utils

import android.net.Uri
import android.util.Log
import java.net.URLDecoder

object FirebaseUrlHelper {
    /**
     * Extracts a clean, usable download URL from Firebase Storage URL
     * Handles both token-based and non-token URLs
     */
    fun extractDownloadUrl(firebaseUrl: String): String {
        return try {
            val uri = Uri.parse(firebaseUrl)

            // Check if this is a Firebase Storage URL
            if (uri.host?.endsWith("firebasestorage.googleapis.com") == true) {
                // Extract the path without the 'o' prefix
                val encodedPath = uri.pathSegments
                    .drop(1) // Skip the 'o' segment
                    .joinToString("/")

                // Rebuild the URL without token parameters
                Uri.Builder()
                    .scheme("https")
                    .authority("firebasestorage.googleapis.com")
                    .appendPath("v0")
                    .appendPath("b")
                    .appendPath(uri.pathSegments[2]) // The bucket name
                    .appendEncodedPath(encodedPath)
                    .build()
                    .toString()
            } else {
                // If not a Firebase URL, return as-is
                firebaseUrl
            }
        } catch (e: Exception) {
            // Fallback to original URL if parsing fails
            firebaseUrl
        }
    }

    /**
     * Extracts the filename from a Firebase Storage URL
     */
    fun extractFilename(firebaseUrl: String): String {
        return try {
            val uri = Uri.parse(firebaseUrl)
            val lastSegment = uri.lastPathSegment ?: "document"

            // Remove URL-encoded characters and token if present
            URLDecoder.decode(lastSegment, "UTF-8")
                .substringBefore("?")
                .substringBefore("&")
                .takeIf { it.isNotBlank() } ?: "document"
        } catch (e: Exception) {
            "document"
        }
    }

    /**
     * Extracts the file extension from URL
     */
    fun getFileExtension(firebaseUrl: String): String {
        val filename = extractFilename(firebaseUrl)
        Log.d("Firebase Url Helper", "getFileExtension: ${filename.substringAfterLast('.', "").lowercase()}")
        return filename.substringAfterLast('.', "").lowercase()
    }
}