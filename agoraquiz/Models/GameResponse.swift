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

    enum CodingKeys: String, CodingKey {
        case game
        case nextQuestion = "next_question"
        case currentQuestionIndex = "current_question_index"
        case correctAnswersCount = "correct_answers_count"
    }
}
