package com.yatapone.nfcreader

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }

    private var nfcAdapter: NfcAdapter? = null
    private var pendingIntent: PendingIntent? = null
    private var intentFilters: Array<IntentFilter>? = null
    private var techLists: Array<Array<String>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "onCreate: ")

        val intent = Intent(this, javaClass)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        intentFilters = arrayOf(IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED))

        techLists = arrayOf(
//            arrayOf(android.nfc.tech.IsoDep::class.java.name), // 免許証
            arrayOf(android.nfc.tech.NfcA::class.java.name), // Mifare、taspo
            arrayOf(android.nfc.tech.NfcB::class.java.name), // 免許証、マイナンバーカード
            arrayOf(android.nfc.tech.NfcF::class.java.name), // 交通系(SUICA, PASMO, ICOCA etc.), 電子マネー(Edy, nanaco, WAON), Mobile FeliCa
//            arrayOf(android.nfc.tech.NfcV::class.java.name),
//            arrayOf(android.nfc.tech.Ndef::class.java.name),
//            arrayOf(android.nfc.tech.NdefFormatable::class.java.name),
//            arrayOf(android.nfc.tech.MifareClassic::class.java.name),
//            arrayOf(android.nfc.tech.MifareUltralight::class.java.name)
        )

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Log.d(TAG, "onNewIntent: intent.action=${intent.action}")
        if (intent.action == NfcAdapter.ACTION_TECH_DISCOVERED) {
            val tag: Tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG) ?: return
            Log.d(TAG, "onNewIntent: tag=$tag")

            val tagIdByte: ByteArray = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID) ?: return
            val tagId = ArrayList<String>()
            tagIdByte.forEach { tagId.add(String.format("%02X", it)) }
            val tagIdJoined: String = tagId.joinToString("")
            Log.d(TAG, "onNewIntent: tagId=$tagId, tagIdJoined=$tagIdJoined")

            tag_text.text = tagId.joinToString(":")

        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: ")
        // このactivityがforegroundの時のみ、他アプリより優先してIntentを取得
        nfcAdapter?.enableForegroundDispatch(this, pendingIntent, intentFilters, techLists)
    }

    override fun onPause() {
        Log.d(TAG, "onPause: ")
        nfcAdapter?.disableForegroundDispatch(this)
        super.onPause()
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy: ")
        super.onDestroy()
    }
}