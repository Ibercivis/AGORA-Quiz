//
//  ClassicGameView.swift
//  agoraquiz
//
//  Created by Ibercivis on 15/5/24.
//

import SwiftUI

struct ClassicGameView: View {
    @State private var currentQuestionIndex = 1
    @State private var totalQuestions = 9
    @State private var questionText = "How much CO2 is absorbed annually by forests, in Europe and worldwide, compared to total greenhouse gas emissions?"
    @State private var selectedAnswer: Int? = nil

    var body: some View {
        VStack(spacing: 0) {
            headerView
            questionProgressView
            questionTextView
            answerOptionsView
            Spacer()
        }
        .navigationBarBackButtonHidden(true)
    }
    
    var headerView: some View {
        ZStack {
            Image("headerBackgroundImage")
                .resizable()
                .aspectRatio(contentMode: .fill)
                .frame(height: 200)
                .clipped()
            
            HStack {
                Button(action: {
                    // Acción para cerrar la vista
                }) {
                    Image(systemName: "xmark")
                        .padding()
                        .background(Color.white.opacity(0.2))
                        .clipShape(Circle())
                }
                .padding(.leading, 16)
                
                Spacer()
                
                VStack {
                    Image(systemName: "gamecontroller.fill")
                        .padding(8)
                    Text("Classic")
                        .font(.title)
                        .bold()
                        .foregroundColor(.white)
                }
                
                Spacer()
                
                Image(systemName: "star.fill")
                    .padding()
                    .background(Color.white.opacity(0.2))
                    .clipShape(Circle())
                    .padding(.trailing, 16)
            }
        }
    }
    
    var questionProgressView: some View {
        VStack {
            Text("\(currentQuestionIndex) of \(totalQuestions) questions")
                .font(.subheadline)
                .padding(.top, 8)
                .foregroundColor(.gray)
            
            ProgressBar(value: CGFloat(currentQuestionIndex) / CGFloat(totalQuestions))
                .frame(height: 10)
                .padding(.horizontal, 16)
            
            HStack {
                Image(systemName: "checkmark.circle")
                    .foregroundColor(.green)
                    .padding(.leading, 16)
                Spacer()
                Text("0")
                    .foregroundColor(.gray)
                    .padding(.trailing, 16)
            }
        }
    }
    
    var questionTextView: some View {
        Text(questionText)
            .font(.headline)
            .padding()
            .multilineTextAlignment(.center)
    }
    
    var answerOptionsView: some View {
        VStack(spacing: 16) {
            ForEach(0..<4) { index in
                AnswerOptionView(index: index, text: "Answer \(index + 1)", isSelected: selectedAnswer == index) {
                    selectedAnswer = index
                }
            }
        }
        .padding(.horizontal, 16)
    }
}

struct ProgressBar: View {
    var value: CGFloat
    
    var body: some View {
        GeometryReader { geometry in
            ZStack(alignment: .leading) {
                Rectangle().frame(width: geometry.size.width, height: geometry.size.height)
                    .opacity(0.3)
                    .foregroundColor(Color.gray)
                
                Rectangle().frame(width: min(geometry.size.width, geometry.size.width * value), height: geometry.size.height)
                    .foregroundColor(Color.blue)
                    .animation(.linear)
            }
            .cornerRadius(45.0)
        }
    }
}

struct AnswerOptionView: View {
    var index: Int
    var text: String
    var isSelected: Bool
    var action: () -> Void
    
    var body: some View {
        Button(action: action) {
            HStack {
                Text("\(Character(UnicodeScalar(65 + index)!)).")
                    .font(.title2)
                    .padding(.horizontal, 8)
                Text(text)
                    .font(.title3)
                    .padding(.horizontal, 8)
                Spacer()
            }
            .padding()
            .background(isSelected ? Color.blue.opacity(0.2) : Color.white)
            .cornerRadius(10)
            .overlay(
                RoundedRectangle(cornerRadius: 10)
                    .stroke(isSelected ? Color.blue : Color.gray, lineWidth: 2)
            )
        }
    }
}

struct ClassicGameView_Previews: PreviewProvider {
    static var previews: some View {
        ClassicGameView()
    }
}
