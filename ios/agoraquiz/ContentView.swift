//
//  ContentView.swift
//  agoraquiz
//
//  Created by Ibercivis on 7/5/24.
//

import SwiftUI

struct ContentView: View {
    @EnvironmentObject var navigationManager: NavigationManager
    @EnvironmentObject var gameService: GameService

    var body: some View {
        
            switch navigationManager.currentPage {
            case .splash:
                SplashView()
                    .environmentObject(navigationManager)
            case .login:
                LoginView()
                    .environmentObject(navigationManager)
                    .environmentObject(gameService)
            case .signUp:
                SignUpView()
                    .environmentObject(navigationManager)
                    .environmentObject(gameService)
            case .mainTab, .settings, .profile:
                AppTabView(selectedTab: $navigationManager.selectedTab)
                    .environmentObject(navigationManager)
                    .environmentObject(gameService)
            case .classicGame(let gameData):
                ClassicGameView(viewModel: ClassicGameViewModel(gameData: gameData))
                    .environmentObject(navigationManager)
                    .environmentObject(gameService)
            case .timeTrialGame(let gameData):
                TimeTrialGameView(viewModel: TimeTrialGameViewModel(gameData: gameData))
                    .environmentObject(navigationManager)
                    .environmentObject(gameService)
            case .categoryGame(let gameData):
                CategoryGameView(viewModel: CategoryGameViewModel(gameData: gameData))
                    .environmentObject(navigationManager)
                    .environmentObject(gameService)
            }
    }
}

