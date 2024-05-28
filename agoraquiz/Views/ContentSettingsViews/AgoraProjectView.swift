//
//  AgoraProjectView.swift
//  agoraquiz
//
//  Created by Ibercivis on 28/5/24.
//

import SwiftUI

struct AgoraProjectView: View {
    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 16) {
                Text("This Quiz has been co-designed and co-developed through the AGORA project.")
                
                Text("PROJECT OVERVIEW")
                    .font(.headline)
                Text("AGORA will support the overall objectives of the Mission on Adaptation to Climate Change by leveraging and step forwarding best practices, innovative approaches, policy instruments and governance mechanisms to meaningfully and effectively engage communities and regions in climate actions, accelerating and upscaling adaptation process for building a climate resilient Europe.")
                
                Text("GOALS")
                    .font(.headline)
                Text("AGORA's main ambition, beyond the state of the art, is to promote societal transformational processes in different social, economic and political contexts through transdisciplinary tools and approaches. AGORA will promote democracy, climate justice, gender equality, equity, and foster adaptive capacity and citizens’ empowerment to pro-actively support decision-making processes.")
                
                Text("CO-DESIGN & CO-CREATION")
                    .font(.headline)
                Text("Citizens, civil society organizations, academics, experts, policy-makers, entrepreneurs, and other relevant actors will be engaged in the co-design and co-creation of innovative and problem-oriented climate adaptation solutions that could be extensively adopted in Europe.")
            }
            .padding()
        }
    }
}

struct ContactUsView: View {
    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 16) {
                ContactLinkView(iconName: "facebook", text: "Facebook", url: "https://www.facebook.com/profile.php?id=100090701292038")
                ContactLinkView(iconName: "xlogo", text: "X", url: "https://x.com/AgoraAdaptation")
                ContactLinkView(iconName: "linkedin", text: "LinkedIn", url: "https://www.linkedin.com/company/adaptation-agora/")
                ContactLinkView(iconName: "link", text: "Academies", url: "https://adaptationagora.eu/digital-academies-tools/")
                ContactLinkView(iconName: "link", text: "Website", url: "https://adaptationagora.eu/")
            }
            .padding()
        }
    }
}

struct ContactLinkView: View {
    let iconName: String
    let text: String
    let url: String

    var body: some View {
        HStack {
            Image(iconName)
                .resizable()
                .frame(width: 30, height: 30)
                .foregroundColor(.blue)
                .padding(.leading, 12)
            Text(text)
                .font(.headline)
                .padding(.leading, 32)
            Spacer()
        }
        .padding(8)
        .background(RoundedRectangle(cornerRadius: 8).stroke(Color.gray, lineWidth: 1))
        .onTapGesture {
            if let url = URL(string: url) {
                UIApplication.shared.open(url)
            }
        }
    }
}
