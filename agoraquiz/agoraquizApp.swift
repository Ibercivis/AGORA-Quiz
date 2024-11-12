//
//  agoraquizApp.swift
//  agoraquiz
//
//  Created by Ibercivis on 7/5/24.
//

import SwiftUI

@main
struct agoraquizApp: App {
    var gameService = GameService()
    var navigationManager = NavigationManager()
    var body: some Scene {
        WindowGroup {
            ContentView()
                .environmentObject(gameService)
                .environmentObject(navigationManager)
                .onAppear{
                    print("App started, currentPage: \(navigationManager.currentPage)")
                }
        }
    }
}
