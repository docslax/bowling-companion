package ca.josephroque.bowlingcompanion.utils

import android.content.Intent
import android.net.Uri

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Provides methods related to creating and formatting emails
 */
object Email {
    @Suppress("unused")
    private const val TAG = "Email"

    /**
     * Prompts user to send an email with the provided parameters.
     *
     * @param prompt prompt to display to indicate the purpose of the email
     * @param recipient email recipient
     * @param subject subject of the email
     * @param body body of the email
     */
    fun createEmailIntent(prompt: String, recipient: String?, subject: String? = null, body: String? = null): Intent {
        val intent = Intent(Intent.ACTION_SEND)
        intent.data = Uri.parse("mailto:")
        intent.type = "message/rfc822"

        recipient?.let { intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(it)) }
        subject?.let { intent.putExtra(Intent.EXTRA_SUBJECT, it) }
        body?.let { intent.putExtra(Intent.EXTRA_TEXT, it) }

        return Intent.createChooser(intent, prompt)
    }
}
