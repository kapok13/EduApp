package com.vb.eduApp.ui.lesson

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.OnHierarchyChangeListener
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.children
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.vb.eduApp.R
import com.vb.eduApp.databinding.FragmentLessonBinding
import com.vb.eduApp.openKeyboard

const val LESSON_TITLE_KEY = "lesson_title"
const val LESSON_VALUES_KEY = "lesson_value"
const val URI_IDENTIFICATION_CODE = "2582958234"

class LessonFragment : Fragment() {
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
            findNavController().navigate(R.id.questionFragment, Bundle().apply {
                putStringArrayList(LESSON_VALUES_KEY, ArrayList(currentValues))
                putString(LESSON_TITLE_KEY, binding.createLessonTitle.text.toString())
            })
        }
        binding.createLessonNextBtn.isEnabled =
            binding.createLessonMainContainer.childCount > 0 && binding.createLessonTitle.text.isNotEmpty()
        binding.createLessonMainContainer.setOnHierarchyChangeListener(object :
            OnHierarchyChangeListener {
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
