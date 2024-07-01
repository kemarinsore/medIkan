package com.tok.medikan.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.PopupMenu
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.tok.medikan.R
import com.tok.medikan.adapter.HistoryAdapter
import com.tok.medikan.data.history.HistoryItem
import com.tok.medikan.data.user.UserItem
import com.tok.medikan.databinding.ActivityHomeBinding
import com.tok.medikan.util.hideKeyboard
import com.tok.medikan.util.loadCircularImage
import com.tok.medikan.viewmodel.DiseaseViewModel
import com.tok.medikan.viewmodel.UserViewModel
import com.tok.medikan.viewmodel.base.ViewModelFactory

class HomeActivity : AppCompatActivity() {

    private var _binding: ActivityHomeBinding? = null
    val binding get() = _binding!!
    private lateinit var viewModel: ViewModelFactory
    private val userViewModel: UserViewModel by viewModels { viewModel }
    private val diseaseViewModel: DiseaseViewModel by viewModels { viewModel }
    private var userItem: UserItem? = null
    private lateinit var historyAdapter: HistoryAdapter
    private var loadApi: Boolean = true
    private var historyList: List<HistoryItem> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelFactory.getInstance(this)

        binding.apply {
            fabAdd.apply {
                setOnClickListener {
                    if (userItem != null) {
                        this.animate().rotationBy(180f).setDuration(100)
                            .setInterpolator(LinearInterpolator())
                            .start()
                        loadApi = true
                        startActivity(
                            Intent(this@HomeActivity, DiseaseActivity::class.java)
                                .putExtra("id", userItem!!.id)
                        )
                    }
                }
            }
            btnMore.setOnClickListener {
                val more = PopupMenu(this@HomeActivity, it)
                more.menuInflater.inflate(R.menu.main_menu, more.menu)
                more.setOnMenuItemClickListener({
                    when (it.itemId) {
                        R.id.action_profile -> {
                            if (userItem != null) {
                                var img = ""
                                if (userItem!!.profilePicture != null) {
                                    img = userItem!!.profilePicture
                                }
                                startActivity(
                                    Intent(this@HomeActivity, ProfileActivity::class.java)
                                        .putExtra("name", userItem!!.username)
                                        .putExtra("email", userItem!!.email)
                                        .putExtra("img", img)
                                )
                                loadApi = true
                            }
                        }

                        R.id.action_logout -> {
                            userViewModel.logout()
                        }
                    }
                    true
                })
                more.show()
            }
            Glide.with(this@HomeActivity)
                .load(ContextCompat.getDrawable(this@HomeActivity, R.drawable.base)).circleCrop()
                .into(imgProfile)

            svHome.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    filterHistory(newText)
                    return false
                }

            })
        }

        userViewModel.apply {
            message.observe(this@HomeActivity) {
                it.getContentIfNotHandled()?.let {
                    Log.i("messageErrorRegister", it)
                    Toast.makeText(this@HomeActivity, it, Toast.LENGTH_SHORT).show()
                }
            }
            userResponse.observe(this@HomeActivity) {
                it.apply {
                    userItem.apply {
                        this@HomeActivity.userItem = this
                        binding.apply {
                            if (profilePicture != null) {
                                imgProfile.loadCircularImage(profilePicture)
                            }
                        }
                        diseaseViewModel.history(id)
                    }
                }

            }
        }

        diseaseViewModel.apply {
            message.observe(this@HomeActivity) {
                it.getContentIfNotHandled()?.let {
                    Log.i("messageErrorRegister", it)
                    Toast.makeText(this@HomeActivity, it, Toast.LENGTH_SHORT).show()
                }
            }
            historyResponse.observe(this@HomeActivity) {
                if (it.size > 0) {
                    historyList = it.reversed()
                    historyAdapter = HistoryAdapter(historyList)
                    binding.rvHistory.apply {
                        layoutManager = GridLayoutManager(this@HomeActivity, 2)
                        adapter = historyAdapter
                        visibility = View.VISIBLE
                    }
                    binding.txtNotFound.visibility = View.GONE
                } else {
                    binding.rvHistory.visibility = View.GONE
                    binding.txtNotFound.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun filterHistory(newText: String) {
        if (historyList.size > 0) {
            val filteredlist = arrayListOf<HistoryItem>()

            historyList.forEachIndexed { index, historyItem ->
                if (historyItem.historyName.lowercase().contains(newText.lowercase())) {
                    filteredlist.add(historyItem)
                }
            }

            if (filteredlist.size > 0) {
                historyAdapter.filter(filteredlist)
                binding.rvHistory.visibility = View.VISIBLE
                binding.txtNotFound.visibility = View.GONE
            } else {
                binding.rvHistory.visibility = View.GONE
                binding.txtNotFound.visibility = View.VISIBLE
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.svHome.clearFocus()
        hideKeyboard()
        if (loadApi) {
            userViewModel.getUser()
            loadApi = false
        }
    }
}