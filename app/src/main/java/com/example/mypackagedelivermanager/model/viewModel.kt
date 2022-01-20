package com.example.mypackagedelivermanager.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mypackagedelivermanager.Entities.ParcelTable
import com.example.mypackagedelivermanager.Entities.Repository

class viewModel : ViewModel() {
    private var parcels = MutableLiveData<List<ParcelTable>>(ArrayList<ParcelTable>())

    init {
        Repository.getAllPackages(parcels).toString()
    }
}