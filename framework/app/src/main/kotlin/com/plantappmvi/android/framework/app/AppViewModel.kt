package com.plantappmvi.android.framework.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plantappmvi.android.domain.onboarding.data.OnboardingStatusResult
import com.plantappmvi.android.domain.onboarding.usecase.GetOnboardingStatusUseCase
import com.plantappmvi.android.framework.app.navigation.AppRoutes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Decides where the app opens.
 *
 * This is the composition root's answer to the Flutter build's route guard,
 * and it belongs here for the same reason the routes do: it is the one
 * decision that needs to know both features exist. The onboarding feature asks
 * "has this been completed"; only this module turns that into a destination.
 */
@HiltViewModel
internal class AppViewModel @Inject constructor(
    private val getOnboardingStatus: GetOnboardingStatusUseCase,
) : ViewModel() {

    private val internalStartRoute = MutableStateFlow<String?>(null)

    /** Null until the flag has been read. The splash screen holds until then. */
    val startRoute: StateFlow<String?> = internalStartRoute.asStateFlow()

    init {
        viewModelScope.launch {
            internalStartRoute.value = when (getOnboardingStatus()) {
                is OnboardingStatusResult.Completed -> AppRoutes.HOME
                // Fail open toward onboarding. If the flag cannot be read,
                // showing onboarding again is a far milder failure than
                // locking someone out of the app.
                is OnboardingStatusResult.Pending,
                is OnboardingStatusResult.Unavailable,
                -> AppRoutes.INTRO
            }
        }
    }
}
