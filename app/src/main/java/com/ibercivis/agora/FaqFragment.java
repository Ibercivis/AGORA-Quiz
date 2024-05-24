package com.ibercivis.agora;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ibercivis.agora.classes.FaqItem;

import java.util.ArrayList;
import java.util.List;

public class FaqFragment extends Fragment {

    private RecyclerView recyclerView;
    private FaqAdapter faqAdapter;
    private List<FaqItem> faqList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_faq, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_faq);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        faqList = getFaqItems();
        faqAdapter = new FaqAdapter(faqList);
        recyclerView.setAdapter(faqAdapter);

        return view;
    }

    private List<FaqItem> getFaqItems() {
        List<FaqItem> items = new ArrayList<>();
        items.add(new FaqItem("How do I reset my password?", "To reset your password, go to the login screen and click on 'Forgot password'. Follow the instructions to reset your password."));
        items.add(new FaqItem("How do I create an account?", "To create an account, click on 'Sign up' on the login screen and fill in the required details."));
        items.add(new FaqItem("How can I contact technical support?", "You can contact technical support by emailing support@agora.com or calling our helpline."));
        items.add(new FaqItem("Why are the Categories and Multiplayer game modes not available?", "The app will continue to be developed during the life of the project, so new functionalities will be integrated in future releases."));
        items.add(new FaqItem("How can I edit my profile?", "To edit your profile, go to the settings page and click on 'Edit Profile'."));
        items.add(new FaqItem("Where can I read about the Agora Project?", "You can read about the Agora Project on our official website."));
        return items;
    }
}
