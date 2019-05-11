package io.rm.mvi.view

interface DataSource<T> {
    val itemsCount: Int
    fun getItemAtPosition(position: Int): T
}