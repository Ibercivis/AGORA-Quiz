package com.ibercivis.agora;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.appintro.AppIntro;
import com.github.appintro.AppIntroFragment;
import com.github.appintro.AppIntroPageTransformerType;

public class OnboardingActivity extends AppIntro {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Slide 1: Welcome
        addSlide(AppIntroFragment.newInstance(
                "Welcome to Agora Quiz!",
                "Test your knowledge on climate change while having fun. Learn about its causes, solutions, and impacts through engaging quizzes.",
                R.drawable.ic_onboarding_1,
                getResources().getColor(R.color.background_secondary),
                getResources().getColor(R.color.background_primary),
                getResources().getColor(R.color.background_primary)
        ));

        // Slide 2: Classic Mode
        addSlide(AppIntroFragment.newInstance(
                "Classic Mode",
                "Answer 10 questions and test your general knowledge about climate change. Can you beat your high score?",
                R.drawable.ic_onboarding_2,
                getResources().getColor(R.color.background_tertiary),
                getResources().getColor(R.color.background_primary)
        ));

        // Slide 3: Categories Mode
        addSlide(AppIntroFragment.newInstance(
                "Categories Mode",
                "Choose a specific topic —like climate change disinformation, communication, or media literacy— and dive deep into it.",
                R.drawable.ic_onboarding_3,
                getResources().getColor(R.color.background_quaternary),
                getResources().getColor(R.color.background_primary)
        ));

        // Slide 4: Time Trial Mode
        addSlide(AppIntroFragment.newInstance(
                "Time Trial Mode",
                "Race against the clock! Earn extra time with each correct answer and see how far you can go.",
                R.drawable.ic_onboarding_4,
                R.drawable.bg_onboarding,
                getResources().getColor(R.color.background_primary)
        ));

        // Slide 5: Personalize Your Experience
        addSlide(AppIntroFragment.newInstance(
                "Personalize Your Experience",
                "Update your profile to track your progress. Visit the FAQ for common questions and explore useful links about climate change.",
                R.drawable.ic_onboarding_5,
                getResources().getColor(R.color.background_quaternary),
                getResources().getColor(R.color.background_primary)
        ));

        // Slide 6: You’re Ready!
        addSlide(AppIntroFragment.newInstance(
                "You’re Ready!",
                "Let’s get started! Choose a game mode and begin your journey towards climate knowledge.",
                R.drawable.ic_onboarding_6,
                getResources().getColor(R.color.background_tertiary),
                getResources().getColor(R.color.background_primary)
        ));

        // Onboarding customization
        setTransformer(AppIntroPageTransformerType.Fade.INSTANCE);
        setSkipButtonEnabled(true);
        setVibrate(true);
        setIndicatorColor(getColor(R.color.background_primary), getColor(R.color.background_secondary));
    }

    @Override
    public void onSkipPressed(@Nullable Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        finishOnboarding();
    }

    @Override
    public void onDonePressed(@Nullable Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        finishOnboarding();
    }

    private void finishOnboarding() {
        // Set onboarding as completed
        SessionManager sessionManager = new SessionManager(this);
        sessionManager.setOnboardingCompleted(true);

        // Navigate to MainActivity
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}