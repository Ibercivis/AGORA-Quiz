//
//  NavigationManager.swift
//  agoraquiz
//
//  Created by Ibercivis on 17/5/24.
//

import SwiftUI

class NavigationManager: ObservableObject {
    @Published var currentPage: Page
    @Published var selectedTab: Int = 0

    init() {
        print("NavigationManager initialized")
        currentPage = .splash
        DispatchQueue.main.asyncAfter(deadline: .now() + 3) {
            if SessionManager.shared.isLogged {
                print("User logged, navigating to main")
                self.currentPage = .mainTab
                print(self.currentPage)
            } else {
                self.currentPage = .login
            }
        }
    }

    enum Page {
        case splash
        case login
        case signUp
        case mainTab
        case settings
        case profile
        case classicGame(GameResponse)
        case timeTrialGame(GameResponse)
        case categoryGame(GameResponse) 
    }

    func navigateToHome() {
        print("Navigating to Home")
        currentPage = .mainTab
        selectedTab = 0 // 'Main'
    }

    func navigateToSettings() {
        currentPage = .mainTab
        selectedTab = 1  // 'Settings'
    }

    func navigateToProfile() {
        currentPage = .mainTab
        selectedTab = 2  // 'Profile'
    }

    func navigateToClassicGame(gameData: GameResponse) {
        currentPage = .classicGame(gameData)
    }
    
    func navigateToCategoryGame(gameData: GameResponse) {
        currentPage = .categoryGame(gameData)
    }

    func navigateToTimeTrialGame(gameData: GameResponse) {
        currentPage = .timeTrialGame(gameData)
    }
    
    func navigateToLogin(){
        currentPage = .login
    }
}
