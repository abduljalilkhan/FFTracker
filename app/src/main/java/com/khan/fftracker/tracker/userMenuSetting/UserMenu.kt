package com.khan.fftracker.tracker.userMenuSetting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.khan.fftracker.logCalls.LogCalls_Debug
import com.khan.fftracker.Navigation_Drawer.Drawer
import com.khan.fftracker.R
import com.khan.fftracker.RecylerViewClicked.RecyclerViewItemListener
import com.khan.fftracker.databinding.TrackerUserMenuBinding
import com.khan.fftracker.tracker.alertSetting.ManageAlerts
import com.khan.fftracker.tracker.userMenuSetting.adaptor.UserMenuAdaptor
import com.khan.fftracker.tracker.userMenuSetting.friendSettings.FriendListSettings
import com.khan.fftracker.tracker.userMenuSetting.viewModel.UserMenuVM

class UserMenu : Fragment(), RecyclerViewItemListener {


    private lateinit var adaptor: UserMenuAdaptor
    private val TAG = UserMenu::class.simpleName

    var binding: TrackerUserMenuBinding? = null
    var mView: View? = null

    lateinit var viewModel: UserMenuVM


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.tracker_user_menu, container, false)

        mView = binding!!.root

        setupViewModel()



        requireActivity().onBackPressedDispatcher.addCallback(
            requireActivity(),
            onBackPressedCallback
        )


        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        initRecyclerView()

    }

    private fun initRecyclerView() {
        LogCalls_Debug.d(TAG, "initRecyclerView")

        adaptor = UserMenuAdaptor(requireActivity(), viewModel.getMenuList(),viewModel.getMenuListImg(), this)
        binding!!.rvMenuPlaces.adapter = adaptor
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[UserMenuVM::class.java]
        ///////////////////////////////////////////////////

        binding!!.lifecycleOwner = this
        binding!!.viewModel = viewModel
    }

    private fun setupObservers() {

        viewModel.getNavigateToFriendSetting().observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { // Only proceed if the event has never been handled
                (activity as Drawer?)!!.getFragment(FriendListSettings(), -1)
            }
        }

        viewModel.getNavigateToManageAlert().observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { // Only proceed if the event has never been handled
                (activity as Drawer?)!!.getFragment(ManageAlerts(), -1)
            }
        }
    }


    override fun onItemClick(get: HashMap<String, Any>, pos: Int) {}
    override fun onItemClickObject(id: Int, any: Any, pos: Int) {

        //  viewModel.onNavigateAddGeofence("0")
        viewModel.onNavigateToNextFrag(pos)

    }


    ////////////// When on Back button pressed fragment navigate to prevoius fragment
    private val onBackPressedCallback: OnBackPressedCallback =
        object : OnBackPressedCallback(true /* Enabled by default */) {
            override fun handleOnBackPressed() {

                // Handle the back button event
                LogCalls_Debug.d(
                    LogCalls_Debug.TAG + UserMenu::class.java.name,
                    "handleOnBackPressed false"
                )
                closeFragment()

            }
        }

    private fun closeFragment() {

        requireActivity().supportFragmentManager.popBackStack()

    }
}


