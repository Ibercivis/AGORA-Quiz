//
//  GameCompletedView.swift
//  agoraquiz
//
//  Created by Ibercivis on 16/5/24.
//

import SwiftUI

struct GameCompletedView: View {
    @Environment(\.presentationMode) var presentationMode
    var score: Int
    var onQuit: () -> Void
    
    var body: some View {
        ZStack {
            Color.black.opacity(0.5).edgesIgnoringSafeArea(.all)
            VStack {
                HStack {
                    Spacer()
                    Text("Game Completed!")
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

                Text("Thank you for playing! All points earned will be saved.")
                    .foregroundColor(.white)
                    .padding(.top, 20)
                    .padding(.horizontal)
                    .multilineTextAlignment(.center)
            }
        }
    }
}

struct GameCompletedView_Previews: PreviewProvider {
    static var previews: some View {
        GameCompletedView(score: 10) {
            // Acción de ejemplo para la función onQuit
            print("Quit game")
        }
    }
}
