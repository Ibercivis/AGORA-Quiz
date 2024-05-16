//
//  Question.swift
//  agoraquiz
//
//  Created by Ibercivis on 15/5/24.
//

import Foundation

struct Question: Codable {
    let id: Int
    let questionText: String
    let answers: [String]
    let correctAnswer: Int
}
