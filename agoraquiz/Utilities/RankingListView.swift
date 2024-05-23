//
//  RankingListView.swift
//  agoraquiz
//
//  Created by Ibercivis on 22/5/24.
//

import SwiftUI

struct RankingListView: View {
    @Binding var rankingItems: [RankingItem]
    let selectedRanking: RankingType

    var body: some View {
        List {
            if rankingItems.count > 3 {
                ForEach(3..<rankingItems.count, id: \.self) { index in
                    RankingListItemView(rank: index + 1, rankingItem: rankingItems[index], selectedRanking: selectedRanking)
                }
            }
        }
    }
}
struct RankingListItemView: View {
    let rank: Int
    let rankingItem: RankingItem
    let selectedRanking: RankingType

    var body: some View {
        HStack {
            Text("\(rank)")
                .frame(width: 32, height: 32)
                .foregroundColor(.gray)
                .overlay(
                    Circle()
                        .stroke(Color.gray, lineWidth: 2)
                )
            if let imageUrl = rankingItem.fullProfileImageUrl {
                URLImage(url: imageUrl)
                    .clipShape(Circle())
                    .frame(width: 56, height: 56)
            } else {
                Image("avatarPlain")
                    .resizable()
                    .clipShape(Circle())
                    .frame(width: 56, height: 56)
            }
            
            Text(rankingItem.username)
                .bold()
            
            Spacer()
            Spacer()
            
            if selectedRanking == .classic {
                Text("\(rankingItem.totalPoints) Pts")
                    .font(.headline)
                    .foregroundColor(.red)
            } else {
                Text(rankingItem.maxTimeTrialTime.formattedTime)
                    .font(.headline)
                    .foregroundColor(.red)
                
            }
            
            Spacer()
        }
        .padding(.vertical, 8)
    }
}

struct RankingListView_Previews: PreviewProvider {
    static var previews: some View {
        let mockRankingItems = [
            RankingItem(username: "Player1", totalPoints: 100, maxTimeTrialTime: 120, profileImageUrl: nil),
            RankingItem(username: "Player2", totalPoints: 90, maxTimeTrialTime: 110, profileImageUrl: nil),
            RankingItem(username: "Player3", totalPoints: 80, maxTimeTrialTime: 100, profileImageUrl: nil),
            RankingItem(username: "Player4", totalPoints: 70, maxTimeTrialTime: 90, profileImageUrl: nil)
        ]
        
        return Group {
            RankingListView(rankingItems: .constant(mockRankingItems), selectedRanking: .classic)
            RankingListView(rankingItems: .constant(mockRankingItems), selectedRanking: .timeTrial)
        }
    }
}
