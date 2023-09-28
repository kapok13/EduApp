package com.vb.eduApp.ui.lesson

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.vb.eduApp.R
import com.vb.eduApp.data.model.Question
import com.vb.eduApp.databinding.FragmentQuestionBinding
import com.vb.eduApp.openKeyboard

class LessonEditAnswersFragment : Fragment() {
    private val binding by lazy { FragmentQuestionBinding.inflate(layoutInflater) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val currentValues = arguments?.getStringArrayList(LESSON_VALUES_KEY)
        val title = arguments?.getString(LESSON_TITLE_KEY)
        val lessonId = arguments?.getString(EDIT_LESSON_ID_KEY)
        val database = FirebaseDatabase.getInstance()
        val lessonsRef = database.reference.child("lessons")
        val storage = Firebase.storage
        val storageRef = storage.reference

        lessonsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
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
                        questionList.forEach {
                            val questionContainer = LinearLayout(requireContext()).apply {
                                layoutParams = ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT
                                )
                                setOnLongClickListener {
                                    binding.createQuestionMainContainer.removeView(this)
                                    true
                                }
                                orientation = LinearLayout.VERTICAL
                            }
                            questionContainer.addView(
                                EditText(requireContext()).apply {
                                    layoutParams = ViewGroup.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT
                                    )
                                    setText(it.question)
                                }
                            )
                            createRadioWithEditText(
                                questionContainer,
                                text = it.answers[0],
                                isChecked = it.rightAnswer == 0
                            )
                            createRadioWithEditText(
                                questionContainer,
                                text = it.answers[1],
                                isChecked = it.rightAnswer == 1
                            )
                            createRadioWithEditText(
                                questionContainer,
                                text = it.answers[2],
                                isChecked = it.rightAnswer == 2
                            )
                            createRadioWithEditText(
                                questionContainer,
                                text = it.answers[3],
                                isChecked = it.rightAnswer == 3
                            )
                            binding.createQuestionMainContainer.addView(questionContainer)
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })


        binding.createQuestionSaveBtn.setOnClickListener {
            binding.fragmentQuestionProgress.isVisible = true
            val tasks = mutableListOf<Task<*>>()
            currentValues?.forEach {
                if (it.takeLast(10) == URI_IDENTIFICATION_CODE) {
                    val uriWithoutCode = it.removeSuffix(URI_IDENTIFICATION_CODE)
                    val imageRef = storageRef.child("${lessonId}/${it}")
                    val uri = Uri.parse(uriWithoutCode)
                    val uploadTask = imageRef.putFile(uri)
                    tasks.add(uploadTask)
                }
            }
            Tasks.whenAllComplete(tasks)
                .addOnSuccessListener {
                    binding.fragmentQuestionProgress.isVisible = false
                    val quesions = mutableListOf<Question>()
                    binding.createQuestionMainContainer.children.forEach {
                        val question = ((it as ViewGroup).getChildAt(0) as EditText).text.toString()
                        val answer0 =
                            ((it.getChildAt(1) as ViewGroup).getChildAt(1) as EditText).text.toString()
                        val answer1 =
                            ((it.getChildAt(2) as ViewGroup).getChildAt(1) as EditText).text.toString()
                        val answer2 =
                            ((it.getChildAt(3) as ViewGroup).getChildAt(1) as EditText).text.toString()
                        val answer3 =
                            ((it.getChildAt(4) as ViewGroup).getChildAt(1) as EditText).text.toString()
                        val rightAnswer = when {
                            ((it.getChildAt(1) as ViewGroup).getChildAt(0) as RadioButton).isChecked -> 0
                            ((it.getChildAt(2) as ViewGroup).getChildAt(0) as RadioButton).isChecked -> 1
                            ((it.getChildAt(3) as ViewGroup).getChildAt(0) as RadioButton).isChecked -> 2
                            ((it.getChildAt(4) as ViewGroup).getChildAt(0) as RadioButton).isChecked -> 3
                            else -> 0
                        }
                        quesions.add(
                            Question(
                                question = question,
                                answers = listOf(answer0, answer1, answer2, answer3),
                                rightAnswer = rightAnswer
                            )
                        )
                    }
                    lessonsRef.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            binding.fragmentQuestionProgress.isVisible = true
                            snapshot.children.forEach { dataSnapshot ->
                                if ((dataSnapshot.child("id").getValue(String::class.java)
                                        ?: "") == lessonId
                                ) {
                                    val lessonRef = dataSnapshot.ref

                                    val updatedValues = mapOf(
                                        "title" to title,
                                        "questions" to quesions,
                                        "lesson" to currentValues!!.toList().filterNotNull()
                                            .filter { it != "null" }
                                    )
                                    lessonRef.updateChildren(updatedValues)
                                        .addOnSuccessListener {
                                            binding.fragmentQuestionProgress.isVisible = false
                                            findNavController().navigate(R.id.menuFragment)
                                        }
                                        .addOnFailureListener {
                                            binding.fragmentQuestionProgress.isVisible = false
                                            Snackbar.make(
                                                binding.root,
                                                R.string.error_try_again,
                                                Snackbar.LENGTH_SHORT
                                            ).show()
                                        }
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Snackbar.make(
                                binding.root,
                                R.string.error_try_again,
                                Snackbar.LENGTH_SHORT
                            )
                                .show()
                        }
                    })
                }
                .addOnFailureListener {
                    binding.fragmentQuestionProgress.isVisible = false
                    Snackbar.make(binding.root, R.string.error_try_again, Snackbar.LENGTH_SHORT)
                        .show()
                }
        }
        binding.createQuestionAdd.setOnClickListener {
            val questionContainer = LinearLayout(requireContext()).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                setOnLongClickListener {
                    binding.createQuestionMainContainer.removeView(this)
                    true
                }
                orientation = LinearLayout.VERTICAL
            }
            questionContainer.addView(
                EditText(requireContext()).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    post {
                        this.requestFocus()
                        this.openKeyboard()
                    }
                }
            )
            createRadioWithEditText(questionContainer)
            createRadioWithEditText(questionContainer)
            createRadioWithEditText(questionContainer)
            createRadioWithEditText(questionContainer)
            binding.createQuestionMainContainer.addView(questionContainer)
        }
        binding.createQuestionSaveBtn.isEnabled = binding.createQuestionMainContainer.childCount > 0
        binding.createQuestionMainContainer.setOnHierarchyChangeListener(object :
            ViewGroup.OnHierarchyChangeListener {
            override fun onChildViewAdded(p0: View?, p1: View?) {
                binding.createQuestionSaveBtn.isEnabled =
                    binding.createQuestionMainContainer.childCount > 0
            }

            override fun onChildViewRemoved(p0: View?, p1: View?) {
                binding.createQuestionSaveBtn.isEnabled =
                    binding.createQuestionMainContainer.childCount > 0
            }
        })
    }

    private fun createRadioWithEditText(
        parentContainer: ViewGroup,
        isChecked: Boolean = false,
        text: String? = null
    ) {
        val container = LinearLayout(requireContext())
        container.orientation = LinearLayout.HORIZONTAL
        container.layoutParams =
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

        val radio = RadioButton(requireContext())
        radio.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        radio.setOnCheckedChangeListener { _, checked ->
            if (checked) {
                parentContainer.children.forEach {
                    (it as? LinearLayout)?.let { linear ->
                        (linear.getChildAt(0) as? RadioButton)?.let { radioButton ->
                            if (radioButton != radio) {
                                radioButton.isChecked = false
                            }
                        }
                    }
                }
            }
        }
        radio.isChecked = isChecked

        val editText = EditText(requireContext())
        editText.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        editText.isSingleLine = true
        if (text != null) editText.setText(text)

        container.addView(radio)
        container.addView(editText)

        parentContainer.addView(container)
    }
}
