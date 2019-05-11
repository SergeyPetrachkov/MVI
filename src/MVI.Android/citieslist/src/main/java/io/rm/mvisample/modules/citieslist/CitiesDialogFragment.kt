package io.rm.mvisample.modules.citieslist

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

interface CitiesDialogListener {
    fun onPositiveAction(cityName: String)
}

class CitiesDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view =
            LayoutInflater.from(this.requireContext()).inflate(R.layout.citieslist_dialog, null)
        val input = view.findViewById<EditText>(R.id.input)

        return AlertDialog.Builder(this.requireContext())
            .setTitle(R.string.citieslist_title_add_city_dialog)
            .setView(view)
            .setPositiveButton(this.resources.getString(R.string.citieslist_dialog_ok)) { _, _ ->
                (this.requireActivity() as CitiesDialogListener).onPositiveAction(input.text.toString())
            }
            .setNegativeButton(this.resources.getString(R.string.citieslist_dialog_cancel), null)
            .create()
    }
}