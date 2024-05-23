//
//  RankingPodiumView.swift
//  agoraquiz
//
//  Created by Ibercivis on 22/5/24.
//

import SwiftUI

struct RankingPodiumView: View {
    @Binding var rankingItems: [RankingItem]
    let selectedRanking: RankingType

    var body: some View {
        VStack {
            ZStack {
                Image("podium_image")
                    .resizable()
                    .aspectRatio(contentMode: .fit)
                    .frame(width: UIScreen.main.bounds.width)
                
                HStack(alignment: .top, spacing: 20) {
                    Spacer()
                    
                    VStack {
                        RankingPodiumItemView(rank: 2, rankingItem: rankingItems.indices.contains(1) ? rankingItems[1] : .empty, selectedRanking: selectedRanking)
                    }
                    .padding(.top, 40)
                    
                    Spacer()
                    
                    VStack {
                        RankingPodiumItemView(rank: 1, rankingItem: rankingItems.indices.contains(0) ? rankingItems[0] : .empty, selectedRanking: selectedRanking)
                    }
                    .padding(.top, 0)
                    
                    Spacer()
                    
                    VStack {
                        RankingPodiumItemView(rank: 3, rankingItem: rankingItems.indices.contains(2) ? rankingItems[2] : .empty, selectedRanking: selectedRanking)
                    }
                    .padding(.top, 60)
                    
                    Spacer()
                }
                .offset(y: -140)
            }
        }
    }
}

struct RankingPodiumItemView: View {
    let rank: Int
    let rankingItem: RankingItem
    let selectedRanking: RankingType

    var body: some View {
        VStack {
            ZStack(alignment: .top) {
                if let imageUrl = rankingItem.fullProfileImageUrl {
                    URLImage(url: imageUrl)
                        .clipShape(Circle())
                        .frame(width: 60, height: 60)
                } else {
                    Image("avatarPlain")
                        .resizable()
                        .clipShape(Circle())
                        .frame(width: 60, height: 60)
                }
                Text(selectedRanking == .classic ? "\(rankingItem.totalPoints) Pts" : rankingItem.maxTimeTrialTime.formattedTime)
                    .bold()
                    .padding(4)
                    .background(Color.white)
                    .clipShape(Capsule())
                    .offset(y: -20)
                    .shadow(color: .gray, radius: 2, x: 0, y: 2)
            }
            Text(rankingItem.username)
        }
    }
}

extension Int {
    var formattedTime: String {
        let minutes = self / 60
        let seconds = self % 60
        return String(format: "%02d:%02d", minutes, seconds)
    }
}

struct RankingPodiumView_Previews: PreviewProvider {
    static var previews: some View {
        let mockRankingItems = [
            RankingItem(username: "Player1", totalPoints: 100, maxTimeTrialTime: 120, profileImageUrl: "/media/profile_images/profile_1.jpg"),
            RankingItem(username: "Player2", totalPoints: 90, maxTimeTrialTime: 110, profileImageUrl: "/media/profile_images/profile_2.jpg"),
            RankingItem(username: "Player3", totalPoints: 80, maxTimeTrialTime: 100, profileImageUrl: "/media/profile_images/profile_3.jpg")
        ]
        
        return Group {
            RankingPodiumView(rankingItems: .constant(mockRankingItems), selectedRanking: .classic)
            RankingPodiumView(rankingItems: .constant(mockRankingItems), selectedRanking: .timeTrial)
        }
    }
}
