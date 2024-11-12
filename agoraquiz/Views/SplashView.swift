//
//  SplashView.swift
//  agoraquiz
//
//  Created by Ibercivis on 29/5/24.
//

import SwiftUI

struct SplashView: View {
    @EnvironmentObject var navigationManager: NavigationManager

    var body: some View {
        VStack {
            Spacer()
            Spacer()
            Image("agora_logo")
                .resizable()
                .scaledToFit()
                .frame(width: 150, height: 150)
            Text("AGORA Quiz")
                .font(.largeTitle)
                .foregroundColor(.white)
            Spacer()
            Spacer()
            Spacer()
            Image("fundedbyeu_outline")
                .resizable()
                .scaledToFit()
                .frame(width: 150, height: 150)
            Spacer()
        }
        .frame(maxWidth: .infinity)
        .background(Color.primaryColor)
        .ignoresSafeArea(.all)
        
    }
}

struct SplashView_Previews: PreviewProvider {
    static var previews: some View {
        SplashView()
            .environmentObject(NavigationManager())
    }
}
