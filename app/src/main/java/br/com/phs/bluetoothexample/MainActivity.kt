package br.com.phs.bluetoothexample

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.InputStream
import java.io.OutputStream


class MainActivity : AppCompatActivity() {

    private var mBlueAdapter: BluetoothAdapter? = null
    private val mBluetoothDeviceList: MutableList<BluetoothDevice> = mutableListOf()
    private var mBluetoothDevice: BluetoothDevice? = null
    private var mOutputStream: OutputStream? = null
    private var mInputStream: InputStream? = null
    private var mSocket: BluetoothSocket? = null

    companion object {
        const val REQUEST_ENABLE_BT = 0
        const val REQUEST_DISCOVER_BT = 1
        const val BUFFER_SIZE: Int = 1024
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
            mBluetoothDeviceList.addAll(mBlueAdapter!!.bondedDevices)
        } else {

            bluetoothIv.setImageResource(R.drawable.ic_baseline_bluetooth_disabled_24)
        }

        this.setupListeners()

    }

    @SuppressLint("SetTextI18n")
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
                mBluetoothDevice = null
                mOutputStream = null
                mInputStream = null
                if (mSocket?.isConnected == true)
                    mSocket?.close()
                appendInConsole("", clearConsole = true)
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
                appendInConsole("Paired Devices")
                val devices = mBlueAdapter!!.bondedDevices
                for (device in devices) {
                    appendInConsole("Device: ${device.name}, $device".trimIndent())
                }
            } else {
                //bluetooth is off so can't get paired devices
                showToast("Turn on bluetooth to get paired devices")
            }
        }

        selectDeviceBtn.setOnClickListener {

            if (mBlueAdapter?.isEnabled == true) {

                DevicesDialogFragment(mBluetoothDeviceList)
                {
                    val uuids = it.uuids
                    mSocket = it.createRfcommSocketToServiceRecord(uuids[0].uuid)
                    if (mSocket?.isConnected == false) {

                        Thread {
                            var attempts = 3

                            do {

                                appendInConsole("Tentando conectar com ${it.name}")
                                appendInConsole("Tentativa n° $attempts")

                                try {
                                    mBluetoothDevice = it
                                    mSocket?.connect()
                                    mOutputStream = mSocket?.outputStream
                                    mInputStream = mSocket?.inputStream
                                    attempts = 0
                                } catch (ex: Exception) {
                                    attempts--
                                    appendInConsole("Erro ao tentar conectar. ${ex.message}")
                                    Thread.sleep(2000)
                                }
                            } while (attempts > 0)

                            val msg = if (mSocket?.isConnected == true)
                                "Conectado com: ${it.name}"
                            else
                                "Erro ao tentar conectar com: ${it.name}"

                            appendInConsole(msg)

                        }.start()

                    }
                }.show(supportFragmentManager, "select_device_dialog")
            }
        }

        disconectDeviceBtn.setOnClickListener {

            val deviceName = mBluetoothDevice?.name

            if (mSocket?.isConnected == true) {
                mSocket?.close()
                mBluetoothDevice = null
                mOutputStream = null
                mInputStream = null
                appendInConsole("Desconectado de $deviceName", clearConsole = true)
            }
        }

        sendMsgBtn.setOnClickListener {

            if (validDevice())
                SendCommandDialogFragment(this).show(supportFragmentManager, "send_command_dialog")
        }

    }

    fun sendCommand(command: String) {

        hideKeyboard(this, View(this))

        mOutputStream?.write(command.toByteArray())
        mOutputStream?.flush()
        read()
    }

    private fun read() {

        if (mInputStream == null)
            return

        Thread {

            var inputStreamAvailable = false
            var attempt = 3

            do {

                Thread.sleep(1000)

                if (mInputStream!!.available() > 0) {
                    inputStreamAvailable = true
                } else {
                    attempt--
                }

            } while (!inputStreamAvailable && attempt > 0)

            val buffer = ByteArray(BUFFER_SIZE)
            val readMessage: String

            try {

                val bytesRead = mInputStream?.read(buffer)
                readMessage = String(buffer)
                appendInConsole(readMessage.substring(0, bytesRead!!).replace("\r", "").replace("\n", ""))


            } catch (ex: Exception) {
                appendInConsole(ex.message ?: "UNKNOWN ERROR.")
            }

        }.start()

    }

    private fun hideKeyboard(context: Context, view: View) {
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun validDevice() = mBluetoothDevice != null && mOutputStream != null && mInputStream != null && mBlueAdapter?.isEnabled == true

    private fun showToast(msg: String) {

        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    @SuppressLint("SetTextI18n")
    private fun appendInConsole(msg: String, clearConsole: Boolean = false) {

        runOnUiThread {

            if (clearConsole)
                console.setText("")

            if (console.text.isEmpty())
                console.setText(msg)
            else
                console.setText("$msg\n${console.text}")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_ENABLE_BT -> if (resultCode == RESULT_OK) {
                //bluetooth is on
                bluetoothIv.setImageResource(R.drawable.ic_baseline_bluetooth_24)
                mBluetoothDeviceList.clear()
                mBluetoothDeviceList.addAll(mBlueAdapter!!.bondedDevices)
                showToast("Bluetooth is on")
            } else {
                //user denied to turn bluetooth on
                showToast("could't on bluetooth")
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

}