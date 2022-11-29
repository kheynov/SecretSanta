package ru.kheynov.secretsanta.utils

import android.content.Context
import android.content.Intent
import ru.kheynov.secretsanta.presentation.screens.login_screen.LoginActivity

fun navigateToLoginScreen(context: Context) {
    val intent = Intent(context, LoginActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    context.startActivity(intent)
}