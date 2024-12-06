package com.khan.fftracker.tracker.userMenuSetting.friendSettings.friendInfo

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.khan.fftracker.Ancillary_Coverages.GetImage_Path
import com.khan.fftracker.Ancillary_Coverages.RefreshGallery
import com.khan.fftracker.logCalls.LogCalls_Debug
import com.khan.fftracker.Network_Volley.IsAdmin
import com.khan.fftracker.Permission_Granted.PermissionFragmentKotlin
import com.khan.fftracker.Permission_Granted.PermissionListener
import com.khan.fftracker.R
import com.khan.fftracker.UtilityStuff.Compress_Image
import com.khan.fftracker.autoverse_mvvm.Network_Stuff.ApiRetrofit
import com.khan.fftracker.autoverse_mvvm.networkApi.ResultApi
import com.khan.fftracker.databinding.CameraGalleryBottomsheetBinding
import com.khan.fftracker.databinding.TrackerFriendinfoBinding
import com.khan.fftracker.login_Stuffs.Login_New
import com.khan.fftracker.tracker.TrackerConstant
import com.khan.fftracker.tracker.friendsDetail.FriendDetailsTracker
import com.khan.fftracker.tracker.aApiTracker.TrackerApi
import com.khan.fftracker.tracker.aApiTracker.TrackerRepo
import com.khan.fftracker.tracker.userMenuSetting.friendSettings.friendInfo.viewModel.FriendInfoVM
import com.sangcomz.fishbun.FishBun
import com.sangcomz.fishbun.adapter.image.impl.GlideAdapter
import id.zelory.compressor.Compressor
import java.io.File
import java.io.IOException
import java.util.Date

class FriendInfo : Fragment(), PermissionListener {

    private val TAG = FriendInfo::class.simpleName

    var binding: TrackerFriendinfoBinding? = null
    var mView: View? = null

    lateinit var viewModel: FriendInfoVM

    /// IsAdmin: Class for using check whether customer or admin logged in
    var isAdmin: IsAdmin? = null

    private var bottomSheetDialog: BottomSheetDialog? = null

    //run time permission fragment
    val TAG_PERMISSIONS_FRAGMENT = "permissions"
    private lateinit var permissionsFragment: PermissionFragmentKotlin

    ///taking image form camera or gallery
    private var bitmap: Bitmap? = null

    private var imgPath: String? = null
    private var file: File? = null
    private var photoURI: Uri? = null
    var path = ArrayList<Uri>()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.tracker_friendinfo, container, false)

        mView = binding!!.root

        setupViewModel()
        /// Initialize class
        initializeAdminClass()


        requireActivity().onBackPressedDispatcher.addCallback(
                requireActivity(),
                onBackPressedCallback
        )


        return mView
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()

        setPermission()

    }


    private fun setupViewModel() {

        val apiInterface = ApiRetrofit.getRetrofitInstance().create(TrackerApi::class.java)
        // View Model with factory
        val factory = FriendInfoVM.VMFactory(TrackerRepo(apiInterface))

        viewModel = ViewModelProvider(this, factory)[FriendInfoVM::class.java]
        ///////////////////////////////////////////////////

        binding!!.lifecycleOwner = this
        binding!!.viewModel = viewModel
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun setupObservers() {

        viewModel.getNavigateCancel().observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { // Only proceed if the event has never been handled
                closeFragment()
            }
        }
        viewModel.getShowBottomSheetDialog().observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { // Only proceed if the event has never been handled
                showBottomSheetDialog()

            }
        }
        viewModel.getCameraLaunch().observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { // Only proceed if the event has never been handled
                permissionsFragment.isPermissionGranted()
                bottomSheetDialog!!.dismiss()

            }
        }
        viewModel.getGalleryOpen().observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { // Only proceed if the event has never been handled
                bottomSheetDialog!!.dismiss()
                takeGalleryPhoto()
            }
        }

        viewModel.getProfileImageSavedResponse().observe(viewLifecycleOwner) {
            LogCalls_Debug.d(LogCalls_Debug.TAG, "getProfileImageSavedResponse")
            when (it.statusApi) {
                ResultApi.StatusApi.SUCCESS -> {
                    hideProgressBar()

                    //save image url in prefs. For last screen old image url will be updated will be new one
                    viewModel.saveFriendImage(it.data!!.url)

                 /*   fragments communication
                     Notify mostly previous fragment that data is changed. So previous fragment will be changed according to their need*/
                    passDataPreviousFragment(TrackerConstant.TRACKER_IMAGE_UPLOAD)
                    Toast.makeText(activity, it.data.message, Toast.LENGTH_LONG).show()

                }

                ResultApi.StatusApi.ERROR -> {
                    Toast.makeText(activity, it.msg, Toast.LENGTH_SHORT).show()
                    LogCalls_Debug.d(LogCalls_Debug.TAG, "ERROR")
                    hideProgressBar()
                }

                ResultApi.StatusApi.LOADING -> {
                    //  Keyboard_Close(activity).keyboard_Close_Down()
                    showProgressBar()
                }

                ResultApi.StatusApi.FAILURE -> {
                    Toast.makeText(activity, it.msg, Toast.LENGTH_SHORT).show()
                }
            }
        }
        //Observe data changes when api called
        viewModel.getSaveLocSettingResponse().observe(viewLifecycleOwner) {
            when (it.statusApi) {
                ResultApi.StatusApi.SUCCESS -> {

                    /*   fragments communication
                     Notify mostly previous fragment that data is changed. So previous fragment will be changed according to their need*/
                    passDataPreviousFragment(TrackerConstant.TRACKER_PRIVACY_SETTING)
                    hideProgressBar()
                    Toast.makeText(activity, it.data!!.message, Toast.LENGTH_LONG).show()

                }

                ResultApi.StatusApi.ERROR -> {
                    Toast.makeText(activity, it.msg, Toast.LENGTH_SHORT).show()
                    LogCalls_Debug.d(LogCalls_Debug.TAG, "ERROR")
                    hideProgressBar()
                }

                ResultApi.StatusApi.LOADING -> {
                    //  Keyboard_Close(activity).keyboard_Close_Down()
                    showProgressBar()
                }

                ResultApi.StatusApi.FAILURE -> {
                    Toast.makeText(activity, it.msg, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /*   fragments communication
                     Notify mostly previous fragment that data is changed. So previous fragment will be changed according to their need*/
    private fun passDataPreviousFragment(strBundle: String) {
        // Use the Kotlin extension in the fragment-ktx artifact.
        setFragmentResult(TrackerConstant.TRACKER_FRIEND_SETTING_LISTENER, bundleOf(strBundle to "yes"))
    }

    fun showProgressBar() {
        isAdmin!!.showhideLoader(View.VISIBLE)
    }

    fun hideProgressBar() {
        isAdmin!!.showhideLoader(View.GONE)
    }


    /// Initialize class
    private fun initializeAdminClass() {
        isAdmin = IsAdmin(activity)
    }

    /// Runtime permissions: Call permission fragment to allow asked permission
    ///set permission fragment
    private fun setPermission() {

        /// Runtime permissions
        permissionsFragment = PermissionFragmentKotlin()

        initializePermissionFragment(permissionsFragment, TAG_PERMISSIONS_FRAGMENT)

        permissionsFragment.setPermissionGrantedListener(this)

    }

    private fun initializePermissionFragment(fragment: Fragment, fragmentTag: String) {
        this.requireActivity().supportFragmentManager.beginTransaction().add(fragment, fragmentTag).commit()
    }

    override fun permissionGrantedListener(strResult: String) {
        LogCalls_Debug.d(TAG, "permissionGrantedListener " + strResult)
        when (strResult) {
            "storage" -> {
                takePhotoIntent()
            }
        }
    }

    //Gallery Intent
    //Gallery Register: get Image from gallery
    private fun takeGalleryPhoto() {

        FishBun.with(this@FriendInfo)
                .setImageAdapter(GlideAdapter())
                .setPickerSpanCount(1)
                .textOnImagesSelectionLimitReached("Select Photo.")
        //.startAlbumWithActivityResultCallback(startForResultCallback)

        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intentGalleryRegister.launch(intent)

    }

    val intentGalleryRegister = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        var uri: Uri? = null
        if (result.resultCode == Activity.RESULT_OK) {
            uri = result.data?.data

            //file=File(GetImage_Path(activity).getPath(uri))
            file = File(uri?.let { GetImage_Path(activity).getFilePathForN(it) })
            val compressFile = Compressor(activity).compressToFile(file)
            // bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, uri)
            // bitmap = BitmapFactory.decodeFile(compressFile!!.absolutePath)
            bitmap = GetImage_Path(activity).handleSamplingAndRotationBitmap(uri!!)
            viewModel.setFriendImage(bitmap, compressFile!!)
        }
    }
    private val startForResultCallback = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            path = it.data?.getParcelableArrayListExtra(FishBun.INTENT_PATH) ?: arrayListOf()

            val listVideos2 = ArrayList<HashMap<String, String>>()
            for (a in 0 until path.size) {
                file = File(GetImage_Path(requireActivity()).getFilePathForN(path.get(a)))
                bitmap = BitmapFactory.decodeFile(file!!.absolutePath)
                viewModel.setFriendImage(bitmap, file!!)

            }

            LogCalls_Debug.d(TAG, listVideos2.size.toString())
        }
    }

    //Camera intent
    private fun takePhotoIntent() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, setImageUri())
        // intent.putExtra(REQUEST_CODE,CAPTURE_CAMERA)
        intentActivityResultLauncher.launch(intent)

    }

    //////////////////////////////////// save picture in sdcard and get path of the pic
    private fun setImageUri(): Uri? {
        file = File(Environment.getExternalStorageDirectory().toString() + "/DCIM/", "image" + Date().time + ".jpg")
        // Uri imgUri = Uri.fromFile(file);
        photoURI = FileProvider.getUriForFile(this.requireActivity(), "com.khan.fftracker" + ".provider", file!!)
        imgPath = file!!.getAbsolutePath()
        return photoURI
    }
    /// Camera register: taking picture from camera

    private var intentActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        /////////// taking image from camera

        if (result.resultCode == Activity.RESULT_OK) {
            try {
                // if (requestCode == CAPTURE_CAMERA) {
                LogCalls_Debug.d("json image path", "" + imgPath)

                ///refresh gallery
                RefreshGallery(activity).refreshGallery(file)
                val file_s = (file!!.length() / 1024).toString().toInt()
                LogCalls_Debug.d("file Size", file_s.toString())

                LogCalls_Debug.d(Login_New.TAG, "checkSave_Dialogue: " + file!!.length() / 1024 / 1024 + " byte " + file!!.length())
                bitmap = GetImage_Path(activity).handleSamplingAndRotationBitmap(photoURI!!)

                //  viewModel.setFriendImage(bitmap, file!!)

                // File file=new Compress_Image(getActivity()).saveBitmapToFile(pictureFile);
                var compressFile: File? = null
                try {
                    compressFile = Compressor(activity).compressToFile(file)
                    //  //log.d("file Size", String.valueOf(file_s));
                    val fileCamera = Compress_Image(activity).saveBitmapToFile(compressFile)
                    viewModel.setFriendImage(bitmap, fileCamera!!)
                    Log.d(Login_New.TAG, "Compress_Image: 1 " + fileCamera.length() / 1024 / 1024 + " byte  " + fileCamera.length())
                    Log.d(Login_New.TAG, "checkSave_Dialogue: 1 " + compressFile.length() / 1024 / 1024 + "  byte " + compressFile.length())
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                LogCalls_Debug.d("json  ", bitmap?.getWidth().toString() + " bitheight " + bitmap?.getHeight() + " size " + (bitmap?.byteCount)!! / 1024 / 1024)

//                }
            } catch (e: java.lang.Exception) {
                LogCalls_Debug.d(TAG, "onActivityResult: " + e.message)
                e.printStackTrace()
            }

        }
    }


    /////////////////////////////// Dialogue for taking picture from gallery or camera
    private fun showBottomSheetDialog() {

        bottomSheetDialog = BottomSheetDialog(requireActivity(), R.style.AppBottomSheetDialogTheme)
        val bindingSheet = DataBindingUtil.inflate<CameraGalleryBottomsheetBinding>(layoutInflater, R.layout.camera_gallery_bottomsheet, null, false)
        bottomSheetDialog!!.setContentView(bindingSheet.root)

        bindingSheet!!.lifecycleOwner = this
        bindingSheet.viewModel = viewModel
        bottomSheetDialog!!.show()

    }


    ////////////// When on Back button pressed fragment navigate to prevoius fragment
    private val onBackPressedCallback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* Enabled by default */) {
                override fun handleOnBackPressed() {

                    // Handle the back button event
                    LogCalls_Debug.d(
                            LogCalls_Debug.TAG + FriendDetailsTracker::class.java.name,
                            "handleOnBackPressed false"
                    )
                    closeFragment()

                }
            }

    private fun closeFragment() {

        requireActivity().supportFragmentManager.popBackStack()

    }

}


