//
//  AboutUsView.swift
//  agoraquiz
//
//  Created by Ibercivis on 28/5/24.
//

import SwiftUI

struct AboutUsView: View {
    @State private var selectedTab = 0

    var body: some View {
        VStack {
            Picker("Options", selection: $selectedTab) {
                Text("AGORA Project").tag(0)
                Text("Contact Us").tag(1)
            }
            .pickerStyle(SegmentedPickerStyle())
            .padding()

            if selectedTab == 0 {
                AgoraProjectView()
            } else {
                ContactUsView()
            }
        }
    }
}

#Preview {
    AboutUsView()
}
