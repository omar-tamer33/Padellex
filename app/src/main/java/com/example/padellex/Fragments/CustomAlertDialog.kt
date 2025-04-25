package com.example.padellex.Fragments

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.padellex.R

class CustomAlertDialog(val message : String , val onConfirmClick : () -> Unit) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = requireActivity().layoutInflater.inflate(R.layout.dialog_warning, null)
        val confirmBtn = view.findViewById<Button>(R.id.confirmBtn)
        val cancelBtn = view.findViewById<Button>(R.id.cancelBtn)
        val text = view.findViewById<TextView>(R.id.warningTextTv)
        val dialog = AlertDialog.Builder(requireContext()).setView(view).create()
        confirmBtn.setOnClickListener {
            onConfirmClick()
        }
        cancelBtn.setOnClickListener {
            dialog.dismiss()
        }

        text.text = message

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return dialog
    }
}