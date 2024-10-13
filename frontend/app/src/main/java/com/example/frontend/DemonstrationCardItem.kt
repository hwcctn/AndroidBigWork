package com.example.frontend


data class DemonstrationCardItem (
    val id:Int,
    val sender:String,
    val title: String,
    val content:List<String>,
    val images:List<String>,
    val date:Long
)