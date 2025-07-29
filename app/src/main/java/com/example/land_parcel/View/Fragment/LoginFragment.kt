package com.example.land_parcel.View.Fragment

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.auth0.android.jwt.JWT
import com.example.land_parcel.R

import com.example.land_parcel.Utils.BaseFragment
import com.example.land_parcel.Utils.NetworkUtils
import com.example.land_parcel.Utils.PrefManager
import com.example.land_parcel.databinding.DialogConfirmationBinding
import com.example.land_parcel.databinding.DialogOfflinerBinding
import com.example.land_parcel.databinding.DialogResponseLoginBinding
import com.example.land_parcel.databinding.FragmentLoginBinding
import com.example.land_parcel.network.NetworkSealed
import com.example.land_parcel.viewmodel.LoginViewmodel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : BaseFragment() {
    lateinit var binding: FragmentLoginBinding

    private val viewmodel: LoginViewmodel by viewModels()
    @Inject
    lateinit var networkUtils: NetworkUtils
    @Inject
    lateinit var prefManager: PrefManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {

        binding=FragmentLoginBinding.inflate(inflater)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewmodel.getClientId(requireActivity())

        if(!networkUtils.isNetworkConnectionAvailable()){
            offlineDialog()
        }

        binding.signInBtn.setOnClickListener {
            if(networkUtils.isNetworkConnectionAvailable()){
                if (!validateEmail(binding.username)) {
                    return@setOnClickListener
                }
                else if (!isValidPassword(
                        binding.password.getText().toString(),
                        binding.password,
                        true)) {
                    return@setOnClickListener
                } else {
                    viewmodel.encryptData(binding.username.text.toString(),binding.password.text.toString())

                    CoroutineScope(Dispatchers.IO).launch {
                        viewmodel.getLogin(viewmodel.encodedUsername, viewmodel.encodedPassword)
                    }
                }
            }
            else{
                showToast(getString(R.string.internet_not_available))
            }

        }
        setobservers()
    }

    fun setobservers(){
        viewmodel.loginResponse.observe(viewLifecycleOwner){
            when(it){
                is NetworkSealed.Loading->{
                    binding.progressCircular.progressCircular.visibility = View.VISIBLE
                 }
                 is NetworkSealed.Data->{
                     binding.progressCircular.progressCircular.visibility = View.GONE
                     val token: String = it.data?.token ?: ""
                     val jwt = JWT(token)
                     val orgid: String = jwt.getClaim("OrganizationId").asString().toString()
                     val userId: String = jwt.getClaim("UserId").asString().toString()
                     prefManager.setUserId(userId)
                     prefManager.setUserName(binding.username.text.toString())
                     prefManager.setLogin(true)
                     prefManager.setToken(token)
                     showToast(getString(R.string.login_successfully))
                     findNavController().navigate(R.id.action_loginFragment_to_dashboard_Fragment)
                 }
                is NetworkSealed.Error->{
                    binding.progressCircular.progressCircular.visibility = View.GONE
                    updateDialog(it.message)
                }
            }
        }
    }
    private fun offlineDialog() {
        val offlineBinding: DialogOfflinerBinding =
            DialogOfflinerBinding.inflate(layoutInflater)
        val dialog = Dialog(requireActivity())
        dialog.setContentView(offlineBinding.root)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()

        offlineBinding.btnSave.setOnClickListener {
            dialog.dismiss()
        }

    }



}