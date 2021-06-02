package com.lunion.lunionapp.viewmodel

import android.os.Build
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.lunion.lunionapp.data.LunionRepository
import com.lunion.lunionapp.model.StatusProses
import com.lunion.lunionapp.model.UserModel

class DetectionViewModel(private val repository: LunionRepository): ViewModel() {

    val dataUser: LiveData<UserModel>
    val saveDataTreatment: LiveData<StatusProses>

    init {
        this.dataUser = repository.dataUser
        this.saveDataTreatment = repository.saveDataTreatment
    }

    fun checkEmailPatient(email: String) = repository.checkEmailPatien(email)

    fun getUserInfo() = repository.getUserInfo()

    fun saveDataTreatment(diagnose: String, note: String, user: UserModel, dataDoctor: UserModel){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            repository.saveDataTreatment(diagnose, note, user, dataDoctor)
        }
    }

}