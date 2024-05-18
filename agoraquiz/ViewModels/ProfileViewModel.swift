//
//  ProfileViewModel.swift
//  agoraquiz
//
//  Created by Ibercivis on 18/5/24.
//

import Foundation
import Combine

class ProfileViewModel: ObservableObject {
    @Published var username: String = "username"
    @Published var totalPoints: Int = 0
    @Published var totalGamesPlayed: Int = 0
    @Published var totalGamesAbandoned: Int = 0
    @Published var totalCorrectAnswers: Int = 0
    @Published var totalIncorrectAnswers: Int = 0
    @Published var bestGame: Int = 0
    @Published var bestTime: String = "00:00:00"
    @Published var wins: Int = 0
    @Published var defeats: Int = 0
    @Published var totalMultiplayerGames: Int = 0
    
    @Published var errorMessage: String?
    @Published var showToast: Bool = false
    
    var gameService: GameService?
    var navigationManager: NavigationManager?
    private var cancellables = Set<AnyCancellable>()
    
    func configure(gameService: GameService, navigationManager: NavigationManager) {
        self.gameService = gameService
        self.navigationManager = navigationManager
        loadUserProfile()
    }
    
    func loadUserProfile() {
        guard let gameService = gameService, let token = SessionManager.shared.token else { return }
        
        gameService.getUserProfile(token: token)
            .sink(receiveCompletion: { completion in
                switch completion {
                case .finished:
                    break
                case .failure(let error):
                    self.errorMessage = self.handleError(error: error)
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
        self.username = SessionManager.shared.username ?? "Username"
        self.totalPoints = userProfile.totalPoints
        self.totalGamesPlayed = userProfile.totalGamesPlayed
        self.totalGamesAbandoned = userProfile.totalGamesAbandoned
        self.totalCorrectAnswers = userProfile.totalCorrectAnswers
        self.totalIncorrectAnswers = userProfile.totalIncorrectAnswers
    }
    
    func logout() {
        SessionManager.shared.logoutUser()
        // Logic to navigate back to login screen can be added here
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
