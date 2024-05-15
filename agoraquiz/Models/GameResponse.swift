//
//  GameResponse.swift
//  agoraquiz
//
//  Created by Ibercivis on 15/5/24.
//

import Foundation

struct GameResponse: Codable {
    var game: Game
    var nextQuestion: Question
    var totalQuestions: Int
}
