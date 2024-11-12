//
//  SignUpViewModel.swift
//  agoraquiz
//
//  Created by Ibercivis on 8/5/24.
//

import Foundation
import Combine

class SignUpViewModel: ObservableObject {
    @Published var username = ""
    @Published var email = ""
    @Published var password = ""
    @Published var confirmPassword = ""
    @Published var errorMessage = ""
    @Published var showToast = false
    @Published var toastMessage = ""
    
    private var cancellables = Set<AnyCancellable>()

    // Función para validar los campos antes de intentar registrar al usuario
    func validateFields() -> Bool {
        guard !username.isEmpty else {
            errorMessage = "Please enter a username"
            return false
        }
        guard isValidEmail(email) else {
            errorMessage = "Please enter a valid email address"
            return false
        }
        guard arePasswordsValid(password, confirmPassword) else {
            errorMessage = "Passwords do not match or do not meet the criteria (must include uppercase, lowercase, and numbers)"
            return false
        }
        return true
    }

    func signUp() {
        guard validateFields() else { return }
        
        errorMessage = ""

        let url = URLs.baseURL.appendingPathComponent(URLs.APIPath.signUp)
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.addValue("application/json", forHTTPHeaderField: "Content-Type")
        
        let body: [String: Any] = [
                    "username": username,
                    "email": email,
                    "password1": password,
                    "password2": confirmPassword
                ]
                
                request.httpBody = try? JSONSerialization.data(withJSONObject: body, options: [])
                
        URLSession.shared.dataTaskPublisher(for: request)
                    .tryMap { result -> Data in
                        guard let httpResponse = result.response as? HTTPURLResponse else {
                            throw NetworkError.unexpectedResponse
                        }
                        if httpResponse.statusCode == 204 {
                            return result.data
                        } else if httpResponse.statusCode == 400 {
                            throw try JSONDecoder().decode(SignUpErrorResponse.self, from: result.data)
                        } else {
                            throw NetworkError.serverError("Unexpected server error.")
                        }
                    }
                    .receive(on: DispatchQueue.main)
                    .sink(receiveCompletion: { completion in
                        switch completion {
                        case .finished:
                            self.showToastWithMessage("Account created. Please check your email to activate the account.")
                        case .failure(let error):
                            if let signUpError = error as? SignUpErrorResponse {
                                self.showToastWithMessage(signUpError.message)
                            } else {
                                self.showToastWithMessage(error.localizedDescription)
                            }
                        }
                    }, receiveValue: { _ in })
                    .store(in: &cancellables)
    }

    private func isValidEmail(_ email: String) -> Bool {
        let emailPattern = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}"
        let emailPred = NSPredicate(format:"SELF MATCHES %@", emailPattern)
        return emailPred.evaluate(with: email)
    }
    
    private func isValidPassword(_ password: String) -> Bool {
        let passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$"
        let passwordPred = NSPredicate(format: "SELF MATCHES %@", passwordPattern)
        return passwordPred.evaluate(with: password)
    }

    private func arePasswordsValid(_ password1: String, _ password2: String) -> Bool {
        return password1 == password2 && !password1.isEmpty && isValidPassword(password1)
    }
    
    struct SignUpErrorResponse: Decodable, Error {
        let username: [String]?
        let email: [String]?

        var message: String {
            var messages = [String]()
            if let usernameErrors = username {
                messages.append(contentsOf: usernameErrors)
            }
            if let emailErrors = email {
                messages.append(contentsOf: emailErrors)
            }
            return messages.joined(separator: "\n")
        }
    }

    enum NetworkError: Error {
        case unexpectedResponse
        case serverError(String)
    }
    
    private func showToastWithMessage(_ message: String) {
        self.toastMessage = message
        self.showToast = true
        DispatchQueue.main.asyncAfter(deadline: .now() + 3) {
            self.showToast = false
        }
    }
}


