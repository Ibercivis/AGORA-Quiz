//
//  RankingItem.swift
//  agoraquiz
//
//  Created by Ibercivis on 22/5/24.
//

import Foundation

struct RankingItem: Identifiable, Codable {
    var id: UUID = UUID()
    var username: String
    var totalPoints: Int
    var maxTimeTrialTime: Int
    var profileImageUrl: String?

    enum CodingKeys: String, CodingKey {
        case username
        case totalPoints = "total_points"
        case maxTimeTrialTime = "max_time_trial_time"
        case profileImageUrl = "profile_image_url"
    }
    
    var fullProfileImageUrl: URL? {
            guard let profileImageUrl = profileImageUrl else { return nil }
            return URLs.baseURL.appendingPathComponent(profileImageUrl)
        }

    static let empty = RankingItem(username: "Empty", totalPoints: 0, maxTimeTrialTime: 0, profileImageUrl: nil)
}
