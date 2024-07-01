package com.tok.medikan.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.tok.medikan.R
import com.tok.medikan.databinding.ActivityProfileBinding
import com.tok.medikan.util.createAlertDialog
import com.tok.medikan.util.reduceFileImage
import com.tok.medikan.util.rotateFile
import com.tok.medikan.util.uriToFile
import com.tok.medikan.viewmodel.UserViewModel
import com.tok.medikan.viewmodel.base.ViewModelFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class ProfileActivity : AppCompatActivity() {

    private val onBackPressedCallback: OnBackPressedCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        }

    private var _binding: ActivityProfileBinding? = null
    val binding get() = _binding!!
    private lateinit var viewModel: ViewModelFactory
    private val userViewModel: UserViewModel by viewModels { viewModel }
    private lateinit var loading: AlertDialog

    private var getFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
        _binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelFactory.getInstance(this)

        createLoading()

        binding.apply {
            bar.apply {
                btnBack.setOnClickListener {
                    onBackPressedCallback.handleOnBackPressed()
                }
                txtTitle.setText("Edit Profile")
            }
            edtEmail.setText(getDataIntent("email"))
            edtUsername.setText(getDataIntent("name"))
            if (getDataIntent("img").length > 0) {
                Glide.with(this@ProfileActivity)
                    .load(getDataIntent("img"))
                    .centerCrop()
                    .error(R.drawable.base)
                    .into(imgProfile)
            }

            imgProfile.setOnClickListener {
                openGallery()
            }

            btnSave.setOnClickListener {
                if (edtUsername.length() == 0 || edtEmail.length() == 0) {
                    if (edtUsername.length() == 0) {
                        edtUsername.error = getString(R.string.error_field)
                    }
                    if (edtEmail.length() == 0) {
                        edtEmail.error = getString(R.string.error_field)
                    }
                } else {
                    if (getFile == null) {
                        userViewModel.updateProfile(edtUsername.text.toString(), edtEmail.text.toString())
                    } else {
                        try {
                            val file = reduceFileImage(getFile as File)
                            userViewModel.updatePicProfile(MultipartBody.Part.createFormData("image", file.getName(), file.asRequestBody("image/*".toMediaType())))

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

            }

            userViewModel.apply {
                isLoading.observe(this@ProfileActivity) {
                    showLoading(it)
                }
                message.observe(this@ProfileActivity) {
                    it.getContentIfNotHandled()?.let {
                        Log.i("messageErrorRegister", it)
                        Toast.makeText(this@ProfileActivity, it, Toast.LENGTH_SHORT).show()

                        if (it.equals("Success")) {
                            if (getFile == null) {
                                onBackPressedCallback.handleOnBackPressed()
                            } else {
                                getFile = null
                                userViewModel.updateProfile(edtUsername.text.toString(), edtEmail.text.toString())
                            }
                        }
                    }
                }
            }
        }
    }

    private fun openGallery() {
        val chooser = Intent.createChooser(
            Intent().setAction(Intent.ACTION_GET_CONTENT).setType("image/*"),
            "Choose a Picture"
        )
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this)

            myFile.let {file ->
                rotateFile(file)
                getFile = file
            }

            Glide.with(this@ProfileActivity)
                .load(myFile)
                .centerCrop()
                .into(binding.imgProfile)
        }
    }

    private fun createLoading() {
        loading = createAlertDialog(this)
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) loading.show() else loading.dismiss()
    }

    fun getDataIntent(string: String): String {
        return intent.getStringExtra(string).toString()
    }
}