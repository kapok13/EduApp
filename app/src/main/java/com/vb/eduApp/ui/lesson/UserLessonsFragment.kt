package com.vb.eduApp.ui.lesson

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.vb.eduApp.R
import com.vb.eduApp.data.model.Lesson
import com.vb.eduApp.data.model.Question
import com.vb.eduApp.databinding.FragmentLessonsBinding
import com.vb.eduApp.ui.lesson.widget.LessonsRecyclerAdapter

const val LESSON_FROM_LIST_TO_EDIT_KEY = "edit_lesson"

class UserLessonsFragment : Fragment() {

    private val adapter = LessonsRecyclerAdapter()
    private val lessons = mutableListOf<Lesson>()
    private var database = FirebaseDatabase.getInstance()
    private val lessonsRef = database.reference.child("lessons")

    private val binding by lazy { FragmentLessonsBinding.inflate(layoutInflater) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lessonsRecycler.adapter = adapter
        val auth = FirebaseAuth.getInstance()

        lessonsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                lessons.clear()
                snapshot.children.forEach { dataSnapshot ->
                    val creatorId =
                        dataSnapshot.child("creatorId").getValue(String::class.java) ?: ""
                    if (creatorId == auth.currentUser?.uid && creatorId.isNotEmpty()) {
                        val id = dataSnapshot.child("id").getValue(String::class.java) ?: ""
                        val title = dataSnapshot.child("title").getValue(String::class.java) ?: ""
                        val creatorName =
                            dataSnapshot.child("creatorName").getValue(String::class.java) ?: ""

                        val lessonList =
                            dataSnapshot.child("lesson").children.map { lessonSnapshot ->
                                lessonSnapshot.getValue(String::class.java) ?: ""
                            }

                        val questionList =
                            dataSnapshot.child("questions").children.map { questionSnapshot ->
                                val question =
                                    questionSnapshot.child("question").getValue(String::class.java)
                                        ?: ""
                                val answers =
                                    questionSnapshot.child("answers").children.map { answerSnapshot ->
                                        answerSnapshot.getValue(String::class.java) ?: ""
                                    }
                                val rightAnswer =
                                    questionSnapshot.child("rightAnswer").getValue(Int::class.java)
                                        ?: 0

                                Question(question, answers, rightAnswer)
                            }

                        val lesson =
                            Lesson(id, title, creatorId, creatorName, lessonList, questionList)
                        lessons.add(lesson)
                    }
                }
                adapter.submitList(lessons)
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
        adapter.onLessonClicked = {
            findNavController().navigate(R.id.lessonEditFragment, Bundle().apply {
                putString(LESSON_FROM_LIST_TO_EDIT_KEY, it.id)
            })
        }
    }
}
