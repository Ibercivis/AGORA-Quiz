//
//  LoginView.swift
//  agoraquiz
//
//  Created by Ibercivis on 7/5/24.
//
import SwiftUI

struct LoginView: View {
    @StateObject private var viewModel = LoginViewModel()  // Usando StateObject para la instancia del ViewModel

    var body: some View {
        NavigationStack {
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
                    
                    // Campos de texto con estilos personalizados
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
                        viewModel.login()
                    }
                    .buttonStyle(FilledButton())
                    .padding(.top, 20)
                    
                    Spacer()
                    
                    // Enlaces de navegación
                    NavigationLink(destination: SignUpView()) {
                        Text("Sign Up")
                    }
                    .buttonStyle(FilledButton())
                }
                .padding()
            }
            .navigationDestination(isPresented: $viewModel.isLoggedIn) {
                AppTabView()
            }
        }
        .navigationViewStyle(StackNavigationViewStyle())
    }
}

// Vista previa para Xcode
struct LoginView_Previews: PreviewProvider {
    static var previews: some View {
        LoginView()
    }
}
