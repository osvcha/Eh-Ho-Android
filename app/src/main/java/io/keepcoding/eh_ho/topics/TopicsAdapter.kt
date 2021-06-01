package io.keepcoding.eh_ho.topics

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.keepcoding.eh_ho.databinding.ViewPinnedBinding
import io.keepcoding.eh_ho.databinding.ViewTopicBinding
import io.keepcoding.eh_ho.extensions.inflater
import io.keepcoding.eh_ho.model.Topic

class TopicsAdapter(val itemClickListener: OnItemClickListener, diffUtilItemCallback: DiffUtil.ItemCallback<Topic> = DIFF) :
    ListAdapter<Topic, RecyclerView.ViewHolder>(diffUtilItemCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TOPIC_VIEW -> TopicViewHolder(parent)
            PINNED_TOPIC_VIEW -> PinnedTopicViewHolder(parent)
            else -> TopicViewHolder(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TopicViewHolder -> holder.bind(getItem(position),itemClickListener)
            is PinnedTopicViewHolder -> holder.bind(getItem(position))
        }
    }

    companion object {
        private const val TOPIC_VIEW = 0
        private const val PINNED_TOPIC_VIEW = 1

        val DIFF = object : DiffUtil.ItemCallback<Topic>() {
            override fun areItemsTheSame(oldItem: Topic, newItem: Topic): Boolean = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Topic, newItem: Topic): Boolean = oldItem == newItem
        }
    }

    override fun getItemViewType(position: Int): Int {
        val topic = currentList[position]
        return when {
            topic.pinned -> PINNED_TOPIC_VIEW
            else -> TOPIC_VIEW
        }
    }

    class TopicViewHolder(
        parent: ViewGroup,
        private val binding: ViewTopicBinding = ViewTopicBinding.inflate(
            parent.inflater,
            parent,
            false
        )
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(topic: Topic, clickListener: OnItemClickListener) {
            binding.title.text = topic.title
            binding.views.text = topic.views
            binding.likes.text = topic.likes

            itemView.setOnClickListener {
                clickListener.onItemClicked(topic)
            }
        }
    }

    class PinnedTopicViewHolder(
        parent: ViewGroup,
        private val binding: ViewPinnedBinding = ViewPinnedBinding.inflate(
            parent.inflater,
            parent,
            false
        )
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(topic: Topic) {
            binding.title.text = topic.title
        }
    }

}

interface OnItemClickListener{
    fun onItemClicked(topic: Topic)
}