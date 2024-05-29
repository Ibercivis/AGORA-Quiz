//
//  LoginView.swift
//  agoraquiz
//
//  Created by Ibercivis on 7/5/24.
//
import SwiftUI

struct LoginView: View {
    @EnvironmentObject var navigationManager: NavigationManager
    @EnvironmentObject var gameService: GameService
    @StateObject private var viewModel = LoginViewModel()
    @State private var showResetPassword = false

    var body: some View {
        ZStack {
            Color.white
                .onTapGesture {
                    UIApplication.shared.endEditing(true)
                }
            
            VStack(spacing: 0){
                ZStack {
                    Image("headerPrimary")
                        .resizable()
                        .aspectRatio(contentMode: .fit)
                        .frame(width: UIScreen.main.bounds.width)
                        .clipped()
                    
                    Text("Login")
                        .font(.largeTitle)
                        .foregroundColor(.white)
                        .padding(.top, 20)
                }
                
                VStack(spacing: 20) {
                    Spacer()
                    
                    Spacer()
                    
                    CustomTextInput(placeholder: "Username", text: $viewModel.username, systemImage: "person.fill")
                    CustomTextInput(placeholder: "Password", text: $viewModel.password, systemImage: "lock.fill", isSecure: true)
                    
                    Button("Login") {
                        viewModel.navigationManager = navigationManager
                        viewModel.login()
                    }
                    .frame(maxWidth: .infinity)
                    .padding()
                    .background(Color.primaryColor)
                    .foregroundColor(.white)
                    .clipShape(RoundedRectangle(cornerRadius: 10))
                    .padding(.horizontal)
                    .padding(.top, 10)
                    
                    HStack(spacing: 20) {
                        Spacer()
                        
                        Button("Forgot password?") {
                            showResetPassword.toggle()
                        }
                        .foregroundColor(.blue)
                        
                    }.padding(.horizontal)
                    
                    Spacer()
                    
                    Spacer()
                    
                    Button("Sign Up") {
                        navigationManager.currentPage = .signUp
                    }
                    .frame(maxWidth: .infinity)
                    .padding()
                    .background(Color.black)
                    .foregroundColor(.white)
                    .clipShape(RoundedRectangle(cornerRadius: 10))
                    .padding(.horizontal)
                    
                    Spacer()
                }
                .padding()
            }
        }
        .background(Color.white)
        .sheet(isPresented: $showResetPassword) {
            ResetPasswordView(gameService: gameService)
                        .environmentObject(gameService)
                }
        .onAppear {
                    viewModel.navigationManager = navigationManager
                }
        .edgesIgnoringSafeArea(.all)
    }
}

import SwiftUI

struct LoginView_Previews: PreviewProvider {
    static var previews: some View {
        let navigationManager = NavigationManager()
        let gameService = GameService()

        LoginView()
            .environmentObject(navigationManager)
            .environmentObject(gameService)
    }
}
