//
//  ClassicGameView.swift
//  agoraquiz
//
//  Created by Ibercivis on 15/5/24.
//

import SwiftUI

struct ClassicGameView: View {
    @EnvironmentObject var navigationManager: NavigationManager
    @EnvironmentObject var gameService: GameService
    @StateObject var viewModel: ClassicGameViewModel

    var body: some View {
        VStack(spacing: 0) {
            headerView
            questionTextView
            Spacer()
            answerOptionsView
            Spacer()
        }
        .navigationBarBackButtonHidden(true)
        .fullScreenCover(isPresented: $viewModel.isPaused) {
            PauseView(score: viewModel.correctAnswersCount) {
                viewModel.quitGame()
            }
        }
        .fullScreenCover(isPresented: $viewModel.isGameCompleted) {
            GameCompletedView(score: viewModel.correctAnswersCount) {
                viewModel.quitGame()
            }
        }
        .onChange(of: viewModel.navigateToMain) { navigate, _ in
            if navigate {
                navigationManager.currentPage = .mainTab
            }
        }
        .onAppear {
                    viewModel.configure(gameService: gameService, navigationManager: navigationManager)
                }
    }

    var headerView: some View {
        ZStack {
            Image("headerPlain")
                .resizable()
                .aspectRatio(contentMode: .fit)
                .frame(width: UIScreen.main.bounds.width)
                .clipped()

            VStack {
                HStack {
                    Button(action: {
                        viewModel.isPaused.toggle()
                    }) {
                        Image(systemName: "xmark")
                            .padding()
                            .background(Color.white.opacity(0.2))
                            .foregroundColor(.white)
                            .clipShape(Circle())
                    }
                    .padding(.leading, 16)

                    Spacer()

                    Text("Classic")
                        .font(.title)
                        .bold()
                        .foregroundColor(.white)

                    Spacer()
                    Spacer()
                }

                questionProgressView
            }
        }
        .opacity(viewModel.isClassicHeaderVisible ? 1 : 0)
    }

    var questionProgressView: some View {
        VStack {
            Text("\(viewModel.currentQuestionIndex) of \(viewModel.totalQuestions) questions")
                .font(.subheadline)
                .padding(.top, 8)
                .foregroundColor(.white)

            GeometryReader { geometry in
                HStack {
                    Spacer()

                    ProgressBar(value: CGFloat(viewModel.currentQuestionIndex) / CGFloat(viewModel.totalQuestions))
                        .frame(width: geometry.size.width * 0.6, height: 10)
                        .padding(.horizontal, 16)

                    HStack(spacing: 8) {
                        Image(systemName: "checkmark.circle")
                            .foregroundColor(.green)

                        Text("\(viewModel.correctAnswersCount)")
                            .foregroundColor(.white)
                    }
                    .padding(.trailing, 24)
                }
            }
            .frame(height: 20) // Ajusta según sea necesario
        }
    }

    var questionTextView: some View {
        Text(viewModel.questionText)
            .font(.headline)
            .padding()
            .multilineTextAlignment(.center)
    }

    var answerOptionsView: some View {
        VStack(spacing: 16) {
            ForEach(viewModel.answers.indices, id: \.self) { index in
                AnswerOptionView(index: index, text: viewModel.answers[index], isSelected: viewModel.selectedAnswer == index) {
                    viewModel.onAnswerSelected(index)
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
                    .foregroundColor(Color.orange)
                    .animation(.linear, value: value)
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
        let gameService = GameService()
        let navigationManager = NavigationManager()
        let gameData = GameResponse(
            game: Game(id: 1, score: 0, status: "in-progress"),
            nextQuestion: Question(id: 1, questionText: "Sample question?", answers: ["Answer 1", "Answer 2", "Answer 3", "Answer 4"], correctAnswer: 1),
            currentQuestionIndex: 1,
            correctAnswersCount: 0
        )
        let viewModel = ClassicGameViewModel(gameData: gameData)
        viewModel.configure(gameService: gameService, navigationManager: navigationManager)

        return ClassicGameView(viewModel: viewModel)
            .environmentObject(gameService)
            .environmentObject(navigationManager)
    }
}
