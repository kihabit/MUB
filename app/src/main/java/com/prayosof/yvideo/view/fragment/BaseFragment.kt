package com.prayosof.yvideo.view.fragment

import android.content.ContentUris
import android.content.Intent
import android.provider.MediaStore
import android.view.View
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import java.io.File

/**
 * Created by Yogesh Y. Nikam on 06/08/20.
 */
abstract class BaseFragment : Fragment() {

    open fun startFileShareIntent(filePath: String) { // pass the file path where the actual file is located.
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "*/*"  // "*/*" will accepts all types of files, if you want specific then change it on your need.
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            putExtra(
                    Intent.EXTRA_SUBJECT,
                    "P-MediaPlayer"
            )
            putExtra(
                    Intent.EXTRA_TEXT,
                    "P-MediaPlayer - Download link"
            )
            val fileURI = FileProvider.getUriForFile(
                    requireContext(), requireContext().packageName + ".provider",
                    File(filePath)
            )
            putExtra(Intent.EXTRA_STREAM, fileURI)
        }
        startActivity(shareIntent)
    }

//     open fun deleteFile(filePath: String, id: Long) {
//        val contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id) // get the uri content
//        val file = File(filePath)
//        val deleted: Boolean = file.delete() // delete the file
//        if (deleted) {
//            requireContext().getContentResolver().delete(contentUri, null, null)
//            mFiles.removeAt(position)
//            notifyItemRemoved(position)
//            notifyItemRangeChanged(position, mFiles.size)
//            Snackbar.make(v, "File deleted", Snackbar.LENGTH_LONG).show()
//        } else {
//            // if file is in sd card or api level is 19
//            Snackbar.make(v, "File can't be deleted : ", Snackbar.LENGTH_LONG).show()
//        }
//    }z
}