//
//  SettingsView.swift
//  agoraquiz
//
//  Created by Ibercivis on 27/5/24.
//

import SwiftUI

struct SettingsView: View {
    @ObservedObject var viewModel: ProfileViewModel
    @Environment(\.presentationMode) var presentationMode
    @State private var showImagePicker = false
    @State private var showActionSheet = false
    @State private var image: UIImage?
    @State private var emailForPasswordReset: String = ""

    var body: some View {
        VStack {
            if viewModel.isEditingProfile {
                editProfileView
            } else if viewModel.isChangingPassword {
                changePasswordView
            } else if viewModel.isViewingAboutUs {
                aboutUsView
            } else if viewModel.isViewingFAQ {
                faqView
            } else {
                settingsContent
            }
        }
        .frame(maxWidth: .infinity)
        .frame(height: UIScreen.main.bounds.height * 0.8)
        .background(Color.white)
        .cornerRadius(16)
        .shadow(radius: 4)
        .gesture(
            DragGesture().onEnded { value in
                if value.translation.height > 100 {
                    withAnimation {
                        viewModel.isSettingsViewVisible = false
                    }
                }
            }
        )
        .toast(isPresented: $viewModel.showToast, message: viewModel.errorMessage ?? "")
        .actionSheet(isPresented: $showActionSheet) {
            ActionSheet(title: Text("Profile Image"), message: Text("Choose an option"), buttons: actionSheetButtons())
        }
        .sheet(isPresented: $showImagePicker, onDismiss: loadImage) {
            ImagePicker(image: self.$image)
        }
    }
    
    var settingsContent: some View {
        VStack(spacing: 16) {
            
            Spacer()
            
            Button(action: {
                showEditProfileView()
            }) {
                HStack {
                    Image(systemName: "person.crop.circle")
                        .foregroundColor(.blue)
                    Text("Edit Profile")
                    Spacer()
                    Image(systemName: "chevron.right")
                        .foregroundColor(.gray)
                }
                .padding()
                .background(Color.white)
                .cornerRadius(8)
            }

            Button(action: {
                viewModel.isChangingPassword = true
            }) {
                HStack {
                    Image(systemName: "lock.circle")
                        .foregroundColor(.blue)
                    Text("Change Password")
                    Spacer()
                    Image(systemName: "chevron.right")
                        .foregroundColor(.gray)
                }
                .padding()
                .background(Color.white)
                .cornerRadius(8)
            }

            Button(action: {
                viewModel.isViewingAboutUs = true
            }) {
                HStack {
                    Image(systemName: "info.circle")
                        .foregroundColor(.blue)
                    Text("About Us")
                    Spacer()
                    Image(systemName: "chevron.right")
                        .foregroundColor(.gray)
                }
                .padding()
                .background(Color.white)
                .cornerRadius(8)
            }

            Button(action: {
                viewModel.isViewingFAQ = true
            }) {
                HStack {
                    Image(systemName: "questionmark.circle")
                        .foregroundColor(.blue)
                    Text("FAQ")
                    Spacer()
                    Image(systemName: "chevron.right")
                        .foregroundColor(.gray)
                }
                .padding()
                .background(Color.white)
                .cornerRadius(8)
            }

            Button(action: {
                viewModel.logout()
                presentationMode.wrappedValue.dismiss()
            }) {
                HStack {
                    Image(systemName: "arrow.right.circle")
                        .foregroundColor(.red)
                    Text("Logout")
                        .foregroundColor(.red)
                    Spacer()
                }
                .padding()
                .background(Color.white)
                .cornerRadius(8)
            }
            
            Spacer()
            
            Spacer()
            
            Spacer()
            }
        }

        var editProfileView: some View {
            ScrollView {
                VStack {
                    Spacer()
                    
                    HStack {
                        Button(action: {
                            withAnimation {
                                viewModel.isEditingProfile = false
                            }
                        }) {
                            Image(systemName: "arrow.left")
                                .foregroundColor(.blue)
                        }
                        Spacer()
                    }
                    .padding()
                    
                    HStack{
                        Spacer()
                        Text("Edit Profile")
                            .font(.headline)
                        Spacer()
                    }
                    .padding()
                    
                    VStack(spacing: 16) {
                        if let profileImageUrl = viewModel.profileImageUrl, let url = URL(string: profileImageUrl) {
                            URLImage(url: url)
                                .frame(width: 100, height: 100)
                                .clipShape(Circle())
                                .onTapGesture {
                                    self.showActionSheet = true
                                }
                        } else {
                            Image("avatarPlain")
                                .resizable()
                                .frame(width: 100, height: 100)
                                .clipShape(Circle())
                                .onTapGesture {
                                    self.showActionSheet = true
                                }
                        }
                        
                        Spacer()
                        
                        CustomTextInput(placeholder: "Username", text: $viewModel.usernameEditText, systemImage: "person.fill")
                        
                        
                        CustomTextInput(placeholder: "Email", text: $viewModel.emailEditText, systemImage: "envelope.fill")
                        
                        Spacer()
                        
                        Button(action: {
                            viewModel.updateProfile()
                        }) {
                            Text("Save Changes")
                                .foregroundColor(.white)
                                .padding()
                                .background(Color.blue)
                                .cornerRadius(8)
                        }
                        .padding(.top)
                        
                        Spacer()
                        
                        Spacer()
                    }
                    .padding()
                    
                }
            }
        }
    
    var changePasswordView: some View {
            ScrollView {
                VStack {
                    HStack{
                        Button(action: {
                            withAnimation {
                                viewModel.isChangingPassword = false
                            }
                        }) {
                            Image(systemName: "arrow.left")
                                .foregroundColor(.blue)
                        }
                        Spacer()
                    }
                    .padding()
                    HStack {
                        Spacer()
                        Text("Change Password")
                            .font(.headline)
                        Spacer()
                    }
                    .padding()

                    VStack(spacing: 16) {
                        Spacer()
                        
                        Text("Enter your email address and you will receive a link to change your password.")
                            .padding(.horizontal)
                        
                        CustomTextInput(placeholder: "Email", text: $emailForPasswordReset, systemImage: "envelope.fill")
                        
                        Spacer()
                        
                        Button(action: {
                            viewModel.submitChangePassword(email: emailForPasswordReset)
                        }) {
                            Text("Submit")
                                .foregroundColor(.white)
                                .padding()
                                .background(Color.blue)
                                .cornerRadius(8)
                        }
                        .padding(.top)
                    }
                    .padding()
                }
            }
        }
    
    var aboutUsView: some View {
            VStack {
                HStack{
                    Button(action: {
                        withAnimation {
                            viewModel.isViewingAboutUs = false
                        }
                    }) {
                        Image(systemName: "arrow.left")
                            .foregroundColor(.blue)
                    }
                    Spacer()
                }
                .padding()
                HStack {
                    Spacer()
                    Text("About Us")
                        .font(.headline)
                    Spacer()
                }
                .padding()

                AboutUsView()
            }
        }
    
    var faqView: some View {
            VStack {
                HStack {
                    Button(action: {
                        withAnimation {
                            viewModel.isViewingFAQ = false
                        }
                    }) {
                        Image(systemName: "arrow.left")
                            .foregroundColor(.blue)
                    }
                    Spacer()
                    Text("FAQ")
                        .font(.headline)
                    Spacer()
                }
                .padding()

                FAQView()
            }
        }

        func showEditProfileView() {
            withAnimation {
                viewModel.isEditingProfile = true
            }
        }

        func actionSheetButtons() -> [ActionSheet.Button] {
            var buttons: [ActionSheet.Button] = [
                .default(Text("Change Profile Image")) {
                    self.showImagePicker = true
                }
            ]

            if viewModel.profileImageUrl != nil {
                buttons.append(.destructive(Text("Remove Profile Image")) {
                    viewModel.deleteProfileImage()
                })
            }

            buttons.append(.cancel())

            return buttons
        }

        func loadImage() {
            guard let image = self.image else { return }
            viewModel.uploadProfileImage(image)
        }
    }

    struct SettingsView_Previews: PreviewProvider {
        static var previews: some View {
            SettingsView(viewModel: ProfileViewModel())
                .background(Color.black.opacity(0.4))
        }
    }
