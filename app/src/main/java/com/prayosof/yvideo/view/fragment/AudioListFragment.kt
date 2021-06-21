package com.prayosof.yvideo.view.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.prayosof.yvideo.BuildConfig
import com.prayosof.yvideo.R
import com.prayosof.yvideo.adapter.AudioListAdapter
import com.prayosof.yvideo.model.ModelAudios
import com.prayosof.yvideo.view.fragment.audio.TracksFragment
import java.io.File
import java.util.*

class AudioListFragment : Fragment(), AudioListAdapter.MediaListner {
    var boolean_folder = false
    var audiosModelList = ArrayList<ModelAudios>()

    private val AUDIO_REQUEST_PERMISSIONS = 100

    lateinit var obj_adapter : AudioListAdapter

    var column_index_data = 0
    var column_index_folder_name:Int = 0
    lateinit var rvAudios: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_audio_list, container, false)

        // Lookup the recyclerview in activity layout

        // Lookup the recyclerview in activity layout
        rvAudios = root.findViewById<View>(R.id.rvAudioFolders) as RecyclerView
        rvAudios.setHasFixedSize(true)
        rvAudios.setLayoutManager(LinearLayoutManager(context))


        getPermision()
        return root
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    fun getPermision() {
        if (ContextCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(requireActivity(),
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) && ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                            Manifest.permission.READ_EXTERNAL_STORAGE)) {
            } else {
                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
                        AUDIO_REQUEST_PERMISSIONS)
                //                ActivityCompat.requestPermissions(requireActivity(),
//                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
//                        REQUEST_PERMISSIONS);
            }
        } else {
            //Log.e("Else","Else");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                getAudios()
            } else {
                getAudios()
            }
        }
    }

    @SuppressLint("Recycle")
    @RequiresApi(api = Build.VERSION_CODES.Q)
    fun getAudios() {
        audiosModelList.clear()
        var int_position = 0
        boolean_folder = false
        val uri: Uri
        val cursor: Cursor?

        // e.setAllVideoPath(recentsPath);
        var recentMaxCount = 0
        //        videosModelList.add(e);
        var absolutePathOfImage: String? = null
        uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.Audio.AudioColumns.DATA, MediaStore.Audio.Media.ALBUM)
        val orderBy = MediaStore.Audio.Media.DATE_TAKEN
        cursor = requireActivity().contentResolver.query(uri, projection, null, null, null)//"$orderBy DESC"
        if (cursor != null) {
            column_index_data = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DATA)
        }
        if (cursor != null) {
            column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
        }
        if (BuildConfig.DEBUG && cursor == null) {
            error("Assertion failed")
        }
        while (cursor!!.moveToNext()) {

            absolutePathOfImage = cursor.getString(column_index_data)


            val fileName: String = File(absolutePathOfImage).parentFile.name // you file name


            for (i in audiosModelList.indices) {
                if (audiosModelList[i].str_folder == fileName) {
                    boolean_folder = true
                    int_position = i
                    break
                } else {
                    boolean_folder = false
                }
            }
            if (boolean_folder) {
                val al_path = ArrayList<String?>()
                //                if (videosModelList.size() == 0 && int_position == 0) {
//                    return;
//                }
                al_path.addAll(audiosModelList[int_position].allAudioPaths)
                al_path.add(absolutePathOfImage)
                audiosModelList[int_position].allAudioPaths = al_path
            } else {
                val al_path = ArrayList<String?>()
                al_path.add(absolutePathOfImage)
                val obj_model = ModelAudios()
                obj_model.str_folder = fileName
                obj_model.allAudioPaths = al_path
                audiosModelList.add(obj_model)
            }
        }

        for (i in audiosModelList.indices) {
            //Log.e("FOLDER", videosModelList.get(i).getStr_folder());
            for (j in audiosModelList[i].allAudioPaths.indices) {
                //Log.e("FILE", videosModelList.get(i).getAllVideoPath().get(j));
            }
        }


//        ModelVideos last = videosModelList.get(videosModelList.size() - 1);
//        videosModelList.remove(videosModelList.size() - 1);
//        videosModelList.add(0, last);
        obj_adapter = AudioListAdapter(activity, audiosModelList, this)
        // rvVideos.setAdapter(obj_adapter)

        //gv_folder.setAdapter(obj_adapter);
        obj_adapter = AudioListAdapter(activity, audiosModelList, this)
        rvAudios.adapter = obj_adapter
        //rvAudios.setAdapter(obj_adapter)
    }

    @SuppressLint("Recycle")
    @RequiresApi(api = Build.VERSION_CODES.Q)
    fun getAudiosPath() {
        audiosModelList.clear()
        var int_position = 0
        boolean_folder = false
        val uri: Uri
        val cursor: Cursor?
        var absolutePathOfImage: String? = null
        uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.DISPLAY_NAME)
        //val orderBy = MediaStore.Audio.Media.DATE_TAKEN
        cursor = requireActivity().contentResolver.query(uri, null, MediaStore.Audio.Media.IS_MUSIC, null, null)
        if (cursor != null) {
            column_index_data = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
        }
        if (cursor != null) {
            column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
        }
        if (BuildConfig.DEBUG && cursor == null) {
            error("Assertion failed")
        }
        while (cursor!!.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data)
            //Log.e("Column", absolutePathOfImage);
            //Log.e("Folder", cursor.getString(column_index_folder_name));
            for (i in audiosModelList.indices) {
                if (audiosModelList[i].str_folder == cursor.getString(column_index_folder_name)) {
                    boolean_folder = true
                    int_position = i
                    break
                } else {
                    boolean_folder = false
                }
            }
            if (boolean_folder) {
                val al_path = ArrayList<String?>()
                //                if (videosModelList.size() == 0 && int_position == 0) {
//                    return;
//                }
                al_path.addAll(audiosModelList[int_position].allAudioPaths)
                al_path.add(absolutePathOfImage)
                audiosModelList[int_position].allAudioPaths = al_path
            } else {
                val al_path = ArrayList<String?>()
                al_path.add(absolutePathOfImage)
                val obj_model = ModelAudios()
                obj_model.str_folder = cursor.getString(column_index_folder_name)
                obj_model.allAudioPaths = al_path
                audiosModelList.add(obj_model)
            }

        }
        for (i in audiosModelList.indices) {
            //Log.e("FOLDER", videosModelList.get(i).getStr_folder());
            for (j in audiosModelList[i].allAudioPaths) {
                //Log.e("FILE", videosModelList.get(i).getAllVideoPath().get(j));
            }
        }
        obj_adapter = AudioListAdapter(activity, audiosModelList, this)
        rvAudios.setAdapter(obj_adapter)

        //gv_folder.setAdapter(obj_adapter);
    }



    fun Get_Folder_Paths(): ArrayList<String>? {
        val columns = arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.TRACK,
                MediaStore.Audio.Media.ARTIST_ID,
                MediaStore.Audio.Media.DISPLAY_NAME)
        val FoldersList: ArrayList<String> = ArrayList()
        val DATAList: ArrayList<String> = ArrayList()
        val DISPLAY_NAMEList: ArrayList<String> = ArrayList()
        val cursor: Cursor? = requireActivity().contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                columns,
                MediaStore.Audio.Media.IS_MUSIC + " = 1", null, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                if (cursor != null) {
                    do {
                        if (cursor != null) {
                            DATAList.add(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)))
                        }
                        DISPLAY_NAMEList.add(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)))
                    } while (cursor.moveToNext())
                }
            }
        }
        for (i in 0 until DATAList.size) {
            FoldersList.add(DATAList[i].replace(DISPLAY_NAMEList[i], ""))
        }
        val hs = HashSet<Any>()
        hs.addAll(FoldersList) // demoArrayList= name of arrayList from which u want to remove duplicates
        FoldersList.clear()
     //   FoldersList.addAll(hs)
        cursor?.close()
        return FoldersList
    }

    @SuppressLint("NewApi")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == AUDIO_REQUEST_PERMISSIONS) {
            for (i in grantResults.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        //getAudiosPath()
                        getAudios()
                    }
                } else {
                    Toast.makeText(requireActivity(), "The app was not allowed to read or write to your storage. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

   override fun onItemClick(pos: Int) {


//        MediaListFragment nextFrag= new MediaListFragment();
//        Bundle args = new Bundle();
//        args.putInt("value", pos);
//        nextFrag.setArguments(args);

//        MediaListFragment nextFrag= new MediaListFragment();
//        Bundle args = new Bundle();
//        args.putInt("value", pos);
//        nextFrag.setArguments(args);
//
//        requireActivity().getSupportFragmentManager().beginTransaction()
//                .replace(R.id.nav_host_fragment, nextFrag, "findThisFragment")
//                .addToBackStack(null)
//                .commit();
       TracksFragment.newInstanceFolders(audiosModelList.get(pos), "folders")
       val bundle = bundleOf("folderTracks" to audiosModelList.get(pos), "from_page" to "folders")

       Navigation.findNavController(this.requireView()).navigate(R.id.tracksFragment, bundle)
        Navigation.findNavController(requireView()).navigate(R.id.tracksFragment, bundle)

//        requireActivity().getSupportFragmentManager().beginTransaction()
//                .replace(R.id.nav_host_fragment, nextFrag, "findThisFragment")
//                .addToBackStack(null)
//                .commit();
    }
}