//
//  UIApplication.swift
//  agoraquiz
//
//  Created by Ibercivis on 29/5/24.
//

import SwiftUI

extension UIApplication {
    func endEditing(_ force: Bool) {
        self.sendAction(#selector(UIResponder.resignFirstResponder), to: nil, from: nil, for: nil)
    }
}
