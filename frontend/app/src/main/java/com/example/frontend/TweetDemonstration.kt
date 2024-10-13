package com.example.frontend

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
class TweetDemonstration : Fragment() {
    private val tweetViewModel: TweetViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)

        val view = inflater.inflate(R.layout.fragment_tweet, container, false)
        var context = requireContext()


        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        val swipeRefreshLayout: SwipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        recyclerView.layoutManager = LinearLayoutManager(context)


        val adapter = DemonstrationCardAdapter(emptyList(), context)
        recyclerView.adapter = adapter


        tweetViewModel.tweets.observe(viewLifecycleOwner) { tweets ->
                adapter.updateData(tweets.map { tweet ->
                    Log.d("tweet", "${tweet}")
                    DemonstrationCardItem(tweet.id,tweet.sender, tweet.title, tweet.content, tweet.images,tweet.date)
                })
            swipeRefreshLayout.isRefreshing = false
        }
        tweetViewModel.establish(context)

        swipeRefreshLayout.setOnRefreshListener {
            tweetViewModel.tweets.observe(viewLifecycleOwner) { tweets ->
                adapter.updateData(tweets.map { tweet ->
                    Log.d("tweet", "${tweet}")
                    DemonstrationCardItem(tweet.id,tweet.sender, tweet.title, tweet.content, tweet.images,tweet.date)
                })
                swipeRefreshLayout.isRefreshing = false
            }
        }

        val addButton = view.findViewById<Button>(R.id.add_button)

        addButton.setOnClickListener {
            val intent = Intent(requireContext(), NewTweetActivity::class.java)

            startActivity(intent)
        }

        return view
    }

}