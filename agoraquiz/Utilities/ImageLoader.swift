//
//  ImageLoader.swift
//  agoraquiz
//
//  Created by Ibercivis on 19/5/24.
//

import SwiftUI
import Combine

class ImageLoader: ObservableObject {
    @Published var image: UIImage? = nil
    private var cancellable: AnyCancellable?

    func load(from url: URL) {
        cancellable = URLSession.shared.dataTaskPublisher(for: url)
            .map { UIImage(data: $0.data) }
            .replaceError(with: nil)
            .receive(on: DispatchQueue.main)
            .assign(to: \.image, on: self)
    }

    deinit {
        cancellable?.cancel()
    }
}
