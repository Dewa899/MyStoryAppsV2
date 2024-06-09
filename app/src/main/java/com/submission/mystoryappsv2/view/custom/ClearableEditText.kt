package com.submission.mystoryappsv2.view.custom

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RelativeLayout
import com.submission.mystoryappsv2.R

class ClearableEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    private val editText: EditText
    private val clearButton: ImageButton

    init {
        // Inflate layout dari XML
        LayoutInflater.from(context).inflate(R.layout.view_clearable_edit_text, this, true)
        editText = findViewById(R.id.edit_text)
        clearButton = findViewById(R.id.clear_button)

        // Set onClick listener untuk clear button
        clearButton.setOnClickListener {
            editText.text.clear()
        }

        // Tampilkan/ sembunyikan tombol clear berdasarkan teks di EditText
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = if (s?.isNotEmpty() == true) View.VISIBLE else View.INVISIBLE
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Atribut kustom (opsional)
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.ClearableEditText, 0, 0)
            val hint = typedArray.getString(R.styleable.ClearableEditText_hint)
            val buttonImage = typedArray.getDrawable(R.styleable.ClearableEditText_buttonDrawable)
            editText.hint = hint
            clearButton.setImageDrawable(buttonImage)
            typedArray.recycle()
        }
    }

    fun getText(): String {
        return editText.text.toString()
    }

}
