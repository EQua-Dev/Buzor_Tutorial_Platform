package com.awesomenessstudios.schoolprojects.criticalthinkingappforkids.holder


import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * A View model with hiltViewModel annotation that is used to access this view model everywhere needed
 */
@HiltViewModel
class HolderViewModel @Inject constructor(
) : ViewModel() {


}