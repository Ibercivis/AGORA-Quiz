//
//  MainViewModel.swift
//  agoraquiz
//
//  Created by Ibercivis on 16/5/24.
//

import SwiftUI
import Combine

class MainViewModel: ObservableObject {
    @Published var gameData: GameResponse?
    @Published var showToast: Bool = false
    @Published var toastMessage: String = ""

    var gameService: GameService?
    var navigationManager: NavigationManager?
    var cancellables = Set<AnyCancellable>()

    func configure(gameService: GameService, navigationManager: NavigationManager) {
        self.gameService = gameService
        self.navigationManager = navigationManager
        checkForInProgressGame()
    }

    func checkForInProgressGame() {
        guard let gameService = gameService, let token = SessionManager.shared.token else { return }
        gameService.checkForInProgressGame()
            .sink(receiveCompletion: { completion in
                if case .failure(let error) = completion {
                    print("Error checking for in-progress game: \(error)")
                }
            }, receiveValue: { [weak self] gameResponse in
                print("Game Response received: \(gameResponse)") // Debug
                switch gameResponse.game.gameType {
                case "classic":
                    self?.navigationManager?.navigateToClassicGame(gameData: gameResponse)
                case "time_trial":
                    self?.navigationManager?.navigateToTimeTrialGame(gameData: gameResponse)
                default:
                    break
                }
            })
            .store(in: &cancellables)
    }

    func startNewGame() {
        guard let gameService = gameService, let token = SessionManager.shared.token else { return }
        gameService.startGame()
            .sink(receiveCompletion: { completion in
                if case .failure(let error) = completion {
                    print("Error starting new game: \(error)")
                }
            }, receiveValue: { [weak self] gameResponse in
                self?.navigationManager?.navigateToClassicGame(gameData: gameResponse)
            })
            .store(in: &cancellables)
    }

    func startNewTimeTrialGame() {
        guard let gameService = gameService, let token = SessionManager.shared.token else { return }
        gameService.startTimeTrialGame()
            .sink(receiveCompletion: { completion in
                if case .failure(let error) = completion {
                    print("Error starting new time trial game: \(error)")
                }
            }, receiveValue: { [weak self] gameResponse in
                self?.navigationManager?.navigateToTimeTrialGame(gameData: gameResponse)
            })
            .store(in: &cancellables)
    }

    func showUnavailableToast() {
        toastMessage = "This game mode is not available yet"
        showToast = true
        DispatchQueue.main.asyncAfter(deadline: .now() + 2) {
            self.showToast = false
        }
    }
}
