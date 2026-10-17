package com.bidai.app.utils

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object ValidationUtils {

    fun isValidEmail(email: String): Boolean {
        if (email.isBlank()) return false
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
        return email.matches(emailRegex.toRegex())
    }

    fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }

    fun isValidPhone(phone: String): Boolean {
        val digits = phone.filter { it.isDigit() }
        return digits.length in 10..13
    }

    fun formatCurrency(amount: Double): String {
        val format = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
        format.maximumFractionDigits = 0
        return format.format(amount)
    }

    fun formatTimeRemaining(isoDateString: String?): String {
        if (isoDateString.isNullOrBlank()) return "Active Auction"
        return try {
            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            val targetDate = format.parse(isoDateString) ?: Date()
            val diffMs = targetDate.time - System.currentTimeMillis()

            if (diffMs <= 0) {
                "Auction Ended"
            } else {
                val hours = diffMs / (1000 * 60 * 60)
                val minutes = (diffMs / (1000 * 60)) % 60
                if (hours > 24) {
                    val days = hours / 24
                    "${days}d ${hours % 24}h left"
                } else if (hours > 0) {
                    "${hours}h ${minutes}m left"
                } else {
                    "${minutes}m left"
                }
            }
        } catch (e: Exception) {
            "Ending Soon"
        }
    }
}
