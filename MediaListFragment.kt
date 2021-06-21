package com.prayosof.yvideo.view.fragment.media_list_with_searchbar

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.prayosof.yvideo.R
import com.prayosof.yvideo.adapter.MediaListAdapter
import com.prayosof.yvideo.interfaces.CustomOnClickListener
import com.prayosof.yvideo.model.AllMediaListModel
import com.prayosof.yvideo.model.ModelVideos
import com.prayosof.yvideo.view.fragment.BaseFragment
import com.prayosof.yvideo.view.fragment.dashboard.VideosListFragment
import com.prayosof.yvideo.view.fragment.video.MainVideoFragment
import java.io.File
import java.util.*


/**
 * A simple [Fragment] subclass.
 * create an instance of this fragment.
 */
class MediaListFragment : BaseFragment(), CustomOnClickListener {

    var rvMediaList: RecyclerView? = null
    var int_position = 0
    var value = 0
    var videosModelList = ArrayList<ModelVideos>()
    var adapter: MediaListAdapter? = null
    var searcMediaList: EditText? = null
    var allVideoPaths = ArrayList<AllMediaListModel>()
    var listener: CustomOnClickListener? = null
    companion object {
        @kotlin.jvm.JvmField
        var count: Int = 0
        lateinit var ivDelete: ImageView
    }
    var ivCross: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            value = requireArguments().getInt("value")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_media_list, container, false)


        // Lookup the recyclerview in activity layout
        ivCross = root.findViewById<View>(R.id.iv_media_cross) as ImageView
        ivDelete = root.findViewById<View>(R.id.iv_media_delete) as ImageView
        rvMediaList = root.findViewById<View>(R.id.rvMediaList) as RecyclerView
        rvMediaList!!.setHasFixedSize(true)
        //rvMediaList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvMediaList!!.layoutManager = GridLayoutManager(context, 2)
        videosModelList = VideosListFragment.videosModelList
        searcMediaList = root.findViewById(R.id.searcMediaList)
        setUI()
        searcMediaList?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.toString().equals("")) {
                    ivCross!!.visibility = View.VISIBLE
                    filter(s.toString())
                } else {
                    ivCross!!.visibility = View.INVISIBLE
                    if (adapter != null) {
                        adapter!!.updateList(allVideoPaths)
                    }
                }
            }
        })

        MainVideoFragment.ivDelete.isClickable = false
        MainVideoFragment.ivDelete.isEnabled = false
        MainVideoFragment.ivDelete.setColorFilter(resources.getColor(R.color.white))

        ivCross!!.setOnClickListener {
            searcMediaList?.setText("")
            searcMediaList?.clearFocus()
            ivCross!!.visibility = View.INVISIBLE
            if (adapter != null) {
                adapter!!.updateList(allVideoPaths)
            }
        }

        ivDelete?.setOnClickListener { v ->
            val builder = AlertDialog.Builder(requireActivity())
            builder.setMessage("Are you really want to delete files?")
            builder.setPositiveButton("Yes") { dialog, which ->
                for (i in 0 until allVideoPaths.size - 1) {
                    if (allVideoPaths[i].isSelected()) {
                        adapter?.deleteFile(i, v)
//                        val file = File(allVideoPaths[i].getFileName())
//                        file.delete()
//                        allVideoPaths.removeAt(i)
//                        adapter?.notifyItemRemoved(i)
//                        adapter?.notifyItemRangeChanged(i, allVideoPaths.size)
//                        i
                    }
                }
//                var i = 0
//                while (i < allVideoPaths.size) {
//                    if (allVideoPaths[i].isSelected()) {
////                        adapter?.deleteFile(i, v)
//                        val file = File(allVideoPaths[i].getFileName())
//                        file.delete()
//                        allVideoPaths.removeAt(i)
//                        adapter?.notifyItemRemoved(i)
//                        adapter?.notifyItemRangeChanged(i, allVideoPaths.size)
//                        i--
//                    }
//                }
            }.setNegativeButton("No") { dialog, which ->
                for (i in allVideoPaths.indices) {
                    allVideoPaths[i].setSelected(false)
                }
                adapter?.updateList(allVideoPaths)
                dialog.dismiss()
            }.setCancelable(false).show()
        }

        return root
    }

    fun filter(text: String?) {
        //val temp: ArrayList<Any?> = ArrayList<Any?>()
        var temp: ArrayList<AllMediaListModel> = ArrayList()

        //KJS
        for (i in allVideoPaths.indices) {
            if (allVideoPaths[i].getFileName().toLowerCase().contains(text!!)) {
                var model = AllMediaListModel()
                model.setFileName(allVideoPaths[i].getFileName())
                model.setSelected(false)
                temp.add(model)
            }
        }

        //update recyclerview
        if (adapter != null) {
            adapter!!.updateList(temp)
        }
    }

    fun setUI() {
        if (videosModelList.size != 0) {
            listener = this

            //KJS
            for (i in 0 until videosModelList[value].allVideoPath.size) {
                var model = AllMediaListModel()
                model.setFileName(videosModelList[value].allVideoPath[i])
                model.setSelected(false)
                allVideoPaths.add(model)
            }
//            allVideoPaths = videosModelList[value].allVideoPath
            adapter = MediaListAdapter(activity, allVideoPaths, value, listener)
            rvMediaList!!.adapter = adapter
        }
    }

    fun updateListSize(list: ArrayList<AllMediaListModel>) {
        allVideoPaths.clear()
        allVideoPaths = list
    }

    override fun onShareClick(fileName: String, filePath: String) {
        startFileShareIntent(filePath)

        //startFileShareIntent(filePath)
//        File file = new File(filePath);
//        Toast.makeText(requireContext(), fileName +" "+ filePath, Toast.LENGTH_LONG).show();
//        Uri photoURI = FileProvider.getUriForFile(requireContext(), requireContext().getApplicationContext().getPackageName() + ".provider", file);
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.setDataAndType(photoURI, "video/*");
//        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
    }


//    override fun startFileShareIntent(filePath: String) { // pass the file path where the actual file is located.
//        val shareIntent = Intent(Intent.ACTION_SEND).apply {
//            type = "*/*"  // "*/*" will accepts all types of files, if you want specific then change it on your need.
//            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
//            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
//            flags = Intent.FLAG_ACTIVITY_NEW_TASK
//            putExtra(
//                    Intent.EXTRA_SUBJECT,
//                    "Sharing file from the AppName"
//            )
//            putExtra(
//                    Intent.EXTRA_TEXT,
//                    "Sharing file from the AppName with some description"
//            )
//            val fileURI = FileProvider.getUriForFile(
//                    requireContext(), requireContext().packageName + ".provider",
//                    File(filePath)
//            )
//            putExtra(Intent.EXTRA_STREAM, fileURI)
//        }
//        startActivity(shareIntent)
//    }
}