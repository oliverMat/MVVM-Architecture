package com.example.mvvm_order_managemente.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.mvvm_order_managemente.model.LoadingState
import com.example.mvvm_order_managemente.model.Order
import com.example.mvvm_order_managemente.model.OrderDataGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    val orderLiveData = MediatorLiveData<List<Order>>()
    private val queryLiveData = MutableLiveData<String>()
    private val allOrdersLiveData = MutableLiveData<List<Order>>()
    private val searchOrderLiveData: LiveData<List<Order>>
    val loadingStateLiveData = MutableLiveData<LoadingState>()

    private var searchJob: Job? = null
    private val debouncePeriod = 500L

    init {
        searchOrderLiveData = queryLiveData.switchMap { it: String ->
            fetchOrderByQuery(it)
        }
        orderLiveData.addSource(allOrdersLiveData) {
            orderLiveData.value = it
        }
        orderLiveData.addSource(searchOrderLiveData) {
            orderLiveData.value = it
        }


    }

    fun onViewReady() {
        if (allOrdersLiveData.value.isNullOrEmpty()) {
            fetchAllOrders()
        }
    }

    private fun fetchAllOrders() {
        loadingStateLiveData.value = LoadingState.LOADING
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val orders = OrderDataGenerator.getAllOrders()
                allOrdersLiveData.postValue(orders)
                loadingStateLiveData.postValue(LoadingState.LOADED)
            } catch (e: Exception) {
                loadingStateLiveData.postValue(LoadingState.ERROR)
            }
        }
    }

    fun onSearchQuery(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(debouncePeriod)
            if (query.isEmpty()) {
                fetchAllOrders()
            } else {
                queryLiveData.postValue(query)
            }
        }
    }

    private fun fetchOrderByQuery(query: String): LiveData<List<Order>> {
        val liveData = MutableLiveData<List<Order>>()
        loadingStateLiveData.value = LoadingState.LOADING
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val order = OrderDataGenerator.searchOrders(query)
                liveData.postValue(order)
                loadingStateLiveData.postValue(LoadingState.LOADED)
            } catch (e: Exception) {
                loadingStateLiveData.postValue(LoadingState.ERROR)
            }
        }
        return liveData
    }

}