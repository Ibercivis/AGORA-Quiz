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
                .frame(width: 20, height: 20)
            if isSecure {
                SecureField("", text: $text)
                    .placeholder(when: text.isEmpty) {
                        Text(placeholder).foregroundColor(.gray)
                    }
                    .padding(.leading, 10)
                    .foregroundColor(.black)
                    .autocapitalization(.none)
                    .disableAutocorrection(true)
            } else {
                TextField("", text: $text)
                    .placeholder(when: text.isEmpty) {
                        Text(placeholder).foregroundColor(.gray)
                    }
                    .padding(.leading, 10)
                    .foregroundColor(.black)
                    .autocapitalization(.none)
                    .disableAutocorrection(true)
            }
        }
        .padding()
        .background(Color.backgroundButton.opacity(0.2))
        .cornerRadius(10)
        .padding(.horizontal)
    }
}

extension View {
    func placeholder<Content: View>(
        when shouldShow: Bool,
        alignment: Alignment = .leading,
        @ViewBuilder placeholder: () -> Content) -> some View {
        ZStack(alignment: alignment) {
            placeholder().opacity(shouldShow ? 1 : 0)
            self
        }
    }
}
