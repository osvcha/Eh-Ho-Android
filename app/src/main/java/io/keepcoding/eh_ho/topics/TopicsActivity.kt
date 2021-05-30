package io.keepcoding.eh_ho.topics

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import io.keepcoding.eh_ho.R
import io.keepcoding.eh_ho.databinding.ActivityTopicsBinding
import io.keepcoding.eh_ho.di.DIProvider
import io.keepcoding.eh_ho.model.Topic

class TopicsActivity : AppCompatActivity(),OnItemClickListener {
    private val binding: ActivityTopicsBinding by lazy { ActivityTopicsBinding.inflate(layoutInflater) }
    private lateinit var topicsAdapter: TopicsAdapter
    private val vm: TopicsViewModel by viewModels { DIProvider.topicsViewModelProviderFactory }

    lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.viewNoTopics.root.isVisible = false
        topicsAdapter = TopicsAdapter(this)
        binding.topics.apply {
            adapter = topicsAdapter
            addItemDecoration(DividerItemDecoration(this@TopicsActivity, LinearLayout.VERTICAL))
        }
        vm.state.observe(this) {
            when (it) {
                is TopicsViewModel.State.LoadingTopics -> renderLoading(it)
                is TopicsViewModel.State.TopicsReceived -> {
                    binding.viewLoading.root.isVisible = false
                    topicsAdapter.submitList(it.topics)
                }
                is TopicsViewModel.State.NoTopics -> renderEmptyState()
            }
        }

        swipeRefreshLayout = findViewById(R.id.swipe)
        swipeRefreshLayout.setOnRefreshListener {
            vm.loadTopics()
        }

    }

    override fun onResume() {
        super.onResume()
        vm.loadTopics()
    }

    private fun renderEmptyState() {
        binding.viewLoading.root.isVisible = false
        binding.viewNoTopics.root.isVisible = true
    }

    private fun renderLoading(loadingState: TopicsViewModel.State.LoadingTopics) {
        binding.viewLoading.root.isVisible = false
        swipeRefreshLayout.isRefreshing = false
        (loadingState as? TopicsViewModel.State.LoadingTopics.LoadingWithTopics)?.let { topicsAdapter.submitList(it.topics) }
    }

    companion object {
        @JvmStatic
        fun createIntent(context: Context): Intent = Intent(context, TopicsActivity::class.java)
    }

    override fun onItemClicked(topic: Topic) {
        Toast.makeText(this,"Topic name ${topic.title}",Toast.LENGTH_LONG)
            .show()
    }

}

