//
//  URLs.swift
//  agoraquiz
//
//  Created by Ibercivis on 8/5/24.
//

import Foundation

struct URLs {
    static let baseURL = URL(string: "http://dev.ibercivis.es:10002")!
    
    struct APIPath {
        static let login = "/api/login"
        static let signUp = "/api/signup"
        static let startGame = "/api/games/start/"
        static let answerQuestion = "/api/games/%d/answer/"
        static let checkInProgressGame = "/api/games/resume/"
        static let abandonGame = "/api/games/%d/abandon/"
    }
}
