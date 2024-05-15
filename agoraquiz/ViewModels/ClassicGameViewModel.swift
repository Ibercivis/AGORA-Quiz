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

    private var gameService: GameService
    private var currentGameId: Int = 0
    private var currentQuestion: Question?

    init(gameService: GameService) {
        self.gameService = gameService
        startGame()
    }

    func startGame() {
        gameService.startGame { result in
            switch result {
            case .success(let gameResponse):
                DispatchQueue.main.async {
                    self.currentGameId = gameResponse.game.id
                    self.totalQuestions = gameResponse.totalQuestions
                    self.updateUIWithQuestion(gameResponse.nextQuestion, currentQuestionIndex: 1, correctAnswersCount: 0)
                }
            case .failure(let error):
                // Manejar el error
                print(error)
            }
        }
    }

    func onAnswerSelected(_ index: Int) {
        selectedAnswer = index
        if currentQuestion == nil { return }
        sendAnswerToServer(selectedAnswer: index + 1)
    }

    private func sendAnswerToServer(selectedAnswer: Int) {
        guard let currentQuestion = currentQuestion else { return }

        gameService.answerQuestion(gameId: currentGameId, questionId: currentQuestion.id, answerIndex: selectedAnswer) { result in
            switch result {
            case .success(let response):
                DispatchQueue.main.async {
                    self.handleAnswerResponse(response)
                }
            case .failure(let error):
                // Manejar el error
                print(error)
            }
        }
    }

    private func handleAnswerResponse(_ response: AnswerResponse) {
        if response.correct {
            self.correctAnswersCount += 1
            showCorrectAnswer()
        } else {
            showIncorrectAnswer()
        }

        if response.status == "completed" {
            // Manejar la finalización del juego
        } else {
            loadNextQuestion(response: response)
        }
    }

    private func loadNextQuestion(response: AnswerResponse) {
        DispatchQueue.main.asyncAfter(deadline: .now() + 2) {
            self.updateUIWithQuestion(response.nextQuestion, currentQuestionIndex: response.currentQuestionIndex, correctAnswersCount: self.correctAnswersCount)
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
    }

    private func showCorrectAnswer() {
        isClassicHeaderVisible = false
        isCorrectAnswerVisible = true
    }

    private func showIncorrectAnswer() {
        isClassicHeaderVisible = false
        isIncorrectAnswerVisible = true
    }
}
