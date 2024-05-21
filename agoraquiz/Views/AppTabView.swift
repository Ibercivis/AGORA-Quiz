//
//  AppTabView.swift
//  agoraquiz
//
//  Created by Ibercivis on 13/5/24.
//

import SwiftUI

struct AppTabView: View {
    @EnvironmentObject var navigationManager: NavigationManager
    @EnvironmentObject var gameService: GameService
    @Binding var selectedTab: Int

    var body: some View {
        ZStack {
            TabView(selection: $selectedTab) {
                MainView()
                    .environmentObject(navigationManager)
                    .environmentObject(gameService)
                    .tabItem {
                        Image(systemName: "house.fill")
                        Text("Home")
                    }
                    .tag(0)
                    .edgesIgnoringSafeArea(.all) // Asegúrate de que cada vista tenga esto

                Text("Settings")
                    .tabItem {
                        Image(systemName: "gear")
                        Text("Settings")
                    }
                    .tag(1)
                    .edgesIgnoringSafeArea(.all) // Asegúrate de que cada vista tenga esto

                ProfileView()
                    .environmentObject(navigationManager)
                    .environmentObject(gameService)
                    .tabItem {
                        Image(systemName: "person.fill")
                        Text("Profile")
                    }
                    .tag(2)
                    .edgesIgnoringSafeArea(.all) // Asegúrate de que cada vista tenga esto
            }
        }
        .edgesIgnoringSafeArea(.all) // Este también es necesario
    }
}
