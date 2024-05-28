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
        static let login = "/api/users/authentication/login/"
        static let signUp = "/api/users/registration/"
        static let startGame = "/api/games/start/"
        static let startTimeTrialGame = "/api/games/start_time_trial/"
        static let answerQuestion = "/api/games/%d/answer/"
        static let answerTimeTrialQuestion = "/api/games/%d/answer_time_trial/"
        static let checkInProgressGame = "/api/games/resume/"
        static let abandonGame = "/api/games/%d/abandon/"
        static let finishGame = "/api/games/%d/finish_game/"
        static let getProfile = "/api/users/profile/"
        static let updateProfile = "/api/users/profile/update/"
        static let resetPassword = "/api/users/authentication/password/reset/"
        static let rankings = "/api/users/rankings/"
    }
}
