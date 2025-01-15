//
//  FilledButton.swift
//  agoraquiz
//
//  Created by Ibercivis on 8/5/24.
//

import SwiftUI

struct FilledButton: ButtonStyle {
    func makeBody(configuration: Configuration) -> some View {
        configuration.label
            .padding()
            .background(Color.primaryColor)
            .foregroundColor(.white)
            .clipShape(RoundedRectangle(cornerRadius: 10))
            .scaleEffect(configuration.isPressed ? 0.95 : 1.0)
            .animation(.easeOut, value: configuration.isPressed)
    }
}
