//
//  MainViewModel.swift
//  agoraquiz
//
//  Created by Ibercivis on 16/5/24.
//

import SwiftUI
import Combine

class MainViewModel: ObservableObject {
    @Published var navigateToClassicGame: Bool = false
    @Published var gameData: GameResponse?
    @Published var showToast: Bool = false
    @Published var toastMessage: String = ""

    private var gameService: GameService
    private var cancellables = Set<AnyCancellable>()

    init(gameService: GameService) {
        self.gameService = gameService
        checkForInProgressGame()
    }

    func checkForInProgressGame() {
        guard let token = SessionManager.shared.token else { return }
        gameService.checkForInProgressGame()
            .sink(receiveCompletion: { completion in
                if case .failure(let error) = completion {
                    print("Error checking for in-progress game: \(error)")
                }
            }, receiveValue: { [weak self] gameResponse in
                self?.gameData = gameResponse
                self?.navigateToClassicGame = true
            })
            .store(in: &cancellables)
    }

    func startNewGame() {
        guard let token = SessionManager.shared.token else { return }
        gameService.startGame()
            .sink(receiveCompletion: { completion in
                if case .failure(let error) = completion {
                    print("Error starting new game: \(error)")
                }
            }, receiveValue: { [weak self] gameResponse in
                self?.gameData = gameResponse
                self?.navigateToClassicGame = true
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
