package com.github.b402.cmc.core.event

interface Cancellable {
    var isCancel: Boolean
    var reason: String?
}