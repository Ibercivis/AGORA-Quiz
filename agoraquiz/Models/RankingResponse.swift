//
//  RankingResponse.swift
//  agoraquiz
//
//  Created by Ibercivis on 22/5/24.
//

import Foundation

import Foundation

struct RankingsResponse: Codable {
    var classic: [RankingItem]
    var timeTrial: [RankingItem]

    enum CodingKeys: String, CodingKey {
        case classic
        case timeTrial = "time_trial"
    }
}

struct ClassicRanking: Codable {
    let rank: Int
    let username: String
    let points: Int
    let profileImageUrl: String?
}

struct TimeTrialRanking: Codable {
    let rank: Int
    let username: String
    let maxTimeTrialTime: Int
    let profileImageUrl: String?
}
