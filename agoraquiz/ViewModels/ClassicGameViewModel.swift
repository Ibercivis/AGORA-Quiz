//
//  ClassicGameViewModel.swift
//  agoraquiz
//
//  Created by Ibercivis on 15/5/24.
//

import SwiftUI
import Combine

class ClassicGameViewModel: ObservableObject {
    @Published var currentQuestionIndex = 1
    @Published var totalQuestions = 9
    @Published var questionText = ""
    @Published var answers: [String] = []
    @Published var selectedAnswer: Int? = nil
    @Published var correctAnswersCount = 0
    @Published var isCorrectAnswerVisible = false
    @Published var isIncorrectAnswerVisible = false
    @Published var isClassicHeaderVisible = true
    @Published var isPaused = false
    @Published var isGameCompleted = false
    @Published var navigateToMain = false

    var gameService: GameService!
    var navigationManager: NavigationManager!
    
    private var currentGameId: Int
    private var currentQuestion: Question?
    private var game: Game
    private var cancellables = Set<AnyCancellable>()

    init(gameData: GameResponse) {
        self.game = gameData.game
        self.currentGameId = gameData.game.id
        self.currentQuestion = gameData.nextQuestion
        self.correctAnswersCount = gameData.correctAnswersCount
        self.currentQuestionIndex = gameData.currentQuestionIndex

        if let question = self.currentQuestion {
            self.questionText = question.questionText
            self.answers = question.answers
        }
    }
    
    func configure(gameService: GameService, navigationManager: NavigationManager) {
            self.gameService = gameService
            self.navigationManager = navigationManager
        }

    func startGame() {
        gameService.startGame()
            .sink(receiveCompletion: { completion in
                if case .failure(let error) = completion {
                    print("Error starting new game: \(error)")
                }
            }, receiveValue: { [weak self] gameResponse in
                DispatchQueue.main.async {
                    self?.game = gameResponse.game
                    self?.currentGameId = gameResponse.game.id
                    self?.updateUIWithQuestion(gameResponse.nextQuestion, currentQuestionIndex: 1, correctAnswersCount: 0)
                }
            })
            .store(in: &cancellables)
    }

    func onAnswerSelected(_ index: Int) {
        selectedAnswer = index
        guard let currentQuestion = currentQuestion else { return }
        sendAnswerToServer(selectedAnswer: index + 1)
    }

    private func sendAnswerToServer(selectedAnswer: Int) {
        guard let token = SessionManager.shared.token, let question = currentQuestion else { return }
        gameService.answerQuestion(gameId: game.id, questionId: question.id, answerIndex: selectedAnswer)
            .sink(receiveCompletion: { completion in
                if case .failure(let error) = completion {
                    print("Error sending answer: \(error)")
                }
            }, receiveValue: { [weak self] answerResponse in
                DispatchQueue.main.async {
                    self?.handleAnswerResponse(answerResponse)
                }
            })
            .store(in: &cancellables)
    }

    private func handleAnswerResponse(_ response: AnswerResponse) {
        if response.correct {
            self.correctAnswersCount += 1
            showCorrectAnswer()
        } else {
            showIncorrectAnswer()
        }

        if response.status == "completed" {
            isGameCompleted = true
            DispatchQueue.main.asyncAfter(deadline: .now() + 2) {
                self.navigateToMain = true
            }
        } else {
            loadNextQuestion(response: response)
        }
    }

    private func loadNextQuestion(response: AnswerResponse) {
        DispatchQueue.main.asyncAfter(deadline: .now() + 2) {
            if let nextQuestion = response.nextQuestion {
                self.updateUIWithQuestion(nextQuestion, currentQuestionIndex: response.currentQuestionIndex, correctAnswersCount: self.correctAnswersCount)
            }
        }
    }

    private func updateUIWithQuestion(_ question: Question, currentQuestionIndex: Int, correctAnswersCount: Int) {
        self.currentQuestion = question
        self.questionText = question.questionText
        self.answers = question.answers
        self.currentQuestionIndex = currentQuestionIndex
        self.correctAnswersCount = correctAnswersCount
        self.isClassicHeaderVisible = true
        self.isCorrectAnswerVisible = false
        self.isIncorrectAnswerVisible = false
        self.selectedAnswer = nil  
    }

    private func showCorrectAnswer() {
        isClassicHeaderVisible = false
        isCorrectAnswerVisible = true
    }

    private func showIncorrectAnswer() {
        isClassicHeaderVisible = false
        isIncorrectAnswerVisible = true
    }
    
    func navigateToHome(){
        self.navigationManager.currentPage = .mainTab
    }

    func quitGame() {
        guard let token = SessionManager.shared.token else { return }
        gameService.abandonGame(gameId: game.id)
            .sink(receiveCompletion: { completion in
                if case .failure(let error) = completion {
                    print("Error abandoning game: \(error)")
                }
            }, receiveValue: { [weak self] _ in
                DispatchQueue.main.async {
                    guard let strongSelf = self else { return }
                    strongSelf.navigationManager.currentPage = .mainTab
                }
            })
            .store(in: &cancellables)
    }
}
