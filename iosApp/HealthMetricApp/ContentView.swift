import SwiftUI
import UIKit
import HealthMetricUI

struct ContentView: View {
    var body: some View {
        ComposeHealthMetricView()
            .ignoresSafeArea(.keyboard)
    }
}

private struct ComposeHealthMetricView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        HealthMetricViewControllerFactory().makeViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {
        // The Compose hierarchy owns its state and recomposition lifecycle.
    }
}
