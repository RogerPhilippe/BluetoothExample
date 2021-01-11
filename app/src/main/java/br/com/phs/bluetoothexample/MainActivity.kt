package br.com.phs.bluetoothexample

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private var mBlueAdapter: BluetoothAdapter? = null

    companion object {
        const val REQUEST_ENABLE_BT = 0
        const val REQUEST_DISCOVER_BT = 1
    }

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //adapter
        mBlueAdapter = BluetoothAdapter.getDefaultAdapter()

        //check if bluetooth is available or not
        if (mBlueAdapter == null){
            statusBluetoothTv.text = "Bluetooth is not available"
        }
        else {
            statusBluetoothTv.text = "Bluetooth is available"
        }

        if (mBlueAdapter?.isEnabled == true){
            bluetoothIv.setImageResource(R.drawable.ic_baseline_bluetooth_24)
        }
        else {
            bluetoothIv.setImageResource(R.drawable.ic_baseline_bluetooth_disabled_24)
        }

        this.setupListeners()

    }

    private fun setupListeners() {

        onBtn.setOnClickListener {
            if (!mBlueAdapter!!.isEnabled) {
                showToast("Turning On Bluetooth...")
                val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(intent, REQUEST_ENABLE_BT)
            } else {
                showToast("Bluetooth is already on")
            }
        }

        offBtn.setOnClickListener {
            if (mBlueAdapter?.isEnabled == true){
                mBlueAdapter?.disable()
                showToast("Turning Bluetooth Off");
                bluetoothIv.setImageResource(R.drawable.ic_baseline_bluetooth_disabled_24)
            }
            else {
                showToast("Bluetooth is already off");
            }
        }

        discoverableBtn.setOnClickListener {
            if (!mBlueAdapter!!.isDiscovering) {
                showToast("Making Your Device Discoverable")
                val intent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
                startActivityForResult(intent, REQUEST_DISCOVER_BT)
            }
        }

        pairedBtn.setOnClickListener {
            if (mBlueAdapter!!.isEnabled) {
                pairedTv.text = "Paired Devices"
                val devices = mBlueAdapter!!.bondedDevices
                for (device in devices) {
                    pairedTv.append("""Device: ${device.name}, $device""".trimIndent())
                }
            } else {
                //bluetooth is off so can't get paired devices
                showToast("Turn on bluetooth to get paired devices")
            }
        }

        selectDeviceBtn.setOnClickListener {  }

    }

    private fun showToast(msg: String) {

        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_ENABLE_BT -> if (resultCode == RESULT_OK) {
                //bluetooth is on
                bluetoothIv.setImageResource(R.drawable.ic_baseline_bluetooth_24)
                showToast("Bluetooth is on")
            } else {
                //user denied to turn bluetooth on
                showToast("could't on bluetooth")
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

}