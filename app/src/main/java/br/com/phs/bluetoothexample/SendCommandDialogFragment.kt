package br.com.phs.bluetoothexample

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContentProviderCompat
import androidx.fragment.app.DialogFragment

class SendCommandDialogFragment(private val mParent: MainActivity) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val view: View = requireActivity().layoutInflater.inflate(R.layout.send_command_dialog_layout, null)

        val mSendCommandTv = view.findViewById<EditText>(R.id.sendCommandTv)
        val mSendCommandBtn = view.findViewById<Button>(R.id.sendCommandBtn)

        mSendCommandBtn?.setOnClickListener {
            if (mSendCommandTv.text.toString().isNotEmpty()) {
                mParent.sendCommand(mSendCommandTv.text.toString())
                this.dismiss()
            }
            else
                Toast.makeText(requireContext(), "Digite um comando!", Toast.LENGTH_SHORT).show()
        }

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Enviar Comando")
        builder.setView(view)
        builder.setCancelable(false)

        return builder.create()
    }

}