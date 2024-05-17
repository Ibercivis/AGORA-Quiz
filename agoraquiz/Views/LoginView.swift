//
//  LoginView.swift
//  agoraquiz
//
//  Created by Ibercivis on 7/5/24.
//
import SwiftUI

struct LoginView: View {
    @EnvironmentObject var navigationManager: NavigationManager  // Inyección del NavigationManager
    @StateObject private var viewModel = LoginViewModel()

    var body: some View {
        VStack(spacing: 0) {
            // Cabecera con imagen
            ZStack {
                Image("headerPrimary")
                    .resizable()
                    .aspectRatio(contentMode: .fit)
                    .frame(width: UIScreen.main.bounds.width)
                    .clipped()
                
                // Título de la pantalla sobre la cabecera
                Text("Login")
                    .font(.largeTitle)
                    .foregroundColor(.white)
                    .padding(.top, 20)
            }
            
            VStack(spacing: 20) {
                Spacer()
                
                CustomTextInput(placeholder: "Username", text: $viewModel.username, systemImage: "person.fill")
                CustomTextInput(placeholder: "Password", text: $viewModel.password, systemImage: "lock.fill", isSecure: true)
                
                HStack(spacing: 20) {
                    Spacer()
                    
                    Button("Forgot password?") {
                        // TODO: Implementar acción
                    }
                    .foregroundColor(.blue)
                    
                }.padding(.horizontal)
                
                // Botón de login
                Button("Login") {
                    viewModel.navigationManager = navigationManager
                    viewModel.login()
                }
                .buttonStyle(FilledButton())
                .padding(.top, 20)
                
                Spacer()
                
                // Enlace de navegación a SignUpView
                Button("Sign Up") {
                    navigationManager.currentPage = .signUp  // Cambio a SignUpView utilizando NavigationManager
                }
                .buttonStyle(FilledButton())
            }
            .padding()
        }
        .onAppear {
                    viewModel.navigationManager = navigationManager
                }
    }
}

// Vista previa para Xcode
import SwiftUI

struct LoginView_Previews: PreviewProvider {
    static var previews: some View {
        let navigationManager = NavigationManager()
        LoginView()
            .environmentObject(navigationManager) 
    }
}
