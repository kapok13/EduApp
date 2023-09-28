package com.vb.eduApp.ui.lesson.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.vb.eduApp.data.model.Lesson
import com.vb.eduApp.databinding.ItemLessonBinding

class LessonItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private val binding = ItemLessonBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        layoutParams = binding.root.layoutParams
    }

    fun setItem(item: Lesson) {
        binding.postItemAuthor.text = item.creatorName
        binding.postItemTitle.text = item.title
    }
}
