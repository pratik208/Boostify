package com.codecrafters.boostifyapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.codecrafters.boostifyapp.views.OnboardingItem
import com.codecrafters.boostifyapp.views.OnboardingItemsAdapter
import com.google.android.material.button.MaterialButton

class OnboardingActivity : AppCompatActivity() {
    private lateinit var onboardingItemsAdapter: OnboardingItemsAdapter
    private lateinit var indicatorContainer: LinearLayout
    private lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

    //    preferences = applicationContext.getSharedPreferences("ONBOARD", Context.MODE_PRIVATE)

        setOnboardingItems()
        setupIndicators()
        setCurrentIndicator(0)
    }

    private fun setOnboardingItems(){
        onboardingItemsAdapter = OnboardingItemsAdapter(
            listOf(
                OnboardingItem(
                    onboardingImage = R.drawable.img_1,
                    title = "Welcome to Boostify App",
                    description = "Empower Your Happiness with Boostify And Elevate Your Mood with Boostify."
                ),
                OnboardingItem(
                    onboardingImage = R.drawable.img_2,
                    title = "Community",
                    description = "Connect, Chat, and Find Support in Our Empowering Community"
                ),
                OnboardingItem(
                    onboardingImage = R.drawable.img_3,
                    title = "Expert Advice at Your Fingertips",
                    description = "Consult with Trusted Doctors on the Consulting Board."
                ),
                OnboardingItem(
                    onboardingImage = R.drawable.img_4,
                    title = "MeditateMe",
                    description = "Explore Serenity with 'MeditateMe' on Boostify."
                ),
             /*   OnboardingItem(
                    onboardingImage = R.drawable.img_paint,
                    title = "Courses for absolute beginners ",
                    description = "In fact, we made a quick video to help you learn the basics"
                ),*/
                OnboardingItem(
                    onboardingImage = R.drawable.ready,
                    title = "Ready!",
                    description = "Now, get ready to sit back and enjoy Boostify application"
                )
            )
        )
        val onboardingViewPager = findViewById<ViewPager2>(R.id.onboardingViewPager)
        onboardingViewPager.adapter = onboardingItemsAdapter
        onboardingViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)
            }
        })
        (onboardingViewPager.getChildAt(0) as RecyclerView). overScrollMode =
            RecyclerView.OVER_SCROLL_NEVER
        findViewById<ImageView>(R.id.imageNext).setOnClickListener{
            if (onboardingViewPager.currentItem+1 < onboardingItemsAdapter.itemCount){
                onboardingViewPager.currentItem += 1
            } else {
                navigateToHomeActivity()
                setCompleteOnboarding()
            }
        }
        findViewById<TextView>(R.id.textSkip).setOnClickListener{
            navigateToHomeActivity()
        }
        findViewById<MaterialButton>(R.id.buttonGetStarted).setOnClickListener{
            navigateToHomeActivity()
            setCompleteOnboarding()
        }

    }

    private fun navigateToHomeActivity() {
        startActivity(Intent(applicationContext,MainActivity::class.java))
        finish()
    }

    private fun setCompleteOnboarding() {
        preferences.edit().putBoolean("ISCOMPLETE", true).apply()
    }


    private fun setupIndicators(){
        indicatorContainer = findViewById(R.id.indicatorsContainer)
        val indicators = arrayOfNulls<ImageView>(onboardingItemsAdapter.itemCount)
        val layoutParams: LinearLayout.LayoutParams =
            LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT )
        layoutParams.setMargins(8,0,8,0,)
        for (i in indicators.indices) {
            indicators[i] = ImageView(applicationContext)
            indicators[i]?.let {
                it.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_active_background
                    )
                )
                it.layoutParams = layoutParams
                indicatorContainer.addView(it)
            }
        }
    }
    private fun setCurrentIndicator (position: Int){
        val childCount = indicatorContainer.childCount
        for( i  in 0 until childCount){
            val imageView = indicatorContainer.getChildAt(i) as ImageView
            imageView.scaleType = ImageView.ScaleType.FIT_END
            val layoutParams: LinearLayout.LayoutParams =
                LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT )
            imageView.layoutParams = layoutParams
            if(i == position){
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_active_background
                    )
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_inactive_backgound
                    )
                )
            }
        }
    }
}