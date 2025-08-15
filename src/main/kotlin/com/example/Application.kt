package com.example

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.html.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.atomic.AtomicLong

object GuestbookData {
    private val _hitCounter = AtomicLong(0)
    private val _signatures = mutableListOf<Signature>()
    
    val hitCounter: Long get() = _hitCounter.get()
    val signatures: List<Signature> get() = _signatures.toList()
    
    fun incrementHits(): Long = _hitCounter.incrementAndGet()
    
    fun addSignature(signature: Signature) {
        synchronized(_signatures) {
            _signatures.add(signature)
        }
    }
}

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        install(CallLogging)
        
        routing {
            get("/") {
                call.handleGuestbookGet()
            }
            
            post("/") {
                call.handleGuestbookPost()
            }
            
            staticResources("/static", "static")
        }
    }.start(wait = true)
}

suspend fun ApplicationCall.handleGuestbookGet() {
    val hits = GuestbookData.incrementHits()
    val signatures = GuestbookData.signatures
    
    respondHtml {
        guestbookPage(hits, signatures)
    }
}

suspend fun ApplicationCall.handleGuestbookPost() {
    val formParameters = receiveParameters()
    val name = formParameters["name"]?.trim()
    
    if (!name.isNullOrBlank()) {
        val signature = Signature(name, LocalDateTime.now())
        GuestbookData.addSignature(signature)
    }
    
    // Redirect to GET to prevent duplicate submissions
    response.headers.append(HttpHeaders.Location, "/")
    respond(HttpStatusCode.Found)
}

fun HTML.guestbookPage(hits: Long, signatures: List<Signature>) {
    head {
        title("Guestbook")
    }
    body {
        div {
            style = "width: 500px; margin-left: auto; margin-right: auto;"
            
            h1 { +"Welcome to our Guestbook!" }
            
            h2 { +"Guests" }
            p { +"These are the guests who have signed our guestbook:" }
            
            if (signatures.isEmpty()) {
                p { +"Nobody has signed the guestbook yet! Be the first person!" }
            } else {
                ul {
                    signatures.forEach { signature ->
                        li { 
                            +"${signature.name} - ${signature.timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))}"
                        }
                    }
                }
            }
            
            h3 { +"Sign our Guestbook" }
            form(action = "/", method = FormMethod.post) {
                label {
                    htmlFor = "name"
                    +"Name:"
                }
                input(type = InputType.text, name = "name") {
                    id = "name"
                }
                input(type = InputType.submit) {
                    value = "Submit"
                }
            }
            
            p { +"$hits people have visited this website since January 15th, 1998" }
            p { 
                +"This website works best with "
                img(src = "static/images/msie_animated.gif")
            }
        }
    }
}