//
//  URLImage.swift
//  agoraquiz
//
//  Created by Ibercivis on 19/5/24.
//

import SwiftUI

struct URLImage: View {
    @ObservedObject private var imageLoader: ImageLoader
    private let placeholder: Image

    init(url: URL, placeholder: Image = Image("avatarPlain")) {
        self.imageLoader = ImageLoader()
        self.placeholder = placeholder
        self.imageLoader.load(from: url)
    }

    var body: some View {
        Group {
            if let image = imageLoader.image {
                Image(uiImage: image).resizable()
            } else {
                placeholder.resizable()
            }
        }
    }
}
