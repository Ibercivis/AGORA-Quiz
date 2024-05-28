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
    @Published var profileImageUrl: String?
    @Published var userPoints: Int = 0
    @Published var username: String = "username"

    var gameService: GameService?
    var navigationManager: NavigationManager?
    var cancellables = Set<AnyCancellable>()

    func configure(gameService: GameService, navigationManager: NavigationManager) {
        self.gameService = gameService
        self.navigationManager = navigationManager
        loadUserProfile()
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
    
    func loadUserProfile() {
        guard let gameService = gameService, let token = SessionManager.shared.token else { return }
        
        gameService.getUserProfile(token: token)
            .sink(receiveCompletion: { completion in
                switch completion {
                case .finished:
                    break
                case .failure(let error):
                    self.toastMessage = self.handleError(error: error)
                    self.showToast = true
                }
            }, receiveValue: { userProfile in
                DispatchQueue.main.async {
                    self.updateUI(userProfile: userProfile)
                }
            })
            .store(in: &cancellables)
    }
    
    private func updateUI(userProfile: UserProfile) {
        self.profileImageUrl = userProfile.profileImageUrl
        self.userPoints = userProfile.totalPoints
        self.username = userProfile.username
    }

    func startNewGame() {
        guard let gameService = gameService, let token = SessionManager.shared.token else { return }
        gameService.startGame()
            .sink(receiveCompletion: { completion in
                if case .failure(let error) = completion {
                    self.showToastWithMessage(self.handleError(error: error))
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
                    self.showToastWithMessage(self.handleError(error: error))
                }
            }, receiveValue: { [weak self] gameResponse in
                self?.navigationManager?.navigateToTimeTrialGame(gameData: gameResponse)
            })
            .store(in: &cancellables)
    }
    
    private func showToastWithMessage(_ message: String) {
        self.toastMessage = message
        self.showToast = true
        DispatchQueue.main.asyncAfter(deadline: .now() + 3) {
            self.showToast = false
        }
    }

    func showUnavailableToast() {
        toastMessage = "This game mode is not available yet"
        showToast = true
        DispatchQueue.main.asyncAfter(deadline: .now() + 2) {
            self.showToast = false
        }
    }
    
    private func handleError(error: Error) -> String {
        switch error {
        case is URLError:
            return "Cannot connect to the server. Please check your internet connection."
        case let decodingError as DecodingError:
            switch decodingError {
            case .typeMismatch(_, let context):
                return "Type mismatch: \(context.debugDescription)"
            case .valueNotFound(_, let context):
                return "Value not found: \(context.debugDescription)"
            case .keyNotFound(_, let context):
                return "Key not found: \(context.debugDescription)"
            case .dataCorrupted(let context):
                return "Data corrupted: \(context.debugDescription)"
            @unknown default:
                return "Decoding error: \(decodingError.localizedDescription)"
            }
        default:
            return "An unknown error occurred. Please try again later."
        }
    }
}
