//
//  CategorySelectionView.swift
//  agoraquiz
//
//  Created by Ibercivis on 12/11/24.
//

import SwiftUI

struct CategorySelectionView: View {
    var onCategorySelected: (String) -> Void
    @Environment(\.presentationMode) var presentationMode

    var body: some View {
        ZStack {
            // Fondo borroso
            BlurView(style: .systemUltraThinMaterial)
                .edgesIgnoringSafeArea(.all)

            // Contenido del modal
            VStack {
                HStack {
                    Button(action: { presentationMode.wrappedValue.dismiss() }) {
                        Image(systemName: "chevron.left")
                        Text("Close")
                    }
                    .foregroundColor(.black)
                    .padding()
                    Spacer()
                }

                Text("Select Category")
                    .font(.title)
                    .bold()
                    .foregroundColor(.black)
                    .padding(.top, 24)

                VStack(spacing: 16) {
                    CategoryButton(title: "Climate change fundamentals") {
                        selectCategory("Climate change fundamentals")
                    }
                    CategoryButton(title: "Climate change disinformation") {
                        selectCategory("Climate change disinformation")
                    }
                    CategoryButton(title: "Climate change communication and narratives") {
                        selectCategory("Climate change communication and narratives")
                    }
                    CategoryButton(title: "Media literacy") {
                        selectCategory("Media literacy")
                    }
                }
                .padding(.horizontal, 24)
                .padding(.top, 48)

                Spacer()
            }
            .padding()
            .background(Color.white.opacity(0.8))  // Fondo ligeramente transparente
            .cornerRadius(16)
            .padding(.horizontal, 24)
        }
    }

    private func selectCategory(_ category: String) {
        onCategorySelected(category)
        presentationMode.wrappedValue.dismiss()
    }
}

// Componente de botón estilo "card"
struct CategoryButton: View {
    let title: String
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            Text(title)
                .font(.headline)
                .foregroundColor(.black)
                .padding()
                .frame(maxWidth: .infinity)
                .background(Color.white)
                .cornerRadius(8)
                .shadow(color: Color.gray.opacity(0.5), radius: 4, x: 0, y: 2)
        }
    }
}

// Vista de previsualización
struct CategorySelectionView_Previews: PreviewProvider {
    static var previews: some View {
        CategorySelectionView { category in
            print("Selected category: \(category)")
        }
    }
}

// Efecto de desenfoque en el fondo
struct BlurView: UIViewRepresentable {
    var style: UIBlurEffect.Style

    func makeUIView(context: Context) -> UIVisualEffectView {
        let view = UIVisualEffectView(effect: UIBlurEffect(style: style))
        return view
    }

    func updateUIView(_ uiView: UIVisualEffectView, context: Context) {}
}
