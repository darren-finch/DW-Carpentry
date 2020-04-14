package com.all.dwcarpentry

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.all.dwcarpentry.ui.fragments.AllHousesFragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity()
{
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        auth = FirebaseAuth.getInstance()
        signInAnonymously()
    }
    private fun signInAnonymously()
    {
        //Blatant copy of Google's method, but it should get the job done.
        auth.signInAnonymously()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful)
                    Log.d("MainActivity", "signInAnonymously:success")
                else
                {
                    Log.w("MainActivity", "signInAnonymously:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }
}
