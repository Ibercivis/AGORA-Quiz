//
//  FAQView.swift
//  agoraquiz
//
//  Created by Ibercivis on 28/5/24.
//

import SwiftUI

struct FAQView: View {
    @State private var selectedQuestionId: UUID?
    let faqItems: [FAQItem] = [
        FAQItem(question: "How can I improve my scores?", answer: "Within the AGORA project, we have developed two Academies where you can find information and resources to increase your knowledge and improve your scores."),
        FAQItem(question: "How can I change my password?", answer: "To reset your password, go to the login screen and click on 'Forgot password?' or the Edit Profile section. Follow the instructions sent to your email."),
        FAQItem(question: "How can I report a bug?", answer: "The app is currently in beta mode, so it is normal for some issues to occur. If you find something that is not working properly, please contact us at info@ibercivis.es"),
        FAQItem(question: "How can I contact technical support?", answer: "You can contact technical support by emailing info@ibercivis.es"),
        FAQItem(question: "Why are the Categories and Multiplayer game modes not available?", answer: "The app will continue to be developed during the life of the project, so new functionalities will be integrated in future releases."),
        FAQItem(question: "How can I edit my profile?", answer: "To edit your profile, go to the profile screen and click on the 'Edit Profile' button."),
        FAQItem(question: "Where can I read about the Agora Project?", answer: "You can find more information about the AGORA project on our website and social media channels. All relevant links are available in the About Us - Contact Us section.")
    ]
    
    var body: some View {
        ScrollView {
            VStack(spacing: 16) {
                ForEach(faqItems) { item in
                    FAQItemView(item: item, isSelected: selectedQuestionId == item.id)
                        .onTapGesture {
                            withAnimation {
                                if selectedQuestionId == item.id {
                                    selectedQuestionId = nil
                                } else {
                                    selectedQuestionId = item.id
                                }
                            }
                        }
                }
            }
            .padding()
        }
        .navigationBarTitle("FAQ", displayMode: .inline)
    }
}

struct FAQItemView: View {
    let item: FAQItem
    let isSelected: Bool
    
    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            HStack {
                Text(item.question)
                    .foregroundColor(isSelected ? .white : .black)
                    .font(.headline)
                    .lineLimit(nil)
                    .multilineTextAlignment(.leading)
                    .frame(maxWidth: .infinity, alignment: .leading)
                Spacer()
                Image(systemName: isSelected ? "chevron.down" : "chevron.right")
                    .foregroundColor(isSelected ? .white : .gray)
            }
            .padding()
            .background(isSelected ? Color.blue : Color.white)
            .cornerRadius(isSelected ? 8 : 8, corners: isSelected ? [.topLeft, .topRight] : .allCorners)
            .shadow(radius: 4)
            
            if isSelected {
                Text(item.answer)
                    .padding()
                    .background(Color.white)
                    .cornerRadius(8, corners: [.bottomLeft, .bottomRight])
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .overlay(
                        RoundedRectangle(cornerRadius: 8)
                            .stroke(Color.gray.opacity(0.3), lineWidth: 1)
                    )
                    .padding(.bottom, 8)
                    .shadow(radius: 4)
            }
        }
        .frame(maxWidth: .infinity)
    }
}

extension View {
    func cornerRadius(_ radius: CGFloat, corners: UIRectCorner) -> some View {
        clipShape(RoundedCorner(radius: radius, corners: corners))
    }
}

struct RoundedCorner: Shape {
    var radius: CGFloat = .infinity
    var corners: UIRectCorner = .allCorners

    func path(in rect: CGRect) -> Path {
        let path = UIBezierPath(roundedRect: rect, byRoundingCorners: corners, cornerRadii: CGSize(width: radius, height: radius))
        return Path(path.cgPath)
    }
}

struct FAQView_Previews: PreviewProvider {
    static var previews: some View {
        FAQView()
    }
}
