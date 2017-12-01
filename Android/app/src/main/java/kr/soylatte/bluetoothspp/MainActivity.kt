package kr.soylatte.bluetoothspp

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import app.akexorcist.bluetotohspp.library.BluetoothSPP
import app.akexorcist.bluetotohspp.library.BluetoothState
import app.akexorcist.bluetotohspp.library.BluetoothSPP.BluetoothConnectionListener
import app.akexorcist.bluetotohspp.library.BluetoothSPP.OnDataReceivedListener
import kotlinx.android.synthetic.main.activity_main.*
import app.akexorcist.bluetotohspp.library.BluetoothSPP.BluetoothStateListener
import app.akexorcist.bluetotohspp.library.BluetoothSPP.AutoConnectionListener


class MainActivity : AppCompatActivity() {

    val bt = BluetoothSPP(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(!bt.isBluetoothAvailable()){
            Toast.makeText(this@MainActivity, "블루투스를 켜주세요", Toast.LENGTH_SHORT).show()
        }

        bt.setBluetoothStateListener { state ->
            if (state == BluetoothState.STATE_CONNECTED)
                Log.i("Check", "State : Connected")
            else if (state == BluetoothState.STATE_CONNECTING)
                Log.i("Check", "State : Connecting")
            else if (state == BluetoothState.STATE_LISTEN)
                Log.i("Check", "State : Listen")
            else if (state == BluetoothState.STATE_NONE)
                Log.i("Check", "State : None")
        }

        bt.setOnDataReceivedListener (object : BluetoothSPP.OnDataReceivedListener{
            override fun onDataReceived(data: ByteArray?, message: String?) {
                Log.e("message", message)
                var msg : Int = Integer.parseInt(message)
                val tmp = view.getText().toString()
                var mes : Char = msg.toChar()
                view.setText(tmp+mes)
            }
        })

        bt.setBluetoothConnectionListener(object : BluetoothConnectionListener {
            override fun onDeviceConnected(name: String, address: String) {
                Log.i("Check", "Device Connected!!")
                Toast.makeText(this@MainActivity, "Device Connected!!", Toast.LENGTH_SHORT).show()
            }

            override fun onDeviceDisconnected() {
                Log.i("Check", "Device Disconnected!!")
                Toast.makeText(this@MainActivity, "Device DisConnected!!", Toast.LENGTH_SHORT).show()
            }

            override fun onDeviceConnectionFailed() {
                Log.i("Check", "Unable to Connected!!")
                Toast.makeText(this@MainActivity, "Unable to Connected!!", Toast.LENGTH_SHORT).show()
            }
        })

        bt.setAutoConnectionListener(object : AutoConnectionListener {
            override fun onNewConnection(name: String, address: String) {
                Log.i("Check", "New Connection - $name - $address")
                Toast.makeText(this@MainActivity, "New Connection - $name - $address", Toast.LENGTH_SHORT).show()
                bt.send("Bluetooth Connected", false)
            }

            override fun onAutoConnectionStarted() {
                Log.i("Check", "Auto menu_connection started")
            }
        })

        send.setOnClickListener {
            val data = message.getText().toString()
            bt.send(data, false)
        }
    }

    override fun onPause() {
        bt.stopService()
        super.onPause()
    }

    override fun onStart() {
        super.onStart()
        bt.setupService()
        bt.startService(BluetoothState.DEVICE_OTHER)
        bt.autoConnect("HC-06")
        Log.e("STATE", "onStart")
    }
}
