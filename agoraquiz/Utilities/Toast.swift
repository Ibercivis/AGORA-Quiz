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
        ZStack(alignment: .bottom) {
                    self.presenting()
                    if isPresented {
                        VStack {
                            Spacer()
                            HStack {
                                Text(message)
                                    .foregroundColor(.white)
                                    .padding()
                            }
                            .background(Color.blue.opacity(0.8))
                            .cornerRadius(8)
                            .padding(.bottom, 150)
                            .padding(.horizontal)
                        }
                        .transition(.move(edge: .bottom))
                        .animation(.easeInOut)
                        .onTapGesture {
                            withAnimation {
                                self.isPresented = false
                            }
                        }
                    }
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
