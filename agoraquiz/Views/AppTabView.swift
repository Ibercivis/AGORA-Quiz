//
//  AppTabView.swift
//  agoraquiz
//
//  Created by Ibercivis on 13/5/24.
//

import SwiftUI

struct AppTabView: View {
    var body: some View {
        TabView {
            MainView()
                .tabItem {
                    Image(systemName: "house.fill")
                    Text("Home")
                }

            Text("Settings")
                .tabItem {
                    Image(systemName: "gear")
                    Text("Settings")
                }

            Text("Profile")
                .tabItem {
                    Image(systemName: "person.fill")
                    Text("Profile")
                }
        }
    }
}


#Preview {
    AppTabView()
}
