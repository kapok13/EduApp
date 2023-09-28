package com.vb.eduApp.ui.lesson

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.vb.eduApp.R
import com.vb.eduApp.databinding.FragmentLessonDetailBinding
import com.vb.eduApp.ui.lesson.LessonsFragment.Companion.LESSON_FROM_LIST_TO_DETAIL_KEY

class LessonDetailFragment : Fragment() {
    private val binding by lazy { FragmentLessonDetailBinding.inflate(layoutInflater) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val storage = Firebase.storage
        val storageRef = storage.reference
        val database = FirebaseDatabase.getInstance()
        val lessonsRef = database.reference.child("lessons")
        val lessonId = arguments?.getString(LESSON_FROM_LIST_TO_DETAIL_KEY)


        lessonsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach { dataSnapshot ->
                    if ((dataSnapshot.child("id").getValue(String::class.java) ?: "") == lessonId) {
                        binding.lessonDetailAuthor.text =
                            dataSnapshot.child("creatorName").getValue(String::class.java) ?: ""
                        binding.lessonDetailTitle.text =
                            dataSnapshot.child("title").getValue(String::class.java) ?: ""
                        val lessonList =
                            dataSnapshot.child("lesson").children.map { lessonSnapshot ->
                                lessonSnapshot.getValue(String::class.java) ?: ""
                            }
                        lessonList.filter { it != "null" }.forEach {
                            if (it.takeLast(10) == URI_IDENTIFICATION_CODE) {
                                val imageView = ImageView(requireContext()).apply {
                                    layoutParams = ViewGroup.LayoutParams(
                                        ViewGroup.LayoutParams.WRAP_CONTENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT
                                    )
                                    adjustViewBounds = true
                                }
                                val imageRef = storageRef.child("${lessonId}/${it}")
                                imageRef.downloadUrl.addOnSuccessListener { uri ->
                                    Glide.with(requireContext())
                                        .load(uri)
                                        .into(imageView)
                                }
                                binding.lessonDetailMainContainer.addView(imageView)
                            } else {
                                binding.lessonDetailMainContainer.addView(
                                    TextView(requireContext()).apply {
                                        layoutParams = ViewGroup.LayoutParams(
                                            ViewGroup.LayoutParams.MATCH_PARENT,
                                            ViewGroup.LayoutParams.WRAP_CONTENT
                                        )
                                        text = it
                                    }
                                )
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

        binding.lessonDetailToQuestions.setOnClickListener {
            findNavController().navigate(R.id.questionTestFragment, Bundle().apply {
                putString(LESSON_FROM_LIST_TO_DETAIL_KEY, lessonId)
            })
        }
    }
}
