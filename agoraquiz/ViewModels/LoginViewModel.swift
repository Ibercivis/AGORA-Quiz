//
//  LoginViewModel.swift
//  agoraquiz
//
//  Created by Ibercivis on 7/5/24.
//

import Foundation
import Combine

class LoginViewModel: ObservableObject {
    @Published var username = ""
    @Published var password = ""
    @Published var isLoggedIn = SessionManager.shared.isLogged

    func login() {
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
            if let error = error {
                DispatchQueue.main.async {
                    print("Network error: \(error.localizedDescription)")
                }
                return
            }
            
            guard let httpResponse = response as? HTTPURLResponse else {
                DispatchQueue.main.async {
                    print("Invalid response from server")
                }
                return
            }
            
            guard httpResponse.statusCode == 200 else {
                DispatchQueue.main.async {
                    print("Server returned status code: \(httpResponse.statusCode)")
                }
                return
            }
            
            guard let data = data else {
                DispatchQueue.main.async {
                    print("No data received from server")
                }
                return
            }
            
            do {
                if let json = try JSONSerialization.jsonObject(with: data) as? [String: Any],
                   let token = json["key"] as? String {  // Cambiado de "token" a "key"
                    DispatchQueue.main.async {
                        SessionManager.shared.createLoginSession(username: self.username, token: token)
                        self.isLoggedIn = true
                        print("Login successful: \(self.isLoggedIn)")
                    }
                } else {
                    DispatchQueue.main.async {
                        print("Invalid JSON structure: \(String(data: data, encoding: .utf8) ?? "")")
                    }
                }
            } catch {
                DispatchQueue.main.async {
                    print("JSON parsing error: \(error.localizedDescription)")
                }
            }
        }.resume()
    }
    
    func logout() {
        SessionManager.shared.logoutUser()
        isLoggedIn = false
    }
}

