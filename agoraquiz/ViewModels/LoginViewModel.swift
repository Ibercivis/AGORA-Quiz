//
//  LoginViewModel.swift
//  agoraquiz
//
//  Created by Ibercivis on 7/5/24.
//

import Foundation

class LoginViewModel: ObservableObject {
    @Published var username = ""
    @Published var password = ""
    @Published var isLoggedIn = SessionManager.shared.isLogged
    
    func login() {
        // Aquí se implementa la llamada al API
        let url = URLs.baseURL.appendingPathComponent(URLs.APIPath.login)
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.addValue("application/json", forHTTPHeaderField: "Content-Type")
        
        let body: [String: Any] = [
            "username": username,
            "password": password
        ]
        
        request.httpBody = try? JSONSerialization.data(withJSONObject: body, options: [])
        
        URLSession.shared.dataTask(with: request) { data, response, error in
            if let data = data, let json = try? JSONSerialization.jsonObject(with: data) as? [String: Any], let token = json["token"] as? String {
                DispatchQueue.main.async {
                    SessionManager.shared.createLoginSession(username: self.username, token: token)
                    self.isLoggedIn = true
                }
            } else {
                // Manejar error
                DispatchQueue.main.async {
                    // Actualizar algún estado para mostrar un error, etc.
                }
            }
        }.resume()
    }
    
    func logout() {
        SessionManager.shared.logoutUser()
        isLoggedIn = false
    }
}

