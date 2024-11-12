//
//  Game.swift
//  agoraquiz
//
//  Created by Ibercivis on 15/5/24.
//

import Foundation

struct Game: Codable {
    let id: Int
    let score: Int
    let status: String
    let gameType: String
    let timeLeft: Int
    let questions: [Question]
    
    enum CodingKeys: String, CodingKey {
        case id
        case score
        case status
        case gameType = "game_type"
        case timeLeft = "time_left"
        case questions
    }
}
