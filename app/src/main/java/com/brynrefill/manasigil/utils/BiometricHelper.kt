package com.brynrefill.manasigil.utils

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

/**
 * handles biometric authentication
 */
class BiometricHelper(
    private val activity: FragmentActivity
) {
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    // BIOMETRIC AUTH functions
    fun setup(
        onAuthenticationSucceeded: () -> Unit,
        onAuthenticationError: (String) -> Unit,
        onAuthenticationFailed: () -> Unit
    ) {
        val executor = ContextCompat.getMainExecutor(activity)

        biometricPrompt = BiometricPrompt(activity, executor,
            object : BiometricPrompt.AuthenticationCallback() {

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onAuthenticationSucceeded()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    onAuthenticationError(errString.toString())
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    onAuthenticationFailed()
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Authenticate to access your Manasigil credentials!")
            .setSubtitle("Please unlock to proceed.") // using fingerprint (biometric) or device credentials auth

            // for API < 30 i.e. Build.VERSION.SDK_INT >= Build.VERSION_CODES.R it's not possible to "OR-ing" two different authenticators
            /*
            .setAllowedAuthenticators(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or
                BiometricManager.Authenticators.DEVICE_CREDENTIAL
            )
            */

            // .setNegativeButtonText(...) not compatible with DEVICE_CREDENTIAL. The system provides its own UI for device credentials -> the system will crash!
            // TODO: check if with a device, that not support BIOMETRIC_STRONG, will crash with this istruction and manage all the possible cases
            .setNegativeButtonText("CANCEL")
            .build()
    }

    fun showPrompt() {
        val biometricManager = BiometricManager.from(activity)
        // val authenticators = BiometricManager.Authenticators.BIOMETRIC_STRONG or
        //                      BiometricManager.Authenticators.DEVICE_CREDENTIAL

        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
        // when (biometricManager.canAuthenticate(authenticators)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                // biometric authentication available
                biometricPrompt.authenticate(promptInfo)
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                // no biometric hardware, use device credentials (PIN/Pattern/Password)

                // not allow access without biometric
                // isBiometricAuthenticated.value = true
                biometricPrompt.authenticate(promptInfo)
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                // biometric hardware unavailable, use device credentials
                biometricPrompt.authenticate(promptInfo)
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                // no biometrics enrolled, ask to set it up
                biometricPrompt.authenticate(promptInfo)
            }

            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED,
            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED,
            BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> {
                // fallback to device credentials // ?
                biometricPrompt.authenticate(promptInfo)
            }
        }
    }
}
