//
//  ProfileViewModel.swift
//  agoraquiz
//
//  Created by Ibercivis on 18/5/24.
//

import Combine
import SwiftUI

class ProfileViewModel: ObservableObject {
    @Published var username: String = "username"
    @Published var usernameEditText: String = "username"
    @Published var emailEditText: String = "example@example.com"
    @Published var totalPoints: Int = 0
    @Published var totalGamesPlayed: Int = 0
    @Published var totalGamesAbandoned: Int = 0
    @Published var totalCorrectAnswers: Int = 0
    @Published var totalIncorrectAnswers: Int = 0
    @Published var bestGame: Int = 0
    @Published var bestTime: Int = 0
    @Published var wins: Int = 0
    @Published var defeats: Int = 0
    @Published var totalMultiplayerGames: Int = 0
    @Published var profileImageUrl: String?
    
    @Published var isSettingsViewVisible = false
    @Published var isEditingProfile = false
    @Published var isChangingPassword = false
    @Published var isViewingAboutUs = false
    @Published var isViewingFAQ = false
    
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
    
    func updateProfile() {
        guard let gameService = gameService, let token = SessionManager.shared.token else { return }

        gameService.updateProfile(username: usernameEditText, email: emailEditText, token: token)
            .sink(receiveCompletion: { completion in
                print(completion)
                switch completion {
                case .finished:
                    self.showToastWithMessage("Profile updated succesfully")
                case .failure(let error):
                    self.showToastWithMessage(self.handleError(error: error))
                }
            }, receiveValue: { updatedProfile in
                DispatchQueue.main.async {
                    self.username = updatedProfile.username
                    self.emailEditText = updatedProfile.email
                    self.usernameEditText = updatedProfile.username
                    self.showToastWithMessage("Profile updated successfully")
                    self.isEditingProfile = false
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
                    self.showToastWithMessage(self.handleError(error: error))
                }
            }, receiveValue: { userProfile in
                DispatchQueue.main.async {
                    self.updateUI(userProfile: userProfile)
                }
            })
            .store(in: &cancellables)
    }
    
    private func updateUI(userProfile: UserProfile) {
        self.username = userProfile.username
        self.usernameEditText = userProfile.username
        self.emailEditText = userProfile.email
        self.totalPoints = userProfile.totalPoints
        self.totalGamesPlayed = userProfile.totalGamesPlayed
        self.totalGamesAbandoned = userProfile.totalGamesAbandoned
        self.totalCorrectAnswers = userProfile.totalCorrectAnswers
        self.totalIncorrectAnswers = userProfile.totalIncorrectAnswers
        self.profileImageUrl = userProfile.profileImageUrl
        self.bestGame = userProfile.maxTimeTrialPoints
        self.bestTime = userProfile.maxTimeTrialTime
    }
    
    func uploadProfileImage(_ image: UIImage) {
        guard let gameService = gameService, let token = SessionManager.shared.token else { return }
        
        gameService.uploadProfileImage(image: image, token: token)
            .sink(receiveCompletion: { completion in
                switch completion {
                case .finished:
                    self.loadUserProfile()  // Reload profile to get updated image
                    self.showToastWithMessage("Avatar updated succesfully")
                case .failure(let error):
                    self.showToastWithMessage(self.handleError(error: error))
                }
            }, receiveValue: { _ in })
            .store(in: &cancellables)
    }
    
    func deleteProfileImage() {
        guard let gameService = gameService, let token = SessionManager.shared.token else { return }
        
        gameService.deleteProfileImage(token: token)
            .sink(receiveCompletion: { completion in
                switch completion {
                case .finished:
                    self.loadUserProfile()
                    self.showToastWithMessage("Avatar deleted succesfully")
                case .failure(let error):
                    self.showToastWithMessage(self.handleError(error: error))
                }
            }, receiveValue: { _ in })
            .store(in: &cancellables)
    }
    
    func submitChangePassword(email: String) {
            guard let gameService = gameService else { return }

            gameService.resetPassword(email: email)
                .sink(receiveCompletion: { completion in
                    switch completion {
                    case .finished:
                        self.showToastWithMessage("Password reset email sent succesfully")
                        self.isChangingPassword = false
                    case .failure(let error):
                        self.showToastWithMessage(self.handleError(error: error))
                    }
                }, receiveValue: { _ in })
                .store(in: &cancellables)
        }
    
    func logout() {
        SessionManager.shared.logoutUser()
        navigationManager?.navigateToLogin()
    }
    
    private func showToastWithMessage(_ message: String) {
        self.errorMessage = message
        self.showToast = true
        DispatchQueue.main.asyncAfter(deadline: .now() + 3) {
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
