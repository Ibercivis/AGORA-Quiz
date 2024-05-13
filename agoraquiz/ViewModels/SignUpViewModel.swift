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
            errorMessage = "Passwords do not match or are empty"
            return false
        }
        return true
    }

    func signUp() {
        guard validateFields() else { return }

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
        
        URLSession.shared.dataTask(with: request) { data, response, error in
            DispatchQueue.main.async {
                if let data = data, let response = try? JSONSerialization.jsonObject(with: data) as? [String: Any] {
                    self.errorMessage = "Account created. Please check your email to activate the account."
                } else {
                    self.errorMessage = "Registration failed. Please try again later."
                }
            }
        }.resume()
    }

    private func isValidEmail(_ email: String) -> Bool {
        let emailPattern = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}"
        let emailPred = NSPredicate(format:"SELF MATCHES %@", emailPattern)
        return emailPred.evaluate(with: email)
    }

    private func arePasswordsValid(_ password1: String, _ password2: String) -> Bool {
        return password1 == password2 && !password1.isEmpty
    }
}


