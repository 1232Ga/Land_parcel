package com.example.land_parcel.model.Pnil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class MyViewModel @Inject constructor(
    private val pnilRepository: PnilRepository
) : ViewModel() {

    fun savePnilNo(pnilNo: String?) {
        viewModelScope.launch {
            pnilRepository.savePnil(pnilNo)
        }
    }

    val pnilFlow = pnilRepository.getPnil()
}
