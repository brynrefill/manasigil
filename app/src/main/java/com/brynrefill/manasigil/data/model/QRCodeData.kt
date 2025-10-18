package com.brynrefill.manasigil.data.model

/**
 * parsed data from QR code.
 * Supports different formats: URL, plain text, TOTP
 */
data class QRCodeData(
    val type: QRCodeType,
    val label: String = "",
    val username: String = "",
    val password: String = "",
    val url: String = "",
    val notes: String = ""
)

enum class QRCodeType {
    CREDENTIAL, // custom format: manasigil://credential?label=...&user=...&pass=...
    TOTP,       // Google Authenticator format: otpauth://totp/...
    URL,        // regular URL
    TEXT        // plain text
}
