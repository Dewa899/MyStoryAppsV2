package com.submission.mystoryappsv2.view.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.submission.mystoryappsv2.R
import com.submission.mystoryappsv2.data.pref.UserPreference
import com.submission.mystoryappsv2.data.pref.dataStore
import com.submission.mystoryappsv2.databinding.ActivityLoginBinding
import com.submission.mystoryappsv2.view.ViewModelFactory
import com.submission.mystoryappsv2.view.main.MainActivity
import com.submission.mystoryappsv2.view.signup.SignupActivity
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityLoginBinding
    private lateinit var userPreference: UserPreference
    private lateinit var progressBar: ProgressBar
    private var isProcessing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPreference = UserPreference.getInstance(dataStore)
        progressBar = findViewById(R.id.progress_bar)
        progressBar.visibility = View.GONE

        lifecycleScope.launch {
            userPreference.getSession().collect { user ->
                if (user.isLogin) {
                    navigateToStoryList()
                } else {
                    setupView()
                    setupAction()
                    playAnimation()
                }
            }
        }
    }

    private fun navigateToStoryList() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            if (!isProcessing) {
                val email = binding.emailEditText.text.toString()
                val password = binding.passwordEditText.text.toString()

                if (email.isNotEmpty() && password.isNotEmpty()) {
                    isProcessing = true
                    binding.loginButton.isEnabled = false
                    binding.progressBar.visibility = View.VISIBLE

                    viewModel.login(email, password) { success, message ->
                        binding.progressBar.visibility = View.GONE
                        isProcessing = false
                        binding.loginButton.isEnabled = true

                        if (success) {
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            AlertDialog.Builder(this).apply {
                                setTitle("Login Gagal")
                                setMessage("Email atau Password salah: $message")
                                setPositiveButton("OK", null)
                                create()
                                show()
                            }
                        }
                    }
                } else {
                    AlertDialog.Builder(this).apply {
                        setTitle("Login Gagal")
                        setMessage("Email dan Password tidak boleh kosong")
                        setPositiveButton("OK", null)
                        create()
                        show()
                    }
                }
            }
        }

        binding.signupButton.setOnClickListener {
            if (!isProcessing) {
                val intent = Intent(this, SignupActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(100)
        val message = ObjectAnimator.ofFloat(binding.messageTextView, View.ALPHA, 1f).setDuration(100)
        val emailTextView = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(100)
        val emailEditTextLayout = ObjectAnimator.ofFloat(binding.emailEditText, View.ALPHA, 1f).setDuration(100)
        val passwordTextView = ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(100)
        val passwordEditTextLayout = ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val login = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(100)
        val signup = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(
                title,
                message,
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                login,
                signup
            )
            startDelay = 100
        }.start()
    }
}
