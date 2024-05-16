//
//  Toast.swift
//  agoraquiz
//
//  Created by Ibercivis on 16/5/24.
//

import SwiftUI

struct Toast<Presenting>: View where Presenting: View {
    @Binding var isPresented: Bool
    let presenting: () -> Presenting
    let message: String

    var body: some View {
        if isPresented {
            return VStack {
                self.presenting()
                Text(message)
                    .padding()
                    .background(Color.black)
                    .foregroundColor(.white)
                    .cornerRadius(8)
                    .transition(.slide)
                    .onTapGesture {
                        withAnimation {
                            self.isPresented = false
                        }
                    }
            }
            .eraseToAnyView()
        } else {
            return presenting().eraseToAnyView()
        }
    }
}

extension View {
    func toast(isPresented: Binding<Bool>, message: String) -> some View {
        Toast(isPresented: isPresented, presenting: { self }, message: message)
    }
}

extension View {
    func eraseToAnyView() -> AnyView {
        AnyView(self)
    }
}
