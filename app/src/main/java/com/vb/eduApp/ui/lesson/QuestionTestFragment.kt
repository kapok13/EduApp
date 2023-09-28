package com.vb.eduApp.ui.lesson

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.vb.eduApp.R
import com.vb.eduApp.data.model.Question
import com.vb.eduApp.databinding.FragmentQuestionTestBinding

class QuestionTestFragment : Fragment() {
    private val binding by lazy { FragmentQuestionTestBinding.inflate(layoutInflater) }
    private var currentQuestionIndex = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val database = FirebaseDatabase.getInstance()
        val lessonsRef = database.reference.child("lessons")
        val lessonId = arguments?.getString(LessonsFragment.LESSON_FROM_LIST_TO_DETAIL_KEY)


        lessonsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                currentQuestionIndex = 0
                snapshot.children.forEach { dataSnapshot ->
                    if ((dataSnapshot.child("id").getValue(String::class.java) ?: "") == lessonId) {
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
                        binding.testQuestion.text = questionList[currentQuestionIndex].question
                        binding.testFirstAnswer.text = questionList[currentQuestionIndex].answers[0]
                        binding.testSecondAnswer.text =
                            questionList[currentQuestionIndex].answers[1]
                        binding.testThirdAnswer.text = questionList[currentQuestionIndex].answers[2]
                        binding.testFourthAnswer.text =
                            questionList[currentQuestionIndex].answers[3]
                        val onAnswerClickListener = View.OnClickListener {
                            if (questionList[currentQuestionIndex].rightAnswer == it.tag.toString()
                                    .toInt()
                            ) {
                                binding.testCorrectAnswer.setImageResource(R.drawable.baseline_check_44)
                            } else {
                                binding.testCorrectAnswer.setImageResource(R.drawable.baseline_close_44)
                            }
                            binding.testFirstAnswer.isEnabled = false
                            binding.testSecondAnswer.isEnabled = false
                            binding.testThirdAnswer.isEnabled = false
                            binding.testFourthAnswer.isEnabled = false
                            binding.testCorrectAnswer.isVisible = true
                            Handler(Looper.getMainLooper()).postDelayed({
                                binding.testQuestionContainer
                                    .animate()
                                    .alpha(0f)
                                    .setDuration(1000L)
                                    .withEndAction {
                                        binding.testCorrectAnswer.isVisible = false
                                        binding.testFirstAnswer.isEnabled = true
                                        binding.testSecondAnswer.isEnabled = true
                                        binding.testThirdAnswer.isEnabled = true
                                        binding.testFourthAnswer.isEnabled = true
                                        currentQuestionIndex++
                                        if (currentQuestionIndex <= questionList.lastIndex) {
                                            binding.testQuestion.text =
                                                questionList[currentQuestionIndex].question
                                            binding.testFirstAnswer.text =
                                                questionList[currentQuestionIndex].answers[0]
                                            binding.testSecondAnswer.text =
                                                questionList[currentQuestionIndex].answers[1]
                                            binding.testThirdAnswer.text =
                                                questionList[currentQuestionIndex].answers[2]
                                            binding.testFourthAnswer.text =
                                                questionList[currentQuestionIndex].answers[3]
                                            binding.testQuestionContainer
                                                .animate()
                                                .alpha(1f)
                                                .duration = 1000L
                                        } else {
                                            findNavController().navigate(R.id.lessonsFragment)
                                        }
                                    }
                            }, 3000)
                        }
                        binding.testFirstAnswer.setOnClickListener(onAnswerClickListener)
                        binding.testSecondAnswer.setOnClickListener(onAnswerClickListener)
                        binding.testThirdAnswer.setOnClickListener(onAnswerClickListener)
                        binding.testFourthAnswer.setOnClickListener(onAnswerClickListener)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}
