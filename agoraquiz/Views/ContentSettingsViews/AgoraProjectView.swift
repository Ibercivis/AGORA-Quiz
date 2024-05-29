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
                Text("The Agora Quiz game has been co-designed and co-developed within the AGORA project.")
                    .font(.subheadline)
                
                Text("PROJECT OVERVIEW")
                    .foregroundColor(Color.primaryColor)
                    .font(.headline)

                Text("AGORA is a HORIZON Europe project started in January 2023. It supports the EU Mission on Adaptation to Climate Change. AGORA will support the overall objectives of the Mission on Adaptation to Climate Change by leveraging and step forwarding best practices, innovative approaches, policy instruments and governance mechanisms to meaningfully and effectively engage communities and regions in climate actions, accelerating and upscaling adaptation process for building a climate resilient Europe. ")
                    .font(.subheadline)
                
                Text("GOALS")
                    .foregroundColor(Color.primaryColor)
                    .font(.headline)
                Text("AGORA's main ambition, beyond the state of the art, is to promote societal transformational processes in different social, economic and political contexts through transdisciplinary tools and approaches. AGORA will promote democracy, climate justice, gender equality, equity, and foster adaptive capacity and citizens’ empowerment to pro-actively support decision-making processes.")
                    .font(.subheadline)
                
                Text("CO-DESIGN & CO-CREATION")
                    .foregroundColor(Color.primaryColor)
                    .font(.headline)
                Text("Citizens, civil society organizations, academics, experts, policy-makers, entrepreneurs, and other relevant actors will be engaged in the co-design and co-creation of innovative and problem oriented climate adaptation solutions that could be extensively adopted in Europe. AGORA project will take into consideration the ongoing social changes the awareness that there is no “one size fits all” solution.")
                    .font(.subheadline)
                
                Text("PILOT STUDIES")
                    .foregroundColor(Color.primaryColor)
                    .font(.headline)
                Text("A set of pilot regions will constitute the co-production arena to co-design, co-develop and co-implement climate adaptation solutions. AGORA will also develop a roadmap for transformational change and upscale of citizen engagement, for transferability of effective policy instruments and for ensuring long term legacy.")
                    .font(.subheadline)
                
                HStack{
                    Spacer()
                    Image("fundedbyeu_color")
                        .resizable()
                        .scaledToFit()
                        .frame(width: 200, height: 200)
                    Spacer()
                }
                
                Spacer()
                
                Spacer()
                
                
            }
            .padding()
        }
    }
}

struct ContactUsView: View {
    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 16) {
                Spacer()
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
