//
//  ContentView.swift
//  agoraquiz
//
//  Created by Ibercivis on 7/5/24.
//

import SwiftUI

struct ContentView: View {
    @State private var isLoggedIn: Bool = SessionManager.shared.token != nil

    var body: some View {
        NavigationView {
            if isLoggedIn {
                AppTabView()
            } else {
                LoginView()
            }
        }
        .onAppear {
            isLoggedIn = SessionManager.shared.token != nil
        }
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}

