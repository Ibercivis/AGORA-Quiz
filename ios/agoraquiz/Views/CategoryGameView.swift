//
//  CategoryGameView.swift
//  agoraquiz
//
//  Created by Ibercivis on 12/11/24.
//

import SwiftUI

struct CategoryGameView: View {
    @EnvironmentObject var navigationManager: NavigationManager
    @EnvironmentObject var gameService: GameService
    @StateObject var viewModel: CategoryGameViewModel

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
                viewModel.navigateToHome()
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
        Group {
            if viewModel.isCorrectAnswerVisible {
                HeaderCorrect(reference: viewModel.reference)
            } else if viewModel.isIncorrectAnswerVisible {
                HeaderIncorrect(reference: viewModel.reference)
            } else {
                HeaderRegular(isPaused: $viewModel.isPaused, currentQuestionIndex: viewModel.currentQuestionIndex, totalQuestions: viewModel.totalQuestions, correctAnswersCount: viewModel.correctAnswersCount)
            }
        }
    }
    
    struct HeaderRegular: View {
        @Binding var isPaused: Bool
        var currentQuestionIndex: Int
        var totalQuestions: Int
        var correctAnswersCount: Int

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
                        
                        Text("Category Game")
                            .font(.title)
                            .bold()
                            .foregroundColor(.white)
                            .padding(.trailing, 16)
                        
                        Spacer()
                    }
                    
                    questionProgressView
                }
            }
            .edgesIgnoringSafeArea(.all)
        }

        var questionProgressView: some View {
            VStack {
                Text("\(currentQuestionIndex) of \(totalQuestions) questions")
                    .font(.subheadline)
                    .padding(.top, 8)
                    .foregroundColor(.white)

                GeometryReader { geometry in
                    HStack {
                        Spacer()

                        ProgressBar(value: CGFloat(currentQuestionIndex - 1) / CGFloat(totalQuestions))
                            .frame(width: geometry.size.width * 0.6, height: 10)
                            .padding(.horizontal, 16)

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

struct HeaderCorrect: View {
    var reference: String

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
                    .padding(.horizontal, 20)
                    .padding(.vertical, 10)
                    .background(Color.white)
                    .cornerRadius(20)

                Text("Reference: \(reference)")
                    .font(.footnote)
                    .italic()
                    .foregroundColor(.white)
                    .padding(.top, 8)
            }
        }
    }
}

struct HeaderIncorrect: View {
    var reference: String

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
                    .padding(.horizontal, 20)
                    .padding(.vertical, 10)
                    .background(Color.white)
                    .cornerRadius(20)

                Text("Reference: \(reference)")
                    .font(.footnote)
                    .italic()
                    .foregroundColor(.white)
                    .padding(.top, 8)
            }
        }
    }
}

struct CategoryGameView_Previews: PreviewProvider {
    static var previews: some View {
        let gameService = GameService()
        let navigationManager = NavigationManager()
        
        let sampleQuestion = Question(
            id: 1,
            questionText: "What is the main cause of climate change?",
            answers: ["Greenhouse gases", "Deforestation", "Pollution", "Overpopulation"],
            correctAnswer: 0,
            reference: "IPCC Report 2021"
        )
        
        let gameData = GameResponse(
            game: Game(id: 1, score: 0, status: "in-progress", gameType: "category", timeLeft: 60, questions: [sampleQuestion]),
            nextQuestion: sampleQuestion,
            currentQuestionIndex: 1,
            correctAnswersCount: 0
        )
        
        let viewModel = CategoryGameViewModel(gameData: gameData)
        viewModel.configure(gameService: gameService, navigationManager: navigationManager)

        return CategoryGameView(viewModel: viewModel)
            .environmentObject(navigationManager)
            .environmentObject(gameService)
    }
}
