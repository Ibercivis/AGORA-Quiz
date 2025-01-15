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
        items.add(new FaqItem("How can I improve my scores?", "Within the AGORA project, we have developed two Academies where you can find information and resources to increase your knowledge and improve your scores."));
        items.add(new FaqItem("How can I change my password?", "To reset your password, go to the login screen and click on 'Forgot password?' or the Edit Profile section. Follow the instructions sent to your email."));
        items.add(new FaqItem("How can I report a bug?", "The app is currently in beta mode, so it is normal for some issues to occur. If you find something that is not working properly, please contact us at info@ibercivis.es"));
        items.add(new FaqItem("How can I contact technical support?", "You can contact technical support by emailing info@ibercivis.es"));
        items.add(new FaqItem("Why are the Categories and Multiplayer game modes not available?", "The app will continue to be developed during the life of the project, so new functionalities will be integrated in future releases."));
        items.add(new FaqItem("How can I edit my profile?", "To edit your profile, go to the profile screen and click on the 'Edit Profile' button."));
        items.add(new FaqItem("Where can I read about the Agora Project?", "You can find more information about the AGORA project on our website and social media channels. All relevant links are available in the About Us - Contact Us section."));
        return items;
    }
}
