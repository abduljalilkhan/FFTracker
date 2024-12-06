package com.khan.fftracker.tracker.addFriendContactList

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.khan.fftracker.logCalls.LogCalls_Debug
import com.khan.fftracker.Network_Volley.IsAdmin
import com.khan.fftracker.Permission_Granted.PermissionFragmentKotlin
import com.khan.fftracker.Permission_Granted.PermissionListener
import com.khan.fftracker.R
import com.khan.fftracker.RecylerViewClicked.RecyclerViewItemListener
import com.khan.fftracker.autoverse_mvvm.Network_Stuff.ApiRetrofit
import com.khan.fftracker.autoverse_mvvm.networkApi.ResultApi
import com.khan.fftracker.commanStuff.GetContactList
import com.khan.fftracker.commanStuff.ShareDataIntent
import com.khan.fftracker.commanStuff.getPackageInfoCompat
import com.khan.fftracker.commonDataClasses.ContactList
import com.khan.fftracker.databinding.AddfriendContactlistBinding
import com.khan.fftracker.tracker.addFriendContactList.adaptor.AddFriendContactAdaptor
import com.khan.fftracker.tracker.addFriendContactList.viewModel.AddFriendContactVM
import com.khan.fftracker.tracker.aApiTracker.TrackerApi
import com.khan.fftracker.tracker.aApiTracker.TrackerRepo


class AddFriendContact: Fragment(),
        PermissionListener,
        RecyclerViewItemListener {
    private val TAG = AddFriendContact::class.simpleName

    var binding: AddfriendContactlistBinding? = null
    var mView: View? = null

    var adaptor: AddFriendContactAdaptor? = null

    lateinit var viewModel: AddFriendContactVM

    //run time permission fragment
    val TAG_PERMISSIONS_FRAGMENT = "permissions"
    private lateinit var permissionsFragment: PermissionFragmentKotlin


    /// IsAdmin: Class for using check whether customer or admin logged in
    var isAdmin: IsAdmin? = null


    ///Google map
    var mGoogleMap: GoogleMap? = null
    var supportMapFragment: SupportMapFragment? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.addfriend_contactlist, container, false)

        mView = binding!!.root

        setupViewModel()
        /// Initialize class
        initializeAdminClass()

        setPermission()

        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), onBackPressedCallback)

        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        launchPermission()

        ///check apps installed
        checkAppInstalled()

    }

    private fun checkAppInstalled() {

        //Extension function check to whether app is installed or not
        val appInfoWhatsapp=requireActivity().packageManager.getPackageInfoCompat("com.whatsapp")
        val appInfoInstagram=requireActivity().packageManager.getPackageInfoCompat("com.instagram.android")

        viewModel.setIsWhatsAppInstalled(appInfoWhatsapp)
        viewModel.setIsInstagramInstalled(appInfoInstagram)

    }

    private fun launchPermission() {
        val handler = Handler(Looper.getMainLooper())

        handler.postDelayed({

            viewModel.setContactPermissionGranted()
        }, 100)
    }



    private fun setupViewModel() {

        val apiInterface = ApiRetrofit.getRetrofitInstance().create(TrackerApi::class.java)
        // View Model with factory
        val factory = AddFriendContactVM.VMFactory(TrackerRepo(apiInterface))

        viewModel = ViewModelProvider(this, factory)[AddFriendContactVM::class.java]
        ///////////////////////////////////////////////////

        binding!!.lifecycleOwner = this
        binding!!.viewModel = viewModel
    }

    private fun setupObservers() {

        viewModel.getNavigateCancel().observe(viewLifecycleOwner){
            it.getContentIfNotHandled()?.let { // Only proceed if the event has never been handled
                closeFragment()
            }
        }
        viewModel.getContactPermissionGranted().observe(viewLifecycleOwner){
            it.getContentIfNotHandled()?.let { // Only proceed if the event has never been handled
                permissionsFragment.contactPermissionGranted()
            }
        }
        viewModel.getShareLink().observe(viewLifecycleOwner){
            it.getContentIfNotHandled()?.let { // Only proceed if the event has never been handled
                ShareDataIntent(requireActivity()).shareDataNew("Invite Link Tracker", viewModel.getUserInviteUrl().value)
            }
        }
        viewModel.getCopyLink().observe(viewLifecycleOwner){
            it.getContentIfNotHandled()?.let { // Only proceed if the event has never been handled
                ShareDataIntent(requireActivity()).copyText(viewModel.getInviteUrl().value)

            }
        }

        viewModel.getWhatsAppShare().observe(viewLifecycleOwner){
            it.getContentIfNotHandled()?.let { // Only proceed if the event has never been handled
                ShareDataIntent(requireActivity()).sendWhatsAppMsg(viewModel.getUserInviteUrl().value,viewModel.getPackageName().value)
            }
        }
//observe merchant api transformation data
        viewModel.getAdaptorFilter().observe(viewLifecycleOwner) {
            LogCalls_Debug.d(TAG, "success observer $it")
            if (adaptor!=null) {
                adaptor!!.filter(it)
            }
        }
        //Observe data changes when api called
        viewModel.getInviteLinkResponse().observe(viewLifecycleOwner) {
            when (it.statusApi) {
                ResultApi.StatusApi.SUCCESS -> {
                    hideProgressBar()
                    viewModel.setInviteUrl(it.data!!.InviteURL)

                    ShareDataIntent(requireActivity()).sendTextMessage(viewModel.getPhoneNo().value, viewModel.getInviteUrl().value)
                }

                ResultApi.StatusApi.ERROR -> {
                    Toast.makeText(activity, it.msg, Toast.LENGTH_SHORT).show()
                    LogCalls_Debug.d(TAG, "ERROR")
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

    fun setRecyclerViewData() {
        adaptor = AddFriendContactAdaptor(requireActivity(), viewModel.getContactList(), this)
        binding!!.rvContact.adapter = adaptor
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
            "contact" -> {
                viewModel.setRecyclerViewVisibility()
                val contactList = GetContactList(requireActivity()).getContactList()
                viewModel.setContactList(contactList)
                setRecyclerViewData()
            }
        }
    }



    override fun onItemClick(get: HashMap<String, Any>, pos: Int) {}
    override fun onItemClickObject(id: Int, any: Any, pos: Int) {
        if (any is ContactList) {
            viewModel.onInviteLinkApi(any.phoneNumber,any.name)
        }
    }

    ////////////// When on Back button pressed fragment navigate to prevoius fragment
    private val onBackPressedCallback: OnBackPressedCallback = object : OnBackPressedCallback(true /* Enabled by default */) {
        override fun handleOnBackPressed() {
            // Handle the back button event
            LogCalls_Debug.d(TAG, "handleOnBackPressed false")
            closeFragment()

        }
    }

    private fun closeFragment() {

        requireActivity().supportFragmentManager.popBackStack()

    }
}