//
//  SessionManager.swift
//  agoraquiz
//
//  Created by Ibercivis on 17/5/24.
//

import Foundation

class SessionManager {
    static let shared = SessionManager()
    
    private let defaults = UserDefaults.standard
    
    private let keyUsername = "username"
    private let keyToken = "token"
    private let keyIsLogged = "isLogged"
    
    private init() {}
    
    func createLoginSession(username: String, token: String) {
        print("Creating login session for username: \(username)")
        defaults.set(username, forKey: keyUsername)
        defaults.set(token, forKey: keyToken)
        defaults.set(true, forKey: keyIsLogged)
    }
    
    var username: String? {
        defaults.string(forKey: keyUsername)
    }
    
    var token: String? {
        defaults.string(forKey: keyToken)
    }
    
    var isLogged: Bool {
        defaults.bool(forKey: keyIsLogged)
    }
    
    func logoutUser() {
        defaults.removeObject(forKey: keyUsername)
        defaults.removeObject(forKey: keyToken)
        defaults.set(false, forKey: keyIsLogged)
    }
}
