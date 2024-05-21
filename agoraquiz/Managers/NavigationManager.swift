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
        if SessionManager.shared.isLogged {
            currentPage = .mainTab
        } else {
            currentPage = .login
        }
    }

    enum Page {
        case login
        case signUp
        case mainTab
        case settings
        case profile
        case classicGame(GameResponse)
        case timeTrialGame(GameResponse) // Añadir esta línea
    }

    func navigateToHome() {
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

    func navigateToTimeTrialGame(gameData: GameResponse) { // Añadir esta función
        currentPage = .timeTrialGame(gameData)
    }
}
