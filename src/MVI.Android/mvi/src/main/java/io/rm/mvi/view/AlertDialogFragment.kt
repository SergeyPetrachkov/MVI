package io.rm.mvi.view

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import io.rm.viper.R

class AlertDialogFragment : DialogFragment() {

    companion object {
        private const val ARGUMENT_TITLE: String = "argumentTitle"
        private const val ARGUMENT_MESSAGE: String = "argumentMessage"

        fun newInstance(title: String, message: String?): AlertDialogFragment {
            val messageDialogFragment = AlertDialogFragment()
            val arguments = Bundle()
            arguments.putString(ARGUMENT_TITLE, title)
            arguments.putString(ARGUMENT_MESSAGE, message)
            messageDialogFragment.arguments = arguments
            return messageDialogFragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        this.arguments?.let {
            val title = it.getString(ARGUMENT_TITLE)
            val message = it.getString(ARGUMENT_MESSAGE)

            return AlertDialog.Builder(this.requireContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(
                    this.getString(R.string.viper_alert_dialog_positive_button_Label)
                ) { _, _ -> }
                .create()
        } ?: throw IllegalStateException()
    }
}