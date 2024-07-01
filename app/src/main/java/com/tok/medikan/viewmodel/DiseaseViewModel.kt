package com.tok.medikan.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tok.medikan.api.APIConfig
import com.tok.medikan.data.disease.DiseaseResponse
import com.tok.medikan.data.history.HistoryItem
import com.tok.medikan.data.history.HistoryResponse
import com.tok.medikan.util.UserPreferences
import com.tok.medikan.viewmodel.base.Event
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DiseaseViewModel(private val userPreferences: UserPreferences) : ViewModel() {

    companion object {
        private const val TAG = "HistoryViewModel"
    }

    private val _historyResponse = MutableLiveData<List<HistoryItem>>()
    val historyResponse: LiveData<List<HistoryItem>> = _historyResponse

    private val _diseaseResponse = MutableLiveData<DiseaseResponse>()
    val diseaseResponse: LiveData<DiseaseResponse> = _diseaseResponse

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _message = MutableLiveData<Event<String>>()
    val message: LiveData<Event<String>> = _message

    fun history(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val client = APIConfig.build(getTokenUser()).history(id)
            client.enqueue(object : Callback<HistoryResponse> {
                override fun onResponse(
                    call: Call<HistoryResponse>,
                    response: Response<HistoryResponse>
                ) {
                    _isLoading.value = false
                    if (response.isSuccessful && response.body() != null) {
                        val historyResponse = response.body()!!
                        _message.value = Event(historyResponse.message)
                        if (historyResponse.message.equals("jwt expired")) {
                            logout()
                        }
                        if (historyResponse.status.equals("Success")) {
                            _historyResponse.value = historyResponse.data
                        }
                    } else {
                        _message.value = Event(response.message().toString())
                        Log.e(
                            TAG,
                            "Failure: ${response.message()}, ${response.body()?.message.toString()}"
                        )
                    }
                }

                override fun onFailure(call: Call<HistoryResponse>, t: Throwable) {
                    _isLoading.value = false
                    _message.value = Event(t.message.toString())
                    Log.e(TAG, "onFailure: ${t.message.toString()}")
                }
            })
        }
    }

    fun disease(file: MultipartBody.Part, name: String, id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            val client = APIConfig.build(getTokenUser()).disease("https://predict-fhl2f6pupa-et.a.run.app/predict",file, name, id)
            client.enqueue(object : Callback<DiseaseResponse> {
                override fun onResponse(
                    call: Call<DiseaseResponse>,
                    response: Response<DiseaseResponse>
                ) {
                    _isLoading.value = false
                    if (response.isSuccessful && response.body() != null) {
                        val diseaseResponse = response.body()!!
                        _message.value = Event(diseaseResponse.message)
                        if (diseaseResponse.message.equals("jwt expired")) {
                            logout()
                        }
                        if (diseaseResponse.status.equals("Success")) {
                            _diseaseResponse.value = diseaseResponse
                        }
                    } else {
                        _message.value = Event(response.message().toString())
                        Log.e(
                            TAG,
                            "Failure: ${response.message()}, ${response.body()?.message.toString()}"
                        )
                    }
                }

                override fun onFailure(call: Call<DiseaseResponse>, t: Throwable) {
                    _isLoading.value = false
                    _message.value = Event(t.message.toString())
                    Log.e(TAG, "onFailure: ${t.message.toString()}")
                }
            })
        }
    }


    suspend fun getTokenUser(): String {
        return userPreferences.getSession().first().token
    }

    fun logout() {
        viewModelScope.launch {
            userPreferences.logout()
        }
    }
}