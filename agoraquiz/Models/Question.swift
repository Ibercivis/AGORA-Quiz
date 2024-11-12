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
    let reference: String

    enum CodingKeys: String, CodingKey {
        case id
        case questionText = "question_text"
        case correctAnswer = "correct_answer"
        case answer1, answer2, answer3, answer4
        case reference
    }
    
    init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        id = try container.decode(Int.self, forKey: .id)
        questionText = try container.decode(String.self, forKey: .questionText)
        correctAnswer = try container.decode(Int.self, forKey: .correctAnswer)
        reference = try container.decodeIfPresent(String.self, forKey: .reference) ?? "N/A"
        
        // Almacenar respuestas en un array
        var answersArray = [String]()
        for i in 1...4 {
            let key = CodingKeys(rawValue: "answer\(i)")
            if let answerKey = key, let answer = try container.decodeIfPresent(String.self, forKey: answerKey) {
                answersArray.append(answer)
            }
        }
        answers = answersArray
    }
    
    init(id: Int, questionText: String, answers: [String], correctAnswer: Int, reference: String) {
        self.id = id
        self.questionText = questionText
        self.answers = answers
        self.correctAnswer = correctAnswer
        self.reference = reference
    }

    func encode(to encoder: Encoder) throws {
        var container = encoder.container(keyedBy: CodingKeys.self)
        try container.encode(id, forKey: .id)
        try container.encode(questionText, forKey: .questionText)
        try container.encode(correctAnswer, forKey: .correctAnswer)
        try container.encode(reference, forKey: .reference)
        for (index, answer) in answers.enumerated() {
            try container.encode(answer, forKey: .init(stringValue: "answer\(index + 1)")!)
        }
    }
}
