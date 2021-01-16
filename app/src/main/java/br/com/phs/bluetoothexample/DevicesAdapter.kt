package br.com.phs.bluetoothexample

import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.devices_dialog_rv_item_layout.view.*

class DevicesAdapter(
        private val mItems: MutableList<BluetoothDevice>,
        private val mParent: DevicesDialogFragment
        ) : RecyclerView.Adapter<DevicesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(mItems[position]) {
            holder.bindDeviceNameTv(this.name)
            holder.bindDeviceUIDTv(this.address)

            holder.itemView.setOnClickListener { mParent.selectDevice(this) }

        }
    }

    override fun getItemCount() = mItems.size

    class ViewHolder(parent: ViewGroup): RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.devices_dialog_rv_item_layout, parent, false)
    ) {

        fun bindDeviceNameTv(deviceName: String) = with(itemView) { deviceNameTv.text = deviceName }
        fun bindDeviceUIDTv(deviceUID: String) = with(itemView) { deviceUIDTv.text = deviceUID }

    }

}