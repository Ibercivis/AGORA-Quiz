//
//  TimeTrialGameView.swift
//  agoraquiz
//
//  Created by Ibercivis on 21/5/24.
//

import SwiftUI

struct TimeTrialGameView: View {
    @EnvironmentObject var navigationManager: NavigationManager
    @EnvironmentObject var gameService: GameService
    @StateObject var viewModel: TimeTrialGameViewModel

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
        .edgesIgnoringSafeArea(.all)
    }

    var headerView: some View {
        Group {
            if viewModel.isCorrectAnswerVisible {
                HeaderCorrect()
            } else if viewModel.isIncorrectAnswerVisible {
                HeaderIncorrect()
            } else {
                HeaderRegular(isPaused: $viewModel.isPaused, currentQuestionIndex: viewModel.currentQuestionIndex, totalQuestions: viewModel.totalQuestions, correctAnswersCount: viewModel.correctAnswersCount, timeLeft: viewModel.timeLeft)
            }
        }
    }

    struct HeaderRegular: View {
        @Binding var isPaused: Bool
        var currentQuestionIndex: Int
        var totalQuestions: Int
        var correctAnswersCount: Int
        var timeLeft: Int

        var body: some View {
            ZStack {
                Image("headerPlain")
                    .resizable()
                    .aspectRatio(contentMode: .fit)
                    .frame(width: UIScreen.main.bounds.width)
                    .clipped()

                VStack {
                    HStack {
                        Button(action: {
                            isPaused.toggle()
                        }) {
                            Image(systemName: "xmark")
                                .padding()
                                .background(Color.white.opacity(0.2))
                                .foregroundColor(.white)
                                .clipShape(Circle())
                        }
                        .padding(.leading, 16)
                        
                        Spacer()

                        Image("ic_timetrial_title")

                        Text("Time Trial")
                            .font(.title)
                            .bold()
                            .foregroundColor(.white)
                            .padding(.trailing, 16)

                        Spacer()
                        
                        Spacer()

                        
                    }

                    questionProgressView
                }
            }
        }

        var questionProgressView: some View {
            VStack {
                Text("\(currentQuestionIndex) of \(totalQuestions) questions")
                    .font(.subheadline)
                    .padding(.top, 8)
                    .foregroundColor(.white)

                GeometryReader { geometry in
                    HStack {
                        Text(formatTime(seconds: timeLeft))
                            .foregroundColor(.white)
                            .padding(.leading, 36)

                        ProgressBar(value: CGFloat(currentQuestionIndex - 1) / CGFloat(totalQuestions))
                            .frame(width: geometry.size.width * 0.5, height: 10)
                            .padding(.horizontal, 4)

                        HStack(spacing: 8) {
                            Image(systemName: "checkmark.circle")
                                .foregroundColor(.green)

                            Text("\(correctAnswersCount)")
                                .foregroundColor(.white)
                        }
                        .padding(.trailing, 24)
                    }
                }
                .frame(height: 20) // Ajusta según sea necesario
            }
        }

        func formatTime(seconds: Int) -> String {
            let minutes = seconds / 60
            let remainingSeconds = seconds % 60
            return String(format: "%02d:%02d", minutes, remainingSeconds)
        }
    }

    struct HeaderCorrect: View {
        var body: some View {
            ZStack {
                Image("headerCorrect")
                    .resizable()
                    .aspectRatio(contentMode: .fit)
                    .frame(width: UIScreen.main.bounds.width)
                    .clipped()

                VStack {
                    Text("Correct!")
                        .font(.title)
                        .foregroundColor(.white)
                        .padding()
                    Text("+5 Pts")
                        .font(.headline)
                        .foregroundColor(.black)
                        .padding([.leading, .trailing], 20)
                        .padding([.top, .bottom], 10)
                        .background(Color.white)
                        .cornerRadius(20)
                }
            }
        }
    }

    struct HeaderIncorrect: View {
        var body: some View {
            ZStack {
                Image("headerIncorrect")
                    .resizable()
                    .aspectRatio(contentMode: .fit)
                    .frame(width: UIScreen.main.bounds.width)
                    .clipped()

                VStack {
                    Text("Incorrect")
                        .font(.title)
                        .foregroundColor(.white)
                        .padding()
                    Text("-3 Pts")
                        .font(.headline)
                        .foregroundColor(.black)
                        .padding([.leading, .trailing], 20)
                        .padding([.top, .bottom], 10)
                        .background(Color.white)
                        .cornerRadius(20)
                }
            }
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
                AnswerOptionView(index: index,
                                 text: viewModel.answers[index],
                                 isSelected: viewModel.selectedAnswer == index,
                                 isCorrect: viewModel.isCorrectAnswerVisible && viewModel.selectedAnswer == index,
                                 isIncorrect: viewModel.isIncorrectAnswerVisible && viewModel.selectedAnswer == index) {
                    viewModel.onAnswerSelected(index)
                }
            }
        }
        .padding(.horizontal, 16)
    }

}

struct TimeTrialGameView_Previews: PreviewProvider {
    static var previews: some View {
        let gameService = GameService()
        let navigationManager = NavigationManager()
        let gameData = GameResponse(
            game: Game(id: 1, score: 0, status: "in-progress", gameType: "time-trial", timeLeft: 60),
            nextQuestion: Question(id: 1, questionText: "Sample question?", answers: ["Answer 1", "Answer 2", "Answer 3", "Answer 4"], correctAnswer: 1),
            currentQuestionIndex: 1,
            correctAnswersCount: 0
        )
        let viewModel = TimeTrialGameViewModel(gameData: gameData)
        viewModel.configure(gameService: gameService, navigationManager: navigationManager)

        return TimeTrialGameView(viewModel: viewModel)
            .environmentObject(gameService)
            .environmentObject(navigationManager)
    }
}
