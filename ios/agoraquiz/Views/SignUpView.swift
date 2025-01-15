//
//  SignUpView.swift
//  agoraquiz
//
//  Created by Ibercivis on 8/5/24.
//

import SwiftUI

struct SignUpView: View {
    @EnvironmentObject var navigationManager: NavigationManager
    @StateObject private var viewModel = SignUpViewModel()
    @State private var showPrivacyPolicy = false
    @State private var privacyPolicyAccepted = false

    var body: some View {
        ZStack {
            Color.white
                .onTapGesture {
                    UIApplication.shared.endEditing(true)
                }
            ScrollView{
                VStack(spacing: 20) {
                    header
                    Spacer()
                    CustomTextInput(placeholder: "Username", text: $viewModel.username, systemImage: "person.fill")
                    CustomTextInput(placeholder: "Email", text: $viewModel.email, systemImage: "envelope.fill")
                    CustomTextInput(placeholder: "Password", text: $viewModel.password, systemImage: "lock.fill", isSecure: true)
                    CustomTextInput(placeholder: "Confirm Password", text: $viewModel.confirmPassword, systemImage: "lock.fill", isSecure: true)
                    if !viewModel.errorMessage.isEmpty {
                        Text(viewModel.errorMessage)
                            .foregroundColor(.red)
                            .padding(.horizontal)
                    }
                    signUpButton
                    Button("Already have an account? Login") {
                        navigationManager.currentPage = .login
                    }
                    .foregroundColor(.blue)
                    .padding(.top, 24)
                }
                .padding(.horizontal, 24)
            }
            
        }
        .background(Color.white)
        .navigationBarTitle("Sign Up", displayMode: .inline)
        .navigationBarBackButtonHidden(true)
        .edgesIgnoringSafeArea(.all)
        .sheet(isPresented: $showPrivacyPolicy) {
            PrivacyPolicyView(isPresented: $showPrivacyPolicy, isAccepted: $privacyPolicyAccepted)
             .onDisappear {
                if privacyPolicyAccepted {
                    viewModel.signUp()
                }
            }
        }
        .toast(isPresented: $viewModel.showToast, message: viewModel.toastMessage)
    }
    
    var header: some View {
        ZStack {
            Image("headerPrimary")
                .resizable()
                .aspectRatio(contentMode: .fit)
                .frame(width: UIScreen.main.bounds.width)
                .clipped()
            
            Text("Sign Up")
                .font(.largeTitle)
                .foregroundColor(.white)
                .padding(.top, 20)
        }
    }
    
    var signUpButton: some View {
        Button("Sign Up") {
            if viewModel.validateFields() {
                showPrivacyPolicy = true
                viewModel.errorMessage = ""
            }
        }
        .frame(maxWidth: .infinity)
        .padding()
        .background(Color.primaryColor)
        .foregroundColor(.white)
        .clipShape(RoundedRectangle(cornerRadius: 10))
        .padding(.horizontal)
        .padding(.top, 24)
    }
}

struct SignUpView_Previews: PreviewProvider {
    static var previews: some View {
        SignUpView()
            .environmentObject(NavigationManager())
    }
}

