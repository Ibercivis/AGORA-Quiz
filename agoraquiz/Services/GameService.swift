//
//  GameService.swift
//  agoraquiz
//
//  Created by Ibercivis on 15/5/24.
//

import Foundation
import Combine

class GameService {
    private let session = URLSession.shared

    func startGame() -> AnyPublisher<GameResponse, Error> {
        guard let url = URL(string: URLs.APIPath.startGame, relativeTo: URLs.baseURL) else {
            return Fail(error: NetworkError.invalidURL).eraseToAnyPublisher()
        }

        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.addValue("application/json", forHTTPHeaderField: "Content-Type")

        if let token = SessionManager.shared.token {
            request.addValue("Token \(token)", forHTTPHeaderField: "Authorization")
            print("Token: \(token)")  // Imprimir el token para depuración
        } else {
            return Fail(error: NetworkError.noToken).eraseToAnyPublisher()
        }

        return session.dataTaskPublisher(for: request)
            .tryMap { result -> GameResponse in
                guard let httpResponse = result.response as? HTTPURLResponse else {
                    throw NetworkError.invalidURL
                }
                print("HTTP Status Code: \(httpResponse.statusCode)")
                
                // Imprimir la respuesta completa del servidor
                if let responseString = String(data: result.data, encoding: .utf8) {
                    print("Server response: \(responseString)")
                }

                if httpResponse.statusCode == 200 {
                    let decoder = JSONDecoder()
                    let gameResponse = try decoder.decode(GameResponse.self, from: result.data)
                    print("Game started successfully: \(gameResponse)")
                    return gameResponse
                } else {
                    if let serverError = try? JSONDecoder().decode(ServerError.self, from: result.data) {
                        print("Server Error: \(serverError.message)")
                        throw NetworkError.serverError(serverError.message)
                    } else {
                        print("Unexpected server response: \(String(data: result.data, encoding: .utf8) ?? "")")
                        throw NetworkError.unexpectedResponse
                    }
                }
            }
            .receive(on: DispatchQueue.main)
            .eraseToAnyPublisher()
    }

    func answerQuestion(gameId: Int, questionId: Int, answerIndex: Int) -> AnyPublisher<AnswerResponse, Error> {
        let urlString = String(format: URLs.APIPath.answerQuestion, gameId)
        guard let url = URL(string: urlString, relativeTo: URLs.baseURL) else {
            return Fail(error: NetworkError.invalidURL).eraseToAnyPublisher()
        }

        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.addValue("application/json", forHTTPHeaderField: "Content-Type")

        if let token = SessionManager.shared.token {
            request.addValue("Token \(token)", forHTTPHeaderField: "Authorization")
            print("Token: \(token)")  // Imprimir el token para depuración
        } else {
            return Fail(error: NetworkError.noToken).eraseToAnyPublisher()
        }

        let parameters: [String: Any] = [
            "question_id": questionId,
            "answer": answerIndex
        ]

        do {
            request.httpBody = try JSONSerialization.data(withJSONObject: parameters, options: [])
        } catch {
            return Fail(error: error).eraseToAnyPublisher()
        }

        return session.dataTaskPublisher(for: request)
            .tryMap { result -> AnswerResponse in
                let decoder = JSONDecoder()
                return try decoder.decode(AnswerResponse.self, from: result.data)
            }
            .receive(on: DispatchQueue.main)
            .eraseToAnyPublisher()
    }

    func checkForInProgressGame() -> AnyPublisher<GameResponse, Error> {
        guard let url = URL(string: URLs.APIPath.checkInProgressGame, relativeTo: URLs.baseURL) else {
            return Fail(error: NetworkError.invalidURL).eraseToAnyPublisher()
        }

        var request = URLRequest(url: url)
        request.httpMethod = "GET"
        request.addValue("application/json", forHTTPHeaderField: "Content-Type")

        if let token = SessionManager.shared.token {
            request.addValue("Token \(token)", forHTTPHeaderField: "Authorization")
            print("Token: \(token)")  // Imprimir el token para depuración
        } else {
            return Fail(error: NetworkError.noToken).eraseToAnyPublisher()
        }

        return session.dataTaskPublisher(for: request)
            .tryMap { result -> GameResponse in
                let decoder = JSONDecoder()
                return try decoder.decode(GameResponse.self, from: result.data)
            }
            .receive(on: DispatchQueue.main)
            .eraseToAnyPublisher()
    }

    func abandonGame(gameId: Int) -> AnyPublisher<Bool, Error> {
        let urlString = String(format: URLs.APIPath.abandonGame, gameId)
        guard let url = URL(string: urlString, relativeTo: URLs.baseURL) else {
            return Fail(error: NetworkError.invalidURL).eraseToAnyPublisher()
        }

        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.addValue("application/json", forHTTPHeaderField: "Content-Type")

        if let token = SessionManager.shared.token {
            request.addValue("Token \(token)", forHTTPHeaderField: "Authorization")
            print("Token: \(token)")  // Imprimir el token para depuración
        } else {
            return Fail(error: NetworkError.noToken).eraseToAnyPublisher()
        }

        return session.dataTaskPublisher(for: request)
            .tryMap { _ in true }
            .receive(on: DispatchQueue.main)
            .eraseToAnyPublisher()
    }
}

enum NetworkError: Error {
    case invalidURL
    case noData
    case noToken
    case unexpectedResponse
    case serverError(String)
}

struct ServerError: Decodable {
    let message: String
}
