//
//  RankingView.swift
//  agoraquiz
//
//  Created by Ibercivis on 22/5/24.
//

import SwiftUI

struct RankingView: View {
    @EnvironmentObject var navigationManager: NavigationManager
    @EnvironmentObject var gameService: GameService
    @StateObject private var viewModel = RankingViewModel()

    @State private var selectedRanking: RankingType = .classic

    var body: some View {
        VStack {
            header

            if selectedRanking == .classic {
                RankingPodiumView(rankingItems: $viewModel.classicRanking, selectedRanking: selectedRanking)
                RankingListView(rankingItems: $viewModel.classicRanking, selectedRanking: selectedRanking)
            } else {
                RankingPodiumView(rankingItems: $viewModel.timeTrialRanking, selectedRanking: selectedRanking)
                RankingListView(rankingItems: $viewModel.timeTrialRanking, selectedRanking: selectedRanking)
            }
        }
        .onAppear {
            viewModel.fetchRankings(gameService: gameService)
        }
    }

    private var header: some View {
        ZStack {
            Image("headerPrimary")
                .resizable()
                .aspectRatio(contentMode: .fit)
                .frame(width: UIScreen.main.bounds.width)
            
            VStack{
                Text("Ranking")
                    .font(.largeTitle)
                    .bold()
                    .foregroundColor(.white)
                
                Picker("", selection: $selectedRanking) {
                    Text("Classic").tag(RankingType.classic)
                    Text("Time Trial").tag(RankingType.timeTrial)
                }
                .pickerStyle(SegmentedPickerStyle())
                .padding()
            }
            
        }
    }
}

enum RankingType {
    case classic
    case timeTrial
}

struct RankingView_Previews: PreviewProvider {
    static var previews: some View {
        let gameService = GameService()
        let navigationManager = NavigationManager()

        RankingView()
            .environmentObject(navigationManager)
            .environmentObject(gameService)
    }
}
