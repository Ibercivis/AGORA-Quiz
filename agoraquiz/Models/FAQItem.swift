//
//  FAQItem.swift
//  agoraquiz
//
//  Created by Ibercivis on 28/5/24.
//

import SwiftUI

struct FAQItem: Identifiable {
    let id = UUID()
    let question: String
    let answer: String
    var isExpanded: Bool = false
}
