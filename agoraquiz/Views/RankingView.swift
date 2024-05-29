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

    init() {
        UISegmentedControl.appearance().setTitleTextAttributes([.foregroundColor: UIColor.white], for: .normal)
        UISegmentedControl.appearance().setTitleTextAttributes([.foregroundColor: UIColor.black], for: .selected)
    }
    
    var body: some View {
        VStack {
            header
            
            if selectedRanking == .classic {
                RankingPodiumView(rankingItems: $viewModel.classicRanking, selectedRanking: selectedRanking)
                    .padding(.top, 140)
                RankingListView(rankingItems: $viewModel.classicRanking, selectedRanking: selectedRanking)
            } else {
                RankingPodiumView(rankingItems: $viewModel.timeTrialRanking, selectedRanking: selectedRanking)
                    .padding(.top, 140)
                RankingListView(rankingItems: $viewModel.timeTrialRanking, selectedRanking: selectedRanking)
            }
            
            Spacer()
        }
        .background(Color.white)
        .onAppear {
            viewModel.fetchRankings(gameService: gameService)
        }
        .edgesIgnoringSafeArea(.all)
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
                    .padding(.top, 20)
                
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
