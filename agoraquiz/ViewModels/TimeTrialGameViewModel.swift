//
//  TimeTrialGameViewModel.swift
//  agoraquiz
//
//  Created by Ibercivis on 21/5/24.
//

import SwiftUI
import Combine

class TimeTrialGameViewModel: ObservableObject {
    @Published var currentQuestionIndex = 1
    @Published var totalQuestions = 9
    @Published var questionText = ""
    @Published var answers: [String] = []
    @Published var selectedAnswer: Int? = nil
    @Published var correctAnswersCount = 0
    @Published var isCorrectAnswerVisible = false
    @Published var isIncorrectAnswerVisible = false
    @Published var isPaused = false
    @Published var isGameCompleted = false
    @Published var navigateToMain = false
    @Published var timeLeft = 60

    var gameService: GameService!
    var navigationManager: NavigationManager!

    private var currentGameId: Int
    private var currentQuestion: Question?
    private var game: Game
    private var cancellables = Set<AnyCancellable>()
    private var timer: AnyCancellable?

    init(gameData: GameResponse) {
        self.game = gameData.game
        self.currentGameId = gameData.game.id
        self.currentQuestion = gameData.nextQuestion
        self.correctAnswersCount = gameData.correctAnswersCount
        self.currentQuestionIndex = gameData.currentQuestionIndex
        self.timeLeft = gameData.game.timeLeft

        if let question = self.currentQuestion {
            self.questionText = question.questionText
            self.answers = question.answers
        }
    }

    func configure(gameService: GameService, navigationManager: NavigationManager) {
        self.gameService = gameService
        self.navigationManager = navigationManager
        startTimer()
    }

    private func startTimer() {
        timer = Timer.publish(every: 1, on: .main, in: .common)
            .autoconnect()
            .sink { [weak self] _ in
                guard let self = self else { return }
                if self.timeLeft > 0 {
                    self.timeLeft -= 1
                } else {
                    self.endGame()
                }
            }
    }

    func onAnswerSelected(_ index: Int) {
        selectedAnswer = index
        guard let currentQuestion = currentQuestion else { return }
        sendAnswerToServer(selectedAnswer: index + 1)
    }

    private func sendAnswerToServer(selectedAnswer: Int) {
        guard let token = SessionManager.shared.token, let question = currentQuestion else { return }
        gameService.answerTimeTrialQuestion(gameId: game.id, questionId: question.id, answerIndex: selectedAnswer, timeLeft: timeLeft)
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
            self.timeLeft += 5
            showCorrectAnswer()
        } else {
            self.timeLeft -= 3
            showIncorrectAnswer()
        }

        if response.status == "completed" {
            isGameCompleted = true
            timer?.cancel()
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
        self.isCorrectAnswerVisible = false
        self.isIncorrectAnswerVisible = false
        self.selectedAnswer = nil
    }

    private func showCorrectAnswer() {
        isCorrectAnswerVisible = true
    }

    private func showIncorrectAnswer() {
        isIncorrectAnswerVisible = true
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

    private func endGame() {
        timer?.cancel()
        isGameCompleted = true
        gameService.finishGame(gameId: currentGameId)
            .sink(receiveCompletion: { completion in
                if case .failure(let error) = completion {
                    print("Error finishing game: \(error)")
                }
            }, receiveValue: { [weak self] _ in
                DispatchQueue.main.async {
                    self?.navigateToMain = true
                }
            })
            .store(in: &cancellables)
    }
}
