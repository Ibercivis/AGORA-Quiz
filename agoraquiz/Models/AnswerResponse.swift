//
//  AnswerResponse.swift
//  agoraquiz
//
//  Created by Ibercivis on 15/5/24.
//

import Foundation

struct AnswerResponse: Codable {
    var correct: Bool
    var score: Int
    var status: String
    var nextQuestion: Question
    var currentQuestionIndex: Int
    var correctAnswersCount: Int
}
