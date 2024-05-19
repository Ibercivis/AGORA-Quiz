//
//  UserProfile.swift
//  agoraquiz
//
//  Created by Ibercivis on 18/5/24.
//

import Foundation

struct UserProfile: Codable {
    let profileImageUrl: String
    let totalPoints: Int
    let totalGamesPlayed: Int
    let totalGamesAbandoned: Int
    let totalCorrectAnswers: Int
    let totalIncorrectAnswers: Int

    enum CodingKeys: String, CodingKey {
        case profileImageUrl = "profile_image"
        case totalPoints = "total_points"
        case totalGamesPlayed = "total_games_played"
        case totalGamesAbandoned = "total_games_abandoned"
        case totalCorrectAnswers = "total_correct_answers"
        case totalIncorrectAnswers = "total_incorrect_answers"
    }
}
