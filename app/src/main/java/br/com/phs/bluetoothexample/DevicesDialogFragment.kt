package br.com.phs.bluetoothexample

import android.app.AlertDialog
import android.app.Dialog
import android.bluetooth.BluetoothDevice
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView

class DevicesDialogFragment(
        private val mItems: MutableList<BluetoothDevice>,
        private val func: (device: BluetoothDevice)-> Unit
) : DialogFragment() {

    private lateinit var mDevicesRv: RecyclerView

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val view: View = requireActivity().layoutInflater.inflate(R.layout.devices_dialog_layout, null)
        mDevicesRv = view.findViewById(R.id.devicesRv)
        val adapter = DevicesAdapter(mItems, this)
        mDevicesRv.adapter = adapter
        mDevicesRv.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Selecione um dispositivo")
        builder.setView(view)
        builder.setCancelable(false)

        return builder.create()
    }

    fun selectDevice(device: BluetoothDevice) {

        func(device)
        this.dismiss()
    }

}