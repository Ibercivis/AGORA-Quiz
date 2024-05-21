//
//  MainView.swift
//  agoraquiz
//
//  Created by Ibercivis on 13/5/24.
//

import SwiftUI

struct MainView: View {
    @EnvironmentObject var navigationManager: NavigationManager
    @EnvironmentObject var gameService: GameService
    @StateObject private var viewModel = MainViewModel()

    var body: some View {
        NavigationView {
            ScrollView {
                VStack(alignment: .leading) {
                    header
                    userInfo
                    gameModesSection
                }
            }
            .navigationBarTitle("Home", displayMode: .inline)
            .navigationBarHidden(true)
        }
        .navigationViewStyle(StackNavigationViewStyle())
        .toast(isPresented: $viewModel.showToast, message: viewModel.toastMessage)
        .onAppear {
            viewModel.configure(gameService: gameService, navigationManager: navigationManager)
        }
    }

    var header: some View {
        ZStack {
            Image("headerPrimary")
                .resizable()
                .aspectRatio(contentMode: .fit)
                .frame(width: UIScreen.main.bounds.width)
                .clipped()

            VStack(alignment: .leading, spacing: 5) {
                Text("Hi \(SessionManager.shared.username ?? "user name"),")
                    .font(.title)
                    .foregroundColor(.white)
                Text("Great to see you again!")
                    .font(.subheadline)
                    .foregroundColor(.white)
            }
            .padding()
        }
    }

    var userInfo: some View {
        HStack {
            Spacer()
            UserStatView(iconName: "star.fill", title: "Master", subtitle: "Level")
            Spacer()
            UserStatView(iconName: "trophy.fill", title: "730", subtitle: "Points")
            Spacer()
        }
        .padding()
    }

    var gameModesSection: some View {
        VStack(alignment: .leading) {
            Text("Game modes")
                .font(.headline)
                .padding(.horizontal)

            ForEach(GameMode.allCases, id: \.self) { mode in
                Button(action: {
                    switch mode {
                    case .classic:
                        viewModel.startNewGame()
                    case .timeTrial:
                        viewModel.startNewTimeTrialGame()
                    case .categories:
                        viewModel.showUnavailableToast()
                    case .multiplayer:
                        viewModel.showUnavailableToast()
                    }
                }) {
                    GameModeView(mode: mode)
                }
            }
        }
    }

    struct UserStatView: View {
        var iconName: String
        var title: String
        var subtitle: String

        var body: some View {
            VStack {
                Image(systemName: iconName)
                    .font(.largeTitle)
                    .foregroundColor(.blue)
                Text(title)
                    .font(.title3)
                    .foregroundColor(.primary)
                Text(subtitle)
                    .foregroundColor(.secondary)
            }
            .padding()
        }
    }

    struct GameModeView: View {
        var mode: GameMode

        var body: some View {
            Text(mode.rawValue)
                .padding()
                .frame(maxWidth: .infinity, alignment: .leading)
                .background(Color.white)
                .cornerRadius(10)
                .shadow(radius: 2)
                .padding(.horizontal)
        }
    }

    struct MainView_Previews: PreviewProvider {
        static var previews: some View {
            let gameService = GameService()
            let navigationManager = NavigationManager()

            MainView()
                .environmentObject(navigationManager)
                .environmentObject(gameService)
        }
    }
}
