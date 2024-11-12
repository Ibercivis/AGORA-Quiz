//
//  RankingViewModel.swift
//  agoraquiz
//
//  Created by Ibercivis on 22/5/24.
//

import SwiftUI
import Combine

class RankingViewModel: ObservableObject {
    @Published var classicRanking: [RankingItem] = []
    @Published var timeTrialRanking: [RankingItem] = []

    private var cancellables = Set<AnyCancellable>()

    func fetchRankings(gameService: GameService) {
        gameService.getRankings()
            .sink(receiveCompletion: { completion in
                if case .failure(let error) = completion {
                    print("Error fetching rankings: \(error)")
                }
            }, receiveValue: { [weak self] rankings in
                self?.classicRanking = rankings.classic
                self?.timeTrialRanking = rankings.timeTrial
            })
            .store(in: &cancellables)
    }
}
