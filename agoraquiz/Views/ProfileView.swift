//
//  ProfileView.swift
//  agoraquiz
//
//  Created by Ibercivis on 18/5/24.
//

import SwiftUI

struct ProfileView: View {
    @EnvironmentObject var navigationManager: NavigationManager
    @EnvironmentObject var gameService: GameService
    @StateObject private var viewModel = ProfileViewModel()
    
    var body: some View {
        NavigationView {
            VStack(spacing: 0) {
                headerView
                profileSection
                statsSection
                Spacer()
            }
        }
        .edgesIgnoringSafeArea(.top)
        .navigationBarTitle("Home", displayMode: .inline)
        .navigationBarHidden(true)
        .navigationViewStyle(StackNavigationViewStyle())
        .toast(isPresented: $viewModel.showToast, message: viewModel.errorMessage ?? "")
        .onAppear {
                    viewModel.configure(gameService: gameService, navigationManager: navigationManager)
                }
        
    }
    
    var headerView: some View {
        ZStack {
            Image("headerPrimary")
                .resizable()
                .aspectRatio(contentMode: .fill)
                .frame(height: 200)
                .clipped()
            
            VStack {
                Text("Profile")
                    .font(.largeTitle)
                    .foregroundColor(.white)
                    .padding(.top, 50)
            }
        }
    }
    
    var profileSection: some View {
        VStack(spacing: 16) {
            HStack {
                VStack(spacing: 8) {
                    Image("avatarPlain")
                        .resizable()
                        .frame(width: 100, height: 100)
                        .clipShape(Circle())
                    
                    Button(action: {
                        // Logout action
                    }) {
                        Text("Logout")
                            .font(.subheadline)
                            .foregroundColor(.white)
                            .frame(width: 100, height: 32)
                            .background(Color.black)
                            .cornerRadius(16)
                    }
                }
                
                VStack(alignment: .leading, spacing: 8) {
                    Text("\(viewModel.username)")
                        .font(.headline)
                    
                    HStack {
                        Image(systemName: "star.fill")
                            .foregroundColor(.yellow)
                        Text("\(viewModel.totalPoints) Points")
                            .font(.subheadline)
                    }
                    
                    HStack {
                        Image(systemName: "trophy.fill")
                            .foregroundColor(.blue)
                        Text("Master Level")
                            .font(.subheadline)
                    }
                }
                .padding(.leading, 16)
                
                Spacer()
            }
            .padding()
            .background(Color.white)
            .cornerRadius(12)
            .shadow(radius: 4)
            .padding(.horizontal, 16)
        }
        .offset(y: -30)
    }
    
    var statsSection: some View {
        ScrollView {
            VStack(spacing: 16) {
                statsCardView(title: "Stats", items: [
                    ("Correct answers", "\(viewModel.totalCorrectAnswers)"),
                    ("Incorrect answers", "\(viewModel.totalIncorrectAnswers)"),
                    ("Games played", "\(viewModel.totalGamesPlayed)"),
                    ("Games abandoned", "\(viewModel.totalGamesAbandoned)"),
                    ("Total points", "\(viewModel.totalPoints)")
                ])
                
                statsCardView(title: "Time trial", items: [
                    ("Best game", "00"),
                    ("Best time", "00:00:00")
                ])
                
            }
            .padding(.vertical, 16)
        }
    }
    
    func statsCardView(title: String, items: [(String, String)]) -> some View {
        VStack(spacing: 8) {
            Text(title)
                .font(.headline)
                .padding()
                .frame(maxWidth: .infinity)
                .background(Color.blue)
                .foregroundColor(.white)
            
            ForEach(items, id: \.0) { item in
                HStack {
                    Text(item.0)
                        .font(.subheadline)
                        .padding(.leading, 16)
                    
                    Spacer()
                    
                    Text(item.1)
                        .font(.subheadline)
                        .padding(.trailing, 16)
                }
                .padding(.vertical, 4)
            }
        }
        .background(Color.white)
        .cornerRadius(12)
        .shadow(radius: 4)
        .padding(.horizontal, 16)
        
    }
}

struct ProfileView_Previews: PreviewProvider {
    static var previews: some View {
        ProfileView()
    }
}
