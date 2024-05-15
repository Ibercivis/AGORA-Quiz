//
//  Question.swift
//  agoraquiz
//
//  Created by Ibercivis on 15/5/24.
//

import Foundation

struct Question: Codable {
    var id: Int
    var questionText: String
    var answers: [String]
}
