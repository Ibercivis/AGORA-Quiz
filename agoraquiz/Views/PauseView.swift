//
//  PauseView.swift
//  agoraquiz
//
//  Created by Ibercivis on 16/5/24.
//

import SwiftUI

struct PauseView: View {
    @Environment(\.presentationMode) var presentationMode
    var score: Int
    var onQuit: () -> Void

    var body: some View {
        ZStack {
            Color.black.opacity(0.5).edgesIgnoringSafeArea(.all)
            VStack {
                HStack {
                    Button(action: {
                        presentationMode.wrappedValue.dismiss()
                    }) {
                        Image(systemName: "arrow.left")
                            .padding()
                            .background(Color.white)
                            .clipShape(Circle())
                            .foregroundColor(.black)
                    }
                    Text("Pause")
                        .font(.title)
                        .bold()
                        .foregroundColor(.white)
                    Spacer()
                }
                .padding()

                Spacer()

                VStack {
                    Text("Current points!")
                        .foregroundColor(.white)
                        .padding(.bottom, 10)

                    ZStack {
                        Circle()
                            .fill(Color.white)
                            .frame(width: 100, height: 100)
                        Text("\(score)")
                            .font(.largeTitle)
                            .foregroundColor(.black)
                    }
                }

                Spacer()

                Button(action: {
                    onQuit()
                    presentationMode.wrappedValue.dismiss()
                }) {
                    Text("Quit")
                        .padding()
                        .frame(maxWidth: .infinity)
                        .background(Color.white)
                        .foregroundColor(.black)
                        .cornerRadius(10)
                }
                .padding(.horizontal)

                Text("Are you sure you want to leave the game? All points earned will be lost.")
                    .foregroundColor(.white)
                    .padding(.top, 20)
                    .padding(.horizontal)
                    .multilineTextAlignment(.center)
            }
        }
    }
}

struct PauseView_Previews: PreviewProvider {
    static var previews: some View {
        PauseView(score: 10) {
            // Acción de ejemplo para la función onQuit
            print("Quit game")
        }
    }
}
