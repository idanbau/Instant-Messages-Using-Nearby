package com.idanrayan.instantmessagesusingnearby.ui.initialScreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.idanrayan.instantmessagesusingnearby.MainApplication
import com.idanrayan.instantmessagesusingnearby.R
import com.idanrayan.instantmessagesusingnearby.domain.models.Screen
import com.idanrayan.instantmessagesusingnearby.domain.services.DataStoreManager
import com.idanrayan.instantmessagesusingnearby.domain.utils.validate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject


@HiltViewModel
class InitialScreenViewModel @Inject constructor(
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    private val _state = MutableLiveData(InitialScreenState())
    val state: LiveData<InitialScreenState> = _state

    fun onNameChange(userName: String) {
        _state.value = _state.value?.copy(
            name = userName
        )
        if (!validated())
            validation()
    }

    operator fun invoke(navController: NavController) {
        validation()
        if (validated()) {
            viewModelScope.launch {
                dataStoreManager.setOnBoard(_state.value!!.name, UUID.randomUUID().toString())
                navController.navigate(Screen.Main.route) {
                    popUpTo(0)
                }
            }
        }
    }

    private fun validation() {
        _state.value = _state.value!!.copy(
            nameError = _state.value!!.name.validate(MainApplication.res.getString(R.string.username))
                .required().max(MAXIMUM_NAME_LENGTH)
                .build()
        )
    }

    private fun validated(): Boolean = _state.value!!.nameError == null
}