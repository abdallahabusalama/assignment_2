package com.example.myapplication.adapter

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.databinding.UserItemBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.io.File
import java.io.FileOutputStream


class UserAdapter(
    var context: Context,
    var data: ArrayList<String>,
    var pdfs: ArrayList<ByteArray>
) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {
    private val db = FirebaseFirestore.getInstance()
    val cacheDir = context.cacheDir
    val packageName = context.packageName

    class UserViewHolder(var binding: UserItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = UserItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return UserViewHolder(binding)
    }


    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.itemView.setBackground(
            ContextCompat.getDrawable(
                holder.itemView.getContext(),
                R.drawable.underline
            )
        );

        val user: String = data[position]
        holder.binding.txtName.text = user

        holder.binding.btnDownload.setOnClickListener {
            downloadPdfFromArray(position)
        }


    }

    private fun downloadPdfFromArray(index: Int) {
        val progressDialog = ProgressDialog(context)
        progressDialog.setTitle("Uploading PDF")
        progressDialog.setMessage("Please wait...")
        progressDialog.setCancelable(false)
        progressDialog.show()


        if (index < pdfs.size) {
            val pdfBytes = pdfs[index]
            val pdfFile = File.createTempFile("temp", ".pdf", cacheDir)
            val pdfOutputStream = FileOutputStream(pdfFile)
            pdfOutputStream.write(pdfBytes)
            pdfOutputStream.close()

            val pdfUri = FileProvider.getUriForFile(context, "${packageName}.fileprovider", pdfFile)
            val pdfIntent = Intent(Intent.ACTION_VIEW)
            pdfIntent.setDataAndType(pdfUri, "application/pdf")
            pdfIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            context.startActivity(Intent.createChooser(pdfIntent, "Open PDF file"))

            progressDialog.dismiss()


        }
    }


}