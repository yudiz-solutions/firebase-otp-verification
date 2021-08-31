package com.parth.otpverificationdemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private var sendOtp: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        et_enter_otp.isEnabled = false
        btn_verify_otp.isEnabled = false

        btn_generate_otp.setOnClickListener {
            if (et_phone_num.text.toString().length == 10) {
                pb.visibility = View.VISIBLE
                FirebaseAuth.getInstance().addAuthStateListener {
                    val options = PhoneAuthOptions.newBuilder(it)
                        .setPhoneNumber("+91" + et_phone_num.text.toString()) // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)// Activity (for callback binding)
                        .setCallbacks(object :
                            PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                                pb.visibility = View.GONE
                                Toast.makeText(applicationContext, "completed", Toast.LENGTH_SHORT)
                                    .show()
                            }

                            override fun onVerificationFailed(p0: FirebaseException) {
                                pb.visibility = View.GONE
                                Toast.makeText(applicationContext, "failed", Toast.LENGTH_SHORT)
                                    .show()
                            }

                            override fun onCodeSent(
                                otp: String,
                                token: PhoneAuthProvider.ForceResendingToken
                            ) {
                                pb.visibility = View.GONE
                                sendOtp = otp
                                Toast.makeText(
                                    applicationContext,
                                    "otp send successfully",
                                    Toast.LENGTH_SHORT
                                ).show()

                                et_enter_otp.isEnabled = true
                                btn_verify_otp.isEnabled = true
                                et_phone_num.isEnabled = false
                                btn_generate_otp.isEnabled = false
                            }
                        })
                        .build()
                    PhoneAuthProvider.verifyPhoneNumber(options)
                }
            } else {
                Toast.makeText(
                    applicationContext,
                    "Mobile number must be 10 Digit",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        btn_verify_otp.setOnClickListener {
            if (et_enter_otp.text.toString().length == 6) {
                val enterOtp = et_enter_otp.text.toString()
                val credential = PhoneAuthProvider.getCredential(sendOtp, enterOtp)
                pb.visibility = View.VISIBLE
                FirebaseAuth.getInstance().signInWithCredential(credential)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            pb.visibility = View.GONE
                            Toast.makeText(
                                applicationContext,
                                "login successful",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            startActivity(Intent(this, DashboardActivity::class.java))
                        } else {
                            pb.visibility = View.GONE
                            Toast.makeText(applicationContext, "wrong otp", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                et_phone_num.isEnabled = true
                btn_generate_otp.isEnabled = true
            } else {
                Toast.makeText(applicationContext, "Otp must be 6 digits", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}