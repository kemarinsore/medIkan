package com.tok.medikan.ui

import android.Manifest
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.tok.medikan.R
import com.tok.medikan.databinding.ActivityDiseaseBinding
import com.tok.medikan.util.createAlertDialog
import com.tok.medikan.util.createCustomTempFile
import com.tok.medikan.util.reduceFileImage
import com.tok.medikan.util.rotateFile
import com.tok.medikan.util.uriToFile
import com.tok.medikan.viewmodel.DiseaseViewModel
import com.tok.medikan.viewmodel.base.ViewModelFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class DiseaseActivity : AppCompatActivity() {

    private val onBackPressedCallback: OnBackPressedCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        }

    private var _binding: ActivityDiseaseBinding? = null
    val binding get() = _binding!!
    private lateinit var viewModel: ViewModelFactory
    private val diseaseViewModel: DiseaseViewModel by viewModels { viewModel }
    private lateinit var loading: AlertDialog
    private lateinit var currentPhotoPath: String

    private var getFile: File? = null

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
        _binding = ActivityDiseaseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelFactory.getInstance(this)

        createLoading()
        setupPermission()

        binding.apply {
            bar.apply {
                btnBack.setOnClickListener {
                    onBackPressedCallback.handleOnBackPressed()
                }
                txtTitle.setText("Cek Kesehatan Ikan")
            }

            imgFish.setOnClickListener {
                val builder = AlertDialog.Builder(this@DiseaseActivity, R.style.CustomAlertDialog).create()
                val view = layoutInflater.inflate(R.layout.picker,null)
                val  btnCamera = view.findViewById<ConstraintLayout>(R.id.btn_camera)
                val  btnGallery = view.findViewById<ConstraintLayout>(R.id.btn_gallery)
                builder.setView(view)
                btnCamera.setOnClickListener {
                    builder.dismiss()
                    openCamera()
                }
                btnGallery.setOnClickListener {
                    builder.dismiss()
                    openGallery()
                }
                builder.setCanceledOnTouchOutside(true)
                builder.show()
            }

            btnSave.setOnClickListener {
                if (edtName.length() == 0 || getFile == null) {
                    if (edtName.length() == 0) {
                        edtName.error = getString(R.string.error_field)
                    }
                    if (getFile == null) {
                        Toast.makeText(this@DiseaseActivity, "Foto belum dipilih", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    try {
                        val file = reduceFileImage(getFile as File)
                        diseaseViewModel.disease(MultipartBody.Part.createFormData("image", file.getName(), file.asRequestBody("image/*".toMediaType())), edtName.text.toString(), getDataIntent("id").toInt())
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

            }

            diseaseViewModel.apply {
                isLoading.observe(this@DiseaseActivity) {
                    showLoading(it)
                }
                message.observe(this@DiseaseActivity) {
                    it.getContentIfNotHandled()?.let {
                        Log.i("messageErrorRegister", it)
                        Toast.makeText(this@DiseaseActivity, it, Toast.LENGTH_SHORT).show()
                    }
                }
                diseaseResponse.observe(this@DiseaseActivity) {data ->
                    val dataDisease = data.diseaseItem
                    startActivity(
                        Intent(
                            applicationContext,
                            DetailHistoryActivity::class.java
                        )
                            .putExtra("historyName", edtName.text.toString())
                            .putExtra("image", data.imageUrl)
                            .putExtra("createdAt", "Baru saja diupload")
                            .putExtra("name", dataDisease.name)
                            .putExtra("description", dataDisease.description)
                            .putExtra("treatment", dataDisease.treatment)
                    )
                    finish()
                }
            }
        }
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        intent.resolveActivity(packageManager)

        createCustomTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this,
                "com.tok.medikan",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            try {
                val myFile = File(currentPhotoPath)
                getFile = myFile
                myFile.let { file ->
                    rotateFile(file)
                    getFile = file
                    Glide.with(this@DiseaseActivity).load(BitmapFactory.decodeFile(file.path)).centerCrop().into(binding.imgFish)
                }
            } catch (e: Exception) {
                Toast.makeText(this@DiseaseActivity, "Gagal, ketika tekan tombol OK usahakan tampilan dalam mode PORTRAIT", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"

        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            try {
                val selectedImg: Uri = result.data?.data as Uri
                val myFile = uriToFile(selectedImg, this)
                rotateFile(myFile)
                getFile = myFile
                Glide.with(this@DiseaseActivity).load(selectedImg).centerCrop().into(binding.imgFish)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun createLoading() {
        loading = createAlertDialog(this)
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) loading.show() else loading.dismiss()
    }

    private fun setupPermission() {
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    fun getDataIntent(string: String): String {
        return intent.getStringExtra(string).toString()
    }
}