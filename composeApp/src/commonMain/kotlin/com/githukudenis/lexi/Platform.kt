package com.githukudenis.lexi

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform