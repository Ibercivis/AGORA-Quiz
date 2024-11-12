//
//  ResetPasswordViewModel.swift
//  agoraquiz
//
//  Created by Ibercivis on 28/5/24.
//

import SwiftUI
import Combine

class ResetPasswordViewModel: ObservableObject {
    @Published var email: String = ""
    @Published var showToast = false
    @Published var toastMessage = ""
    @Published var isSubmitting = false
    private var cancellables = Set<AnyCancellable>()
    var gameService: GameService

    init(gameService: GameService) {
        self.gameService = gameService
    }

    func submitChangePassword() {
        guard !email.isEmpty else {
            self.showToastWithMessage("Please enter your email")
            return
        }
        isSubmitting = true
        gameService.resetPassword(email: email)
            .sink(receiveCompletion: { completion in
                self.isSubmitting = false
                switch completion {
                case .finished:
                    self.showToastWithMessage("Password reset email sent successfully")
                    self.isSubmitting = false
                case .failure(let error):
                    self.showToastWithMessage(error.localizedDescription)
                }
            }, receiveValue: { _ in })
            .store(in: &self.cancellables)
    }
    
    private func showToastWithMessage(_ message: String) {
        self.toastMessage = message
        self.showToast = true
        DispatchQueue.main.asyncAfter(deadline: .now() + 3) {
            self.showToast = false
        }
    }
}


