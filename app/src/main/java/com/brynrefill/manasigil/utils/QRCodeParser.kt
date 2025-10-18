package com.brynrefill.manasigil.utils

import com.brynrefill.manasigil.data.model.QRCodeData
import com.brynrefill.manasigil.data.model.QRCodeType
import androidx.core.net.toUri

/**
 * parses QR code content into structured data
 */
class QRCodeParser {
    /**
     * parse QR code content into structured credential data
     */
    fun parse(rawValue: String): QRCodeData {
        return when {
            // custom Manasigil format: manasigil://credential?label=X&user=Y&pass=Z&notes=N
            rawValue.startsWith("manasigil://credential") -> {
                parseManasigilFormat(rawValue)
            }

            // Google Authenticator format: otpauth://totp/ServiceName:user@email.com?secret=...&issuer=...
            rawValue.startsWith("otpauth://totp/") -> {
                parseTOTPFormat(rawValue)
            }

            // regular URL
            rawValue.startsWith("http://") || rawValue.startsWith("https://") -> {
                parseURLFormat(rawValue)
            }

            // plain text
            else -> {
                QRCodeData(
                    type = QRCodeType.TEXT,
                    label = "QR Code Data",
                    notes = rawValue
                )
            }
        }
    }

    /**
     * parse custom Manasigil format
     */
    private fun parseManasigilFormat(rawValue: String): QRCodeData {
        val uri = rawValue.toUri() // Uri.parse(...)
        return QRCodeData(
            type = QRCodeType.CREDENTIAL,
            label = uri.getQueryParameter("label") ?: "",
            username = uri.getQueryParameter("user") ?: "",
            password = uri.getQueryParameter("pass") ?: "",
            notes = uri.getQueryParameter("notes") ?: ""
        )
    }

    /**
     * parse Google Authenticator TOTP format
     */
    private fun parseTOTPFormat(rawValue: String): QRCodeData {
        val uri = rawValue.toUri() // Uri.parse(...)
        val path = uri.path?.removePrefix("/") ?: ""
        val parts = path.split(":")
        val serviceName = parts.getOrNull(0) ?: "Unknown service"
        val email = parts.getOrNull(1) ?: uri.getQueryParameter("issuer") ?: ""
        val secret = uri.getQueryParameter("secret") ?: ""

        return QRCodeData(
            type = QRCodeType.TOTP,
            label = serviceName,
            username = email,
            password = secret,
            notes = "TOTP Secret Key"
        )
    }

    /**
     * parse URL format
     */
    private fun parseURLFormat(rawValue: String): QRCodeData {
        val domain = rawValue.toUri().host ?: "Unknown website" // Uri.parse(rawValue).host

        return QRCodeData(
            type = QRCodeType.URL,
            label = domain,
            url = rawValue,
            notes = "Imported from QR code: $rawValue"
        )
    }
}