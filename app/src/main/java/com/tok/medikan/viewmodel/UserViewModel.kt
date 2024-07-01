package com.tok.medikan.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.tok.medikan.api.APIConfig
import com.tok.medikan.data.SessionData
import com.tok.medikan.data.user.UserResponse
import com.tok.medikan.util.UserPreferences
import com.tok.medikan.viewmodel.base.Event
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserViewModel(private val userPreferences: UserPreferences) : ViewModel()  {

    companion object {
        private const val TAG = "UserViewModel"
    }

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _userResponse = MutableLiveData<UserResponse>()
    val userResponse: LiveData<UserResponse> = _userResponse

    private val _message = MutableLiveData<Event<String>>()
    val message: LiveData<Event<String>> = _message

    fun register(username: String, email: String, password: String) {
        _isLoading.value = true
        val client = APIConfig.build().register(username, email, password)
        client.enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                _isLoading.value = false
                if (response.isSuccessful && response.body() != null) {
                    val userResponse = response.body()!!
                    _message.value = Event(userResponse.message)
                    if (userResponse.status.equals("success")) {
                        _userResponse.value = userResponse
                    }
                } else {
                    _message.value = Event(response.message().toString())
                    Log.e(
                        TAG,
                        "Failure: ${response.message()}, ${response.body()?.message.toString()}"
                    )
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                _isLoading.value = false
                _message.value = Event(t.message.toString())
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

    fun login(email: String, password: String) {
        _isLoading.value = true
        val client = APIConfig.build().login(email, password)
        client.enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                _isLoading.value = false
                if (response.isSuccessful && response.body() != null) {
                    val userResponse = response.body()!!
                    _message.value = Event(userResponse.message)
                    if (userResponse.status.equals("Success")) {
                        _userResponse.value = userResponse
                    }
                } else {
                    _message.value = Event(response.message().toString())
                    Log.e(
                        TAG,
                        "Failure: ${response.message()}, ${response.body()?.message.toString()}"
                    )
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                _isLoading.value = false
                _message.value = Event(t.message.toString())
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

    fun getUser() {
        viewModelScope.launch {
            _isLoading.value = true
            val client = APIConfig.build(getTokenUser()).userGet()
            client.enqueue(object : Callback<UserResponse> {
                override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                    _isLoading.value = false
                    if (response.isSuccessful && response.body() != null) {
                        val userResponse = response.body()!!
                        _message.value = Event(userResponse.message)
                        if (userResponse.status.equals("Success")) {
                            _userResponse.value = userResponse
                        }
                        if (userResponse.message.equals("jwt expired")) {
                            logout()
                        }
                    } else {
                        _message.value = Event(response.message().toString())
                        Log.e(
                            TAG,
                            "Failure: ${response.message()}, ${response.body()?.message.toString()}"
                        )
                    }
                }

                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    _isLoading.value = false
                    _message.value = Event(t.message.toString())
                    Log.e(TAG, "onFailure: ${t.message.toString()}")
                }
            })
        }
    }

    fun updateProfile(username: String, email: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val client = APIConfig.build(getTokenUser()).updateProfile(username, email)
            client.enqueue(object : Callback<UserResponse> {
                override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                    _isLoading.value = false
                    if (response.isSuccessful && response.body() != null) {
                        val userResponse = response.body()!!
                        _message.value = Event(userResponse.status)
                    } else {
                        _message.value = Event(response.message().toString())
                        Log.e(
                            TAG,
                            "Failure: ${response.message()}, ${response.body()?.message.toString()}"
                        )
                    }
                }

                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    _isLoading.value = false
                    _message.value = Event(t.message.toString())
                    Log.e(TAG, "onFailure: ${t.message.toString()}")
                }
            })
        }
    }

    fun updatePicProfile(file: MultipartBody.Part) {
        viewModelScope.launch {
            _isLoading.value = true
            val client = APIConfig.build(getTokenUser()).updatePicProfile(file)
            client.enqueue(object : Callback<UserResponse> {
                override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                    _isLoading.value = false
                    if (response.isSuccessful && response.body() != null) {
                        val userResponse = response.body()!!
                        if (userResponse.message.equals("jwt expired")) {
                            logout()
                        }
                        _message.value = Event(userResponse.status)
                    } else {
                        _message.value = Event(response.message().toString())
                        Log.e(
                            TAG,
                            "Failure: ${response.message()}, ${response.body()?.message.toString()}"
                        )
                    }
                }

                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    _isLoading.value = false
                    _message.value = Event(t.message.toString())
                    Log.e(TAG, "onFailure: ${t.message.toString()}")
                }
            })
        }
    }

    fun saveSession(user: SessionData) {
        viewModelScope.launch {
            userPreferences.saveSession(user)
        }
    }

    suspend fun getTokenUser(): String {
        return userPreferences.getSession().first().token
    }

    fun getSessionUser(): LiveData<SessionData> {
        return userPreferences.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            userPreferences.logout()
        }
    }

}