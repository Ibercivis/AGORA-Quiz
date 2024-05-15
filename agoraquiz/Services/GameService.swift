//
//  GameService.swift
//  agoraquiz
//
//  Created by Ibercivis on 15/5/24.
//

import Foundation

class GameService {
    private let session = URLSession.shared
    
    func startGame(completion: @escaping (Result<GameResponse, Error>) -> Void) {
        guard let url = URL(string: URLs.APIPath.startGame, relativeTo: URLs.baseURL) else {
            completion(.failure(NetworkError.invalidURL))
            return
        }
        
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.addValue("application/json", forHTTPHeaderField: "Content-Type")
        
        if let token = SessionManager.shared.token {
            request.addValue("Token \(token)", forHTTPHeaderField: "Authorization")
        } else {
            completion(.failure(NetworkError.noToken))
            return
        }
        
        session.dataTask(with: request) { data, response, error in
            if let error = error {
                completion(.failure(error))
                return
            }
            
            guard let data = data else {
                completion(.failure(NetworkError.noData))
                return
            }
            
            do {
                let gameResponse = try JSONDecoder().decode(GameResponse.self, from: data)
                completion(.success(gameResponse))
            } catch {
                completion(.failure(error))
            }
        }.resume()
    }
    
    func answerQuestion(gameId: Int, questionId: Int, answerIndex: Int, completion: @escaping (Result<AnswerResponse, Error>) -> Void) {
        let urlString = String(format: URLs.APIPath.answerQuestion, gameId)
        guard let url = URL(string: urlString, relativeTo: URLs.baseURL) else {
            completion(.failure(NetworkError.invalidURL))
            return
        }
        
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.addValue("application/json", forHTTPHeaderField: "Content-Type")
        
        if let token = SessionManager.shared.token {
            request.addValue("Token \(token)", forHTTPHeaderField: "Authorization")
        } else {
            completion(.failure(NetworkError.noToken))
            return
        }
        
        let parameters: [String: Any] = [
            "question_id": questionId,
            "answer": answerIndex
        ]
        
        do {
            request.httpBody = try JSONSerialization.data(withJSONObject: parameters, options: [])
        } catch {
            completion(.failure(error))
            return
        }
        
        session.dataTask(with: request) { data, response, error in
            if let error = error {
                completion(.failure(error))
                return
            }
            
            guard let data = data else {
                completion(.failure(NetworkError.noData))
                return
            }
            
            do {
                let answerResponse = try JSONDecoder().decode(AnswerResponse.self, from: data)
                completion(.success(answerResponse))
            } catch {
                completion(.failure(error))
            }
        }.resume()
    }

    func checkForInProgressGame(completion: @escaping (Result<GameResponse, Error>) -> Void) {
        guard let url = URL(string: URLs.APIPath.checkInProgressGame, relativeTo: URLs.baseURL) else {
            completion(.failure(NetworkError.invalidURL))
            return
        }
        
        var request = URLRequest(url: url)
        request.httpMethod = "GET"
        request.addValue("application/json", forHTTPHeaderField: "Content-Type")
        
        if let token = SessionManager.shared.token {
            request.addValue("Token \(token)", forHTTPHeaderField: "Authorization")
        } else {
            completion(.failure(NetworkError.noToken))
            return
        }
        
        session.dataTask(with: request) { data, response, error in
            if let error = error {
                completion(.failure(error))
                return
            }
            
            guard let data = data else {
                completion(.failure(NetworkError.noData))
                return
            }
            
            do {
                let gameResponse = try JSONDecoder().decode(GameResponse.self, from: data)
                completion(.success(gameResponse))
            } catch {
                completion(.failure(error))
            }
        }.resume()
    }

    func abandonGame(gameId: Int, completion: @escaping (Result<Bool, Error>) -> Void) {
        let urlString = String(format: URLs.APIPath.abandonGame, gameId)
        guard let url = URL(string: urlString, relativeTo: URLs.baseURL) else {
            completion(.failure(NetworkError.invalidURL))
            return
        }
        
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.addValue("application/json", forHTTPHeaderField: "Content-Type")
        
        if let token = SessionManager.shared.token {
            request.addValue("Token \(token)", forHTTPHeaderField: "Authorization")
        } else {
            completion(.failure(NetworkError.noToken))
            return
        }
        
        session.dataTask(with: request) { data, response, error in
            if let error = error {
                completion(.failure(error))
                return
            }
            
            guard let _ = data else {
                completion(.failure(NetworkError.noData))
                return
            }
            
            completion(.success(true))
        }.resume()
    }
}

enum NetworkError: Error {
    case invalidURL
    case noData
    case noToken
}
