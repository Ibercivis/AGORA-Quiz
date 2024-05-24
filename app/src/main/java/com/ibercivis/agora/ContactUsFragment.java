package com.ibercivis.agora;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ContactUsFragment extends Fragment {

    private static final String URL_FACEBOOK = "https://www.facebook.com/profile.php?id=100090701292038";
    private static final String URL_X = "https://x.com/AgoraAdaptation";
    private static final String URL_LINKEDIN = "https://www.linkedin.com/company/adaptation-agora/";
    private static final String URL_ACADEMIES = "https://adaptationagora.eu/digital-academies-tools/";
    private static final String URL_WEBSITE = "https://adaptationagora.eu/";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_us, container, false);

        // Set click listeners for the links
        view.findViewById(R.id.link_facebook).setOnClickListener(v -> openUrl(URL_FACEBOOK));
        view.findViewById(R.id.link_x).setOnClickListener(v -> openUrl(URL_X));
        view.findViewById(R.id.link_linkedin).setOnClickListener(v -> openUrl(URL_LINKEDIN));
        view.findViewById(R.id.link_academies).setOnClickListener(v -> openUrl(URL_ACADEMIES));
        view.findViewById(R.id.link_website).setOnClickListener(v -> openUrl(URL_WEBSITE));

        return view;
    }

    private void openUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

}
