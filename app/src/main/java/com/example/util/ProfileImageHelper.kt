package com.example.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object ProfileImageHelper {

    private const val PROFILE_IMAGE_PREFIX = "user_avatar_"
    private const val TEMP_CAMERA_FILENAME = "camera_capture_temp.jpg"

    /**
     * Creates a temporary file in the application's cache directory
     * and returns a content URI via FileProvider for Camera capture.
     */
    fun createTempCameraUri(context: Context): Pair<File, Uri> {
        val cacheDir = context.cacheDir
        val tempFile = File(cacheDir, TEMP_CAMERA_FILENAME)
        if (tempFile.exists()) {
            tempFile.delete()
        }
        tempFile.createNewFile()
        val authority = "${context.packageName}.fileprovider"
        val uri = FileProvider.getUriForFile(context, authority, tempFile)
        return Pair(tempFile, uri)
    }

    /**
     * Copies an image from a content URI (e.g. from Photo Picker/Gallery)
     * into the app's internal persistent files directory.
     * Automatically fixes EXIF rotation if needed and compresses reasonably for avatar use.
     */
    fun copyUriToInternalFile(context: Context, sourceUri: Uri): String? {
        return try {
            cleanupOldAvatars(context)

            val destinationFile = File(context.filesDir, "${PROFILE_IMAGE_PREFIX}${System.currentTimeMillis()}.jpg")
            
            // Read input stream and decode with rotation fix
            var inputStream: InputStream? = context.contentResolver.openInputStream(sourceUri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            if (bitmap == null) return null

            // Check EXIF rotation
            val rotatedBitmap = try {
                context.contentResolver.openInputStream(sourceUri)?.use { stream ->
                    val exif = ExifInterface(stream)
                    val orientation = exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL
                    )
                    rotateBitmapIfNeeded(bitmap, orientation)
                } ?: bitmap
            } catch (e: Exception) {
                bitmap
            }

            FileOutputStream(destinationFile).use { out ->
                rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }

            destinationFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Processes a captured photo from the camera temp file and saves it
     * permanently to internal storage with proper orientation.
     */
    fun processCameraFileToInternal(context: Context, tempFile: File): String? {
        return try {
            if (!tempFile.exists() || tempFile.length() == 0L) return null

            cleanupOldAvatars(context)

            val destinationFile = File(context.filesDir, "${PROFILE_IMAGE_PREFIX}${System.currentTimeMillis()}.jpg")

            val bitmap = BitmapFactory.decodeFile(tempFile.absolutePath) ?: return null

            val rotatedBitmap = try {
                val exif = ExifInterface(tempFile.absolutePath)
                val orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )
                rotateBitmapIfNeeded(bitmap, orientation)
            } catch (e: Exception) {
                bitmap
            }

            FileOutputStream(destinationFile).use { out ->
                rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }

            // Clean up temp file
            tempFile.delete()

            destinationFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Rotates bitmap according to EXIF orientation tag.
     */
    private fun rotateBitmapIfNeeded(bitmap: Bitmap, orientation: Int): Bitmap {
        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.postScale(-1f, 1f)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.postScale(1f, -1f)
            else -> return bitmap
        }

        return try {
            val rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            if (rotated != bitmap) {
                bitmap.recycle()
            }
            rotated
        } catch (e: Exception) {
            bitmap
        }
    }

    /**
     * Cleans up previously saved avatar image files in internal storage
     * to prevent storage accumulation.
     */
    private fun cleanupOldAvatars(context: Context) {
        try {
            val files = context.filesDir.listFiles() ?: return
            for (file in files) {
                if (file.name.startsWith(PROFILE_IMAGE_PREFIX)) {
                    file.delete()
                }
            }
        } catch (e: Exception) {
            // Ignore cleanup failure
        }
    }

    /**
     * Deletes any saved profile avatars.
     */
    fun removeProfileImage(context: Context) {
        cleanupOldAvatars(context)
    }
}
