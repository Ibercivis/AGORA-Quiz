//
//  PrivacyPolicyView.swift
//  agoraquiz
//
//  Created by Ibercivis on 28/5/24.
//

import SwiftUI

struct PrivacyPolicyView: View {
    @Environment(\.presentationMode) var presentationMode
    @Binding var isPresented: Bool
    @Binding var isAccepted: Bool

    let privacyPolicyHTML = """
    At AGORA Quiz Game, we prioritize your privacy and are committed to ensuring that your personal information is protected and handled in accordance with the EU General Data Protection Regulation (GDPR). This Privacy Policy outlines how we collect, use, and safeguard your personal data.<br/><br/>
    <b>Information We Collect</b><br/>
    For the AGORA Quiz Game, we collect and process the following personal data:<br/>
    - <b>Username</b><br/>
    - <b>Email Address</b><br/><br/>
    <b>Purpose of Data Collection</b><br/>
    The personal data we collect is used exclusively for the following purposes:<br/>
    - To authenticate and facilitate user access to the AGORA Quiz Game.<br/>
    - To provide you with functionalities related to game statistics, points, and other game-related services.<br/>
    - To communicate with you regarding account-related matters and updates about the AGORA Quiz Game.<br/><br/>
    <b>Data Handling and Protection</b><br/>
    The data you provide is transmitted to Fundación Ibercivis and stored securely. We take appropriate security measures to protect your data from unauthorized access and ensure that it is processed in compliance with GDPR requirements. Specifically:<br/>
    - Your data will remain within the AGORA Consortium and will not be shared with third parties.<br/>
    - Your data will not be used for automated decision-making or profiling.<br/>
    - Anonymized data may be used for documentation and reporting purposes, but it will not contain any personal information.<br/><br/>
    <b>Retention of Data</b><br/>
    Your personal data will be deleted as soon as it is no longer necessary for the purposes for which it was collected. If we need to process your data for any purpose other than those specified above, we will notify you in advance and seek your consent.<br/><br/>
    <b>Your Rights</b><br/>
    Under GDPR, you have the following rights regarding your personal data:<br/>
    - <b>Right to Access and Rectification:</b> You can request to see the data we hold about you and insist on its correction if it is inaccurate or incomplete.<br/>
    - <b>Right to Erasure:</b> You can request the deletion of your data when it is no longer needed for the purposes for which it was collected, provided there are no legal obligations requiring us to retain it.<br/>
    - <b>Right to Restriction of Processing:</b> You can request the restriction of processing your data under certain circumstances.<br/>
    - <b>Right to Data Portability:</b> You can request that we return your data in a structured, commonly used, and machine-readable format.<br/>
    - <b>Right to Withdraw Consent:</b> You can withdraw your consent to the processing of your data at any time.<br/><br/>
    To exercise your rights, please contact us in writing with your name and email address at: <a href="mailto:info@ibercivis.es">info@ibercivis.es</a><br/><br/>
    <b>Changes to this Privacy Policy</b><br/>
    We may update this Privacy Policy from time to time to reflect changes in our practices or legal requirements. We will notify you of any significant changes and provide you with the updated policy.<br/><br/>
    <b>Contact Us</b><br/>
    If you have any questions or concerns about this Privacy Policy or the handling of your personal data, please contact us at: <a href="mailto:info@ibercivis.es">info@ibercivis.es</a><br/><br/>
    Thank you for trusting AGORA Quiz Game with your personal data. We are committed to safeguarding your privacy and ensuring that your experience with our game is secure and enjoyable.
    """

    var body: some View {
        VStack {
            HStack {
                Button(action: {
                    presentationMode.wrappedValue.dismiss()
                }) {
                    Image(systemName: "xmark")
                        .foregroundColor(.black)
                        .padding()
                }
                Spacer()
            }
            
            Text("Privacy Policy")
                .padding()

            HTMLTextView(htmlContent: privacyPolicyHTML)
                .frame(height: 300)
                .padding()
                .overlay(
                        RoundedRectangle(cornerRadius: 0)
                            .stroke(Color.gray, lineWidth: 1)
                    )

            Toggle(isOn: $isAccepted) {
                Text("I accept the Privacy Policy")
            }
            .padding()

            Button(action: {
                if isAccepted {
                    isPresented = false
                }
            }) {
                Text("SIGN UP")
                    .foregroundColor(.white)
                    .padding()
                    .frame(maxWidth: .infinity)
                    .background(isAccepted ? Color.primaryColor : Color.gray)
                    .cornerRadius(8)
            }
            .disabled(!isAccepted)
            .padding()
        }
        .background(Color.white)
        .cornerRadius(16)
        .padding()
    }
}

struct PrivacyPolicyView_Previews: PreviewProvider {
    static var previews: some View {
        PrivacyPolicyView(isPresented: .constant(true), isAccepted: .constant(false))
    }
}
