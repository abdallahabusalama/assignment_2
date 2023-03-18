package com.example.myapplication

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.example.myapplication.databinding.ActivityMainBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.util.*


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    val reqCode: Int = 100
    val storage = Firebase.storage
    val storageRef = storage.reference
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.upload.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "application/pdf"
            startActivityForResult(intent, reqCode)
        }
        binding.showFiles.setOnClickListener {
            val intent = Intent(this, MainActivity2::class.java)
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == reqCode && resultCode == Activity.RESULT_OK && data != null) {
            val pdfUri = data.data
            if (pdfUri != null) {
                uploadPdfToFirebase(pdfUri)
            } else {
                Toast.makeText(this, "choose the file before click", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadPdfToFirebase(pdfUri: Uri) {
        val pdfRef = storageRef.child("pdfs/${pdfUri.lastPathSegment}")
        val uploadTask = pdfRef.putFile(pdfUri)

        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Uploading PDF")
        progressDialog.setMessage("Please wait...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        uploadTask.addOnCompleteListener { task ->
            progressDialog.dismiss()
            if (task.isSuccessful) {
                Toast.makeText(this, "PDF file uploaded successfully", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Error uploading PDF file", Toast.LENGTH_LONG).show()

            }
        }.addOnProgressListener { taskSnapshot ->
            val progress = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount
            progressDialog.setMessage("Uploading " + progress.toInt() + "%")
        }
    }


}