package com.example.land_parcel.View.Fragment

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.auth0.android.jwt.JWT
import com.example.land_parcel.R
import com.example.land_parcel.Utils.BaseFragment
import com.example.land_parcel.Utils.NetworkUtils
import com.example.land_parcel.Utils.PrefManager
import com.example.land_parcel.databinding.FragmentDashboardBinding
import com.example.land_parcel.databinding.FragmentUserProfileBinding
import com.example.land_parcel.databinding.DialogChoosMideaBinding
import com.example.land_parcel.databinding.DialogLogoutBinding
import com.example.land_parcel.network.NetworkSealed
import com.example.land_parcel.viewmodel.LoginViewmodel
import com.example.land_parcel.viewmodel.LogoutViewmodel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class UserProfileFragment : BaseFragment(), View.OnClickListener {
    private lateinit var bindinguser: FragmentUserProfileBinding
    private val bindings get() = bindinguser
    @Inject
    lateinit var prefManager: PrefManager
    private val viewmodel: LogoutViewmodel by viewModels()
    @Inject
    lateinit var networkUtils: NetworkUtils

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        bindinguser = FragmentUserProfileBinding.inflate(inflater, container, false)
        val view = bindings.root
        getview()
        return view
    }
    private fun getview() {
        bindinguser.surveyorBtn.setOnClickListener(this)
        bindinguser.backArrow.setOnClickListener(this)
        bindinguser.logoutBtn.setOnClickListener(this)
        bindinguser.changPassBtn.setOnClickListener(this)
        val email = prefManager.getUserName()

        val name = email!!.substringBefore("@").replace(".", " ").split(" ")
            .joinToString(" ") { it.capitalize() }

        bindinguser.userName.setText(name)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        })
        setobservers()
    }

    companion object {

    }
    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.surveyor_btn->{
                if(networkUtils.isNetworkConnectionAvailable()){
                    findNavController().navigate(R.id.action_profile_Fragment_to_surveyorFragment)

                }else{
                    showToast(getString(R.string.internet_not_available))
                }
            }
            R.id.back_arrow->{ findNavController().navigateUp()}
            R.id.logout_btn->{
                if(networkUtils.isNetworkConnectionAvailable()){
                    logout(prefManager.getUserId())
                }else{
                   showToast(getString(R.string.internet_not_available))
                }

            }
            R.id.chang_pass_btn->{
                findNavController().navigate(R.id.action_profile_Fragment_to_changepasswordFragment)
            }
        }
    }
    private fun logout(userId: String?) {
        val syncCheckDialogBinding: DialogLogoutBinding =
            DialogLogoutBinding.inflate(layoutInflater)
        val dialog = Dialog(requireActivity())
        dialog.setContentView(syncCheckDialogBinding.root)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()

        syncCheckDialogBinding.btnSave.setOnClickListener {
             logoutApi(userId!!)
            dialog.dismiss()
        }
        syncCheckDialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }
    }
    private fun logoutApi(userId: String) {
        lifecycleScope.launch {
            viewmodel.getLogout(userId)
        }
    }
    fun setobservers(){
        viewmodel.logoutResponse.observe(viewLifecycleOwner){
            when(it){
                is NetworkSealed.Loading->{
                    bindinguser.progressCircular.progressCircular.visibility = View.VISIBLE
                }
                is NetworkSealed.Data->{
                    bindinguser.progressCircular.progressCircular.visibility = View.GONE
                    prefManager.setLogin(false)
                    showToast(getString(R.string.logout_successfully))
                    findNavController().navigate(R.id.action_profile_Fragment_to_loginFragment)

                }
                is NetworkSealed.Error->{
                    bindinguser.progressCircular.progressCircular.visibility = View.GONE
                    showToast(it.message)
                }
            }
        }
    }
}