//
//  ResetPasswordView.swift
//  agoraquiz
//
//  Created by Ibercivis on 28/5/24.
//

import SwiftUI
import Combine

struct ResetPasswordView: View {
    @Environment(\.presentationMode) var presentationMode
    @ObservedObject var viewModel: ResetPasswordViewModel
    
    init(gameService: GameService) {
        self.viewModel = ResetPasswordViewModel(gameService: gameService)
    }

    var body: some View {
        VStack(spacing: 16) {
            HStack {
                Button(action: {
                    presentationMode.wrappedValue.dismiss()
                }) {
                    Image(systemName: "arrow.left")
                        .foregroundColor(.blue)
                }
                Spacer()
                Text("Change Password")
                    .font(.headline)
                Spacer()
            }
            .padding()

            Spacer()

            Text("Enter your email address and you will receive a link to change your password.")
                .font(.subheadline)
                .multilineTextAlignment(.center)
                .padding(.horizontal)

            CustomTextInput(placeholder: "Email", text: $viewModel.email, systemImage: "envelope.fill")

            Button(action: {
                viewModel.submitChangePassword()
            }) {
                Text("Submit")
                    .foregroundColor(.white)
                    .padding()
                    .background(Color.blue)
                    .cornerRadius(8)
            }
            .padding(.top)

            Spacer()
        }
        .padding()
        .toast(isPresented: $viewModel.showToast, message: viewModel.toastMessage)
    }
}

struct ResetPasswordView_Previews: PreviewProvider {
    static var previews: some View {
        let gameService = GameService()

        ResetPasswordView(gameService: gameService)
    }
}
