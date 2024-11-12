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
            if let attributedString = try? NSMutableAttributedString(data: data, options: options, documentAttributes: nil) {
                let range = NSRange(location: 0, length: attributedString.length)
                
                // Enumerar todos los atributos y aplicar la fuente predeterminada de iOS manteniendo otros estilos
                attributedString.enumerateAttribute(.font, in: range, options: []) { value, range, _ in
                    if let currentFont = value as? UIFont {
                        let newFontDescriptor = currentFont.fontDescriptor.withFamily(UIFont.preferredFont(forTextStyle: .body).familyName)
                        let newFont = UIFont(descriptor: newFontDescriptor, size: 12)
                        attributedString.addAttribute(.font, value: newFont, range: range)
                    }
                }
                
                uiView.attributedText = attributedString
            }
        }
    }
}
