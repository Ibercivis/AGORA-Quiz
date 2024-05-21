//
//  GameMode.swift
//  agoraquiz
//
//  Created by Ibercivis on 17/5/24.
//

import Foundation

enum GameMode: String, CaseIterable {
    case classic = "Classic"
    case timeTrial = "Time Trial"
    case categories = "Categories"
    case multiplayer = "Multiplayer"
    
    var iconName: String {
            switch self {
            case .classic:
                return "ic_classic"
            case .timeTrial:
                return "ic_timetrial"
            case .categories:
                return "ic_categories"
            case .multiplayer:
                return "ic_multiplayer"
            }
        }
}
