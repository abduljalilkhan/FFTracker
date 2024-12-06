package com.khan.fftracker.tracker.userMenuSetting.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.khan.fftracker.DashBoard.Dashboard_Constants
import com.khan.fftracker.Prefrences.Prefs_Operation
import com.khan.fftracker.Prefrences.Prefs_OperationKotlin
import com.khan.fftracker.R
import com.khan.fftracker.login_Stuffs.Login_Contstant
import com.khan.fftracker.shoppingBossMVVM.EventLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserMenuVM : ViewModel() {

    //boolean navigation for one time action perform
    private val _navigateFriendSetting = MutableLiveData<EventLiveData<Boolean>>()
    private val _navigateManageAlert = MutableLiveData<EventLiveData<Boolean>>()

   // private var listMenu: MutableList<String> = ArrayList()
   // private var listMenuImg: MutableList<Int> = ArrayList()
    var jk=MutableLiveData<MutableList<Int>>()

    private val _listMenu = MutableLiveData<MutableList<String>>()

    private val _listMenuImg = MutableLiveData<MutableList<Int>>()

    private var customerImage = MutableLiveData("http//:")
    private var userName = MutableLiveData("")
    private var userPhoneNo = MutableLiveData("")

    init {
        getData()


    }

     fun getData() {
        // Launch a coroutine to load the user name
        //viewModelScope.launch(Dispatchers.Main) {
            getCustomerData()
            createMenuList()
      //  }
         }

    //get customer lat,lng and image which is saved in dashboard response
     fun getCustomerData() {
        userName.value = Prefs_OperationKotlin.readString(Login_Contstant.CUSTOMER_NAME, "")

        customerImage.value = Prefs_OperationKotlin.readString(Dashboard_Constants.CUSTOMER_IMAGE, "http//:")
        userPhoneNo.value = Prefs_OperationKotlin.readString(Dashboard_Constants.PHONE_NO, "")

    }

     fun createMenuList() {

        //name list
        _listMenu.value =
                mutableListOf(
                        "Manage Alerts",
                        "Privacy & Friends",
                        "",
                        "Contacts"

                )

        //array of icon
        _listMenuImg.value = ArrayList(
                mutableListOf(
                        R.drawable.bell_icon,
                        R.drawable.lock_blackoutlined,
                        R.drawable.support,
                        R.drawable.support
                )
        )
       // jk.value=listMenuImg

    }

    fun getMenuList(): LiveData<MutableList<String>> {
        return _listMenu
    }
    fun getMenuListImg():LiveData<MutableList<Int>> {
        return _listMenuImg
    }
    fun getCustomerImage(): LiveData<String> {
        return customerImage
    }

    fun getUserName(): LiveData<String> {
        return userName
    }

    fun getPhoneNumber(): LiveData<String> {
        return userPhoneNo
    }

    //observe for AddRemoveFav  response
    //Live data observing in fragment. Purpose not to expose mutable (changeable) data in fragments
    fun getNavigateToFriendSetting(): LiveData<EventLiveData<Boolean>> {
        return _navigateFriendSetting
    }


    //observe for AddRemoveFav  response
    //Live data observing in fragment. Purpose not to expose mutable (changeable) data in fragments
    fun getNavigateToManageAlert(): LiveData<EventLiveData<Boolean>> {
        return _navigateManageAlert
    }

    fun onNavigateToNextFrag(pos: Int) {
        when (pos) {
            0 -> {
                _navigateManageAlert.value = EventLiveData(true)
            }

            1 -> {
                _navigateFriendSetting.value = EventLiveData(true)
            }
        }
    }

}

