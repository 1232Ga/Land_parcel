package com.example.land_parcel.View.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.land_parcel.R
import com.example.land_parcel.Utils.BaseFragment
import com.example.land_parcel.Utils.NetworkUtils
import com.example.land_parcel.Utils.PrefManager
import com.example.land_parcel.databinding.FragmentChangePasswordBinding
import com.example.land_parcel.network.NetworkSealed
import com.example.land_parcel.viewmodel.ChangePasswordViewmodel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class ChangePasswordFragment : BaseFragment(), View.OnClickListener {
    private lateinit var bindinguser: FragmentChangePasswordBinding
    private val bindings get() = bindinguser
    @Inject
    lateinit var prefManager: PrefManager
    @Inject
    lateinit var networkUtils: NetworkUtils

    private val viewmodel:ChangePasswordViewmodel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        bindinguser = FragmentChangePasswordBinding.inflate(inflater, container, false)
        val view = bindings.root
        getview()
        return view
    }
    private fun getview() {
        bindinguser.backArrow.setOnClickListener(this)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        })
      bindinguser.changePassBtn.setOnClickListener(this)
      setoberevers()
    }
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.back_arrow->{ findNavController().navigateUp()}
            R.id.change_pass_btn->{
               if(networkUtils.isNetworkConnectionAvailable()){
                   val oldPassword: String = bindings.oldPassword.text.toString().trim { it <= ' ' }
                   val newPassword: String = bindings.newPassword.text.toString().trim { it <= ' ' }
                   val confirmPassword: String = bindings.confPassword.text.toString().trim { it <= ' ' }
                   if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                       Toast.makeText(requireActivity(), "All fields are required", Toast.LENGTH_LONG).show()
                   }else if (newPassword != confirmPassword) {
                       Toast.makeText(requireActivity(), "New password and Re-enter password do not match.", Toast.LENGTH_SHORT).show()
                   }else{
                       viewmodel.encryptData(oldPassword,newPassword)
                       lifecycleScope.launch {
                           viewmodel.ChangePassword(prefManager.getToken()!!,prefManager.getUserId()!!,viewmodel.encodedoldPassword,viewmodel.encodednewPassword)
                       }
                   }
               }else{
                   showToast(getString(R.string.internet_not_available))
               }

            }
        }
    }
    private fun setoberevers(){
        viewmodel.changeResponse.observe(viewLifecycleOwner){
            when(it){
                is NetworkSealed.Loading->{
                    bindinguser.progressCircular.progressCircular.visibility = View.VISIBLE
                }
                is NetworkSealed.Data->{
                    bindinguser.progressCircular.progressCircular.visibility = View.GONE
                    prefManager.setLogin(true)
                    showToast(it.data!!.message)
                    bindinguser.oldPassword.setText("")
                    bindinguser.newPassword.setText("")
                    bindinguser.confPassword.setText("")
                }
                is NetworkSealed.Error->{
                    bindinguser.progressCircular.progressCircular.visibility = View.GONE
                    showToast(it.message)
                }
            }
        }
    }
}