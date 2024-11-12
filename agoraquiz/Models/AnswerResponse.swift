//
//  AnswerResponse.swift
//  agoraquiz
//
//  Created by Ibercivis on 15/5/24.
//

import Foundation

struct AnswerResponse: Codable {
    let correct: Bool
    let nextQuestion: Question?
    let currentQuestionIndex: Int
    let correctAnswersCount: Int
    let status: String
    
    enum CodingKeys: String, CodingKey {
        case correct
        case nextQuestion = "next_question"
        case currentQuestionIndex = "current_question_index"
        case correctAnswersCount = "correct_answers_count"
        case status
    }
}
