//
//  HTMLTextView.swift
//  agoraquiz
//
//  Created by Ibercivis on 28/5/24.
//

import SwiftUI

struct HTMLTextView: UIViewRepresentable {
    let htmlContent: String
    
    func makeUIView(context: Context) -> UITextView {
        let textView = UITextView()
        textView.isEditable = false
        textView.dataDetectorTypes = .link
        textView.isScrollEnabled = true
        textView.setContentHuggingPriority(.defaultLow, for: .horizontal)
        textView.setContentHuggingPriority(.defaultLow, for: .vertical)
        return textView
    }
    
    func updateUIView(_ uiView: UITextView, context: Context) {
        if let data = htmlContent.data(using: .utf8) {
            let options: [NSAttributedString.DocumentReadingOptionKey: Any] = [
                .documentType: NSAttributedString.DocumentType.html,
                .characterEncoding: String.Encoding.utf8.rawValue
            ]
            if let attributedString = try? NSAttributedString(data: data, options: options, documentAttributes: nil) {
                uiView.attributedText = attributedString
            }
        }
    }
}
