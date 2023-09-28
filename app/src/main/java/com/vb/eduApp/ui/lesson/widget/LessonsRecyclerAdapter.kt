package com.vb.eduApp.ui.lesson.widget

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.vb.eduApp.data.model.Lesson

class LessonsRecyclerAdapter : ListAdapter<Lesson, LessonViewHolder>(
    object : DiffUtil.ItemCallback<Lesson>() {
        override fun areItemsTheSame(oldItem: Lesson, newItem: Lesson): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Lesson, newItem: Lesson): Boolean {
            return oldItem.creatorName == newItem.creatorName && oldItem.title == newItem.title
        }
    }
) {
    var onLessonClicked: ((lesson: Lesson) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LessonViewHolder =
        LessonViewHolder(LessonItemView(parent.context))

    override fun onBindViewHolder(holder: LessonViewHolder, position: Int) {
        with(holder.itemView as LessonItemView) {
            setItem(getItem(position))
            holder.itemView.setOnClickListener {
                onLessonClicked?.invoke(getItem(position))
            }
        }
    }
}
