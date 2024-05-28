//
//  CustomTextInput.swift
//  agoraquiz
//
//  Created by Ibercivis on 8/5/24.
//

import SwiftUI

struct CustomTextInput: View {
    var placeholder: String
    @Binding var text: String
    var systemImage: String
    var isSecure: Bool = false  // Para manejar campos de texto seguros (contraseñas)

    var body: some View {
        HStack {
            Image(systemName: systemImage)
                .foregroundColor(.gray)
                .padding(.leading, 10)
            if isSecure {
                SecureField(placeholder, text: $text)
                    .foregroundColor(.black)
                    .autocapitalization(.none)
                    .disableAutocorrection(true)
            } else {
                TextField(placeholder, text: $text)
                    .foregroundColor(.black)
                    .autocapitalization(.none)
                    .disableAutocorrection(true)
            }
        }
        .padding()
        .background(Color.secondary.opacity(0.2))
        .cornerRadius(10)
        .padding(.horizontal)
    }
}
