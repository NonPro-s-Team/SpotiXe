package com.example.spotixe.Data

fun <T> List<T>.chunkByFour(): List<List<T>> = this.chunked(4)
