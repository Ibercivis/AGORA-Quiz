//
//  SignUpResponse.swift
//  agoraquiz
//
//  Created by Ibercivis on 28/5/24.
//

import Foundation

struct SignUpResponse: Decodable {
    let username: [String]?
    let email: [String]?
}
