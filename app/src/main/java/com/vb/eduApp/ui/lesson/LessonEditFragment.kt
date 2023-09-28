package com.vb.eduApp.ui.lesson

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.children
import androidx.core.widget.addTextChangedListener
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
import com.vb.eduApp.databinding.FragmentLessonBinding
import com.vb.eduApp.openKeyboard

const val EDIT_LESSON_ID_KEY = "lesson_id"

class LessonEditFragment : Fragment() {
    private val binding by lazy { FragmentLessonBinding.inflate(layoutInflater) }
    private val currentValues = mutableListOf<String>()

    private val imagePickerLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                data?.data?.let { uri ->
                    val imageView = ImageView(requireContext())
                    imageView.layoutParams =
                        ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                    imageView.adjustViewBounds = true
                    imageView.tooltipText = "$uri$URI_IDENTIFICATION_CODE"
                    binding.createLessonMainContainer.addView(imageView)
                    Glide.with(this)
                        .load(uri)
                        .into(imageView)
                    imageView.setOnLongClickListener {
                        binding.createLessonMainContainer.removeView(imageView)
                        true
                    }
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val lessonId = arguments?.getString(LESSON_FROM_LIST_TO_EDIT_KEY)
        val storage = Firebase.storage
        val storageRef = storage.reference
        val database = FirebaseDatabase.getInstance()
        val lessonsRef = database.reference.child("lessons")


        lessonsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach { dataSnapshot ->
                    if ((dataSnapshot.child("id").getValue(String::class.java) ?: "") == lessonId) {
                        binding.createLessonTitle.setText(dataSnapshot.child("title").getValue(String::class.java) ?: "")
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
                                binding.createLessonMainContainer.addView(imageView)
                            } else {
                                binding.createLessonMainContainer.addView(
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


        binding.createLessonAddTextBtn.setOnClickListener {
            binding.createLessonMainContainer.addView(
                EditText(requireContext()).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    setOnLongClickListener {
                        binding.createLessonMainContainer.removeView(this)
                        true
                    }
                    addTextChangedListener {
                        var isAllEditTextsFill = true
                        binding.createLessonMainContainer.children.forEach {
                            (it as? EditText)?.let { editText ->
                                if (editText.text.isNullOrEmpty()) {
                                    isAllEditTextsFill = false
                                }
                            }
                        }
                        binding.createLessonNextBtn.isEnabled =
                            isAllEditTextsFill && binding.createLessonTitle.text.isNotEmpty()
                    }
                    post {
                        this.requestFocus()
                        this.openKeyboard()
                    }
                }
            )
        }
        binding.createLessonAddPicture.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.type = "image/*"
            imagePickerLauncher.launch(intent)
            binding.createLessonMainContainer.addView(
                ImageView(requireContext()).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    adjustViewBounds = true
                }
            )
        }
        binding.createLessonNextBtn.setOnClickListener {
            binding.createLessonMainContainer.children.forEach {
                if (it as? EditText != null) {
                    currentValues.add(it.text.toString())
                } else {
                    currentValues.add(it.tooltipText.toString())
                }
            }
            findNavController().navigate(R.id.lessonEditAnswersFragment, Bundle().apply {
                putStringArrayList(LESSON_VALUES_KEY, ArrayList(currentValues))
                putString(LESSON_TITLE_KEY, binding.createLessonTitle.text.toString())
                putString(EDIT_LESSON_ID_KEY, lessonId.toString())
            })
        }
        binding.createLessonNextBtn.isEnabled =
            binding.createLessonMainContainer.childCount > 0 && binding.createLessonTitle.text.isNotEmpty()
        binding.createLessonMainContainer.setOnHierarchyChangeListener(object :
            ViewGroup.OnHierarchyChangeListener {
            override fun onChildViewAdded(p0: View?, p1: View?) {
                binding.createLessonNextBtn.isEnabled =
                    (p1 as? ImageView != null && binding.createLessonTitle.text.isNotEmpty())
            }

            override fun onChildViewRemoved(p0: View?, p1: View?) {
                if (binding.createLessonMainContainer.childCount > 0) {
                    var isAllEditTextsFill = true
                    binding.createLessonMainContainer.children.forEach {
                        (it as? EditText)?.let { editText ->
                            if (editText.text.isNullOrEmpty()) {
                                isAllEditTextsFill = false
                            }
                        }
                    }
                    binding.createLessonNextBtn.isEnabled =
                        isAllEditTextsFill && binding.createLessonTitle.text.isNotEmpty()
                } else {
                    binding.createLessonNextBtn.isEnabled = false
                }
            }
        })
    }
}
