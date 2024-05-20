//
//  GameService.swift
//  agoraquiz
//
//  Created by Ibercivis on 15/5/24.
//

import Foundation
import Combine
import UIKit

class GameService: ObservableObject {
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
                // Imprimir la respuesta completa del servidor
                if let responseString = String(data: result.data, encoding: .utf8) {
                    print("Server response: \(responseString)")
                }

                guard let httpResponse = result.response as? HTTPURLResponse, httpResponse.statusCode == 200 else {
                    throw NetworkError.unexpectedResponse
                }

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
    
    func getUserProfile(token: String) -> AnyPublisher<UserProfile, Error> {
        guard let url = URL(string: URLs.APIPath.getProfile, relativeTo: URLs.baseURL) else {
            return Fail(error: NetworkError.invalidURL).eraseToAnyPublisher()
        }
            
        var request = URLRequest(url: url)
        request.httpMethod = "GET"
        request.addValue("application/json", forHTTPHeaderField: "Content-Type")
        request.addValue("Token \(token)", forHTTPHeaderField: "Authorization")
            
        return session.dataTaskPublisher(for: request)
            .tryMap { result -> UserProfile in
                if let jsonString = String(data: result.data, encoding: .utf8) {
                    print("Server response JSON: \(jsonString)")
                }
                let decoder = JSONDecoder()
                return try decoder.decode(UserProfile.self, from: result.data)
            }
            .receive(on: DispatchQueue.main)
            .eraseToAnyPublisher()
        }
    
    func uploadProfileImage(image: UIImage, token: String) -> AnyPublisher<Void, Error> {
        guard let url = URL(string: URLs.APIPath.getProfile, relativeTo: URLs.baseURL) else {
            return Fail(error: NetworkError.invalidURL).eraseToAnyPublisher()
        }
        
        var request = URLRequest(url: url)
        request.httpMethod = "PUT"
        request.setValue("Token \(token)", forHTTPHeaderField: "Authorization")
        
        let boundary = UUID().uuidString
        request.setValue("multipart/form-data; boundary=\(boundary)", forHTTPHeaderField: "Content-Type")
        
        let data = createBody(with: image, boundary: boundary)
        
        return session.uploadTaskPublisher(for: request, from: data)
            .tryMap { result in
                guard let httpResponse = result.response as? HTTPURLResponse, httpResponse.statusCode == 200 else {
                    throw NetworkError.unexpectedResponse
                }
            }
            .receive(on: DispatchQueue.main)
            .eraseToAnyPublisher()
    }
    
    func deleteProfileImage(token: String) -> AnyPublisher<Void, Error> {
        guard let url = URL(string: URLs.APIPath.getProfile, relativeTo: URLs.baseURL) else {
            return Fail(error: NetworkError.invalidURL).eraseToAnyPublisher()
        }

        var request = URLRequest(url: url)
        request.httpMethod = "PUT"
        request.setValue("Token \(token)", forHTTPHeaderField: "Authorization")

        let boundary = UUID().uuidString
        request.setValue("multipart/form-data; boundary=\(boundary)", forHTTPHeaderField: "Content-Type")

        var body = Data()
        let boundaryPrefix = "--\(boundary)\r\n"
        body.append(boundaryPrefix.data(using: .utf8)!)
        body.append("Content-Disposition: form-data; name=\"profile_image\"\r\n\r\n".data(using: .utf8)!)
        body.append("\r\n".data(using: .utf8)!)
        body.append("--\(boundary)--\r\n".data(using: .utf8)!)

        request.httpBody = body

        return session.dataTaskPublisher(for: request)
            .tryMap { result in
                guard let httpResponse = result.response as? HTTPURLResponse, httpResponse.statusCode == 200 else {
                    throw NetworkError.unexpectedResponse
                }
            }
            .receive(on: DispatchQueue.main)
            .eraseToAnyPublisher()
    }
    
    private func createBody(with image: UIImage, boundary: String) -> Data {
        var body = Data()
        
        let filename = "profile.jpg"
        let mimetype = "image/jpeg"
        let fieldName = "profile_image"
        
        body.append("--\(boundary)\r\n".data(using: .utf8)!)
        body.append("Content-Disposition: form-data; name=\"\(fieldName)\"; filename=\"\(filename)\"\r\n".data(using: .utf8)!)
        body.append("Content-Type: \(mimetype)\r\n\r\n".data(using: .utf8)!)
        body.append(image.jpegData(compressionQuality: 1.0)!)
        body.append("\r\n".data(using: .utf8)!)
        
        body.append("--\(boundary)--\r\n".data(using: .utf8)!)
        
        return body
    }
    
}

extension URLSession {
    func uploadTaskPublisher(for request: URLRequest, from bodyData: Data) -> AnyPublisher<(data: Data, response: URLResponse), URLError> {
        var request = request
        request.httpBody = bodyData
        return self.dataTaskPublisher(for: request).eraseToAnyPublisher()
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
