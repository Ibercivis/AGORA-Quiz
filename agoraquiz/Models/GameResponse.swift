//
//  GameResponse.swift
//  agoraquiz
//
//  Created by Ibercivis on 15/5/24.
//

import Foundation

struct GameResponse: Codable {
    let game: Game
    let nextQuestion: Question
    let currentQuestionIndex: Int
    let correctAnswersCount: Int
}
