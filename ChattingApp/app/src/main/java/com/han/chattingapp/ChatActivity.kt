package com.han.chattingapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.han.chattingapp.databinding.ActivityChatBinding
import com.han.chattingapp.databinding.ActivityMainBinding

class ChatActivity : AppCompatActivity() {

    private lateinit var receiverName: String
    private lateinit var receiverUid: String

    private lateinit var binding: ActivityChatBinding

    lateinit var mAuth: FirebaseAuth
    lateinit var mDbRef: DatabaseReference

    private lateinit var receiverRoom: String
    private lateinit var senderRoom: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 넘어온 데이터 변수에 담기
        receiverName = intent.getStringExtra("name").toString()
        receiverUid = intent.getStringExtra("uId").toString()

        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().reference

        // 접속자 Uid
        val senderUid = mAuth.currentUser?.uid

        senderRoom = receiverUid + senderUid
        receiverRoom = senderUid + receiverUid


        // 액션바에 상대방 이름 보이기
        supportActionBar?.title = receiverName

        binding.btnSend.setOnClickListener{

            val message = binding.edtMessage.text.toString()
            val messageObject = Message(message, senderUid)

            // 데이터 저장
            mDbRef.child("chats").child(senderRoom).child("message").push()
                .setValue(messageObject).addOnSuccessListener {
                    // 저장 성공시
                    mDbRef.child("chats").child(receiverRoom).child("message").push()
                        .setValue(messageObject)


                }
        }

    }
}