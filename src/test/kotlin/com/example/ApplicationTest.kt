package com.example

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.ktor.server.application.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.routing.*
import io.ktor.server.html.*
import io.ktor.server.http.content.*
import kotlin.test.*
import java.time.LocalDateTime

class ApplicationTest {
    
    private fun testAppModule(testing: suspend ApplicationTestBuilder.() -> Unit) = testApplication {
        application {
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
        }
        testing()
    }
    
    @Test
    fun testHitCounter() = testAppModule {
        val startingVal = GuestbookData.hitCounter
        
        client.get("/")
        
        val expected = startingVal + 1
        assertEquals(expected, GuestbookData.hitCounter)
    }
    
    @Test
    fun testAddSignature() = testAppModule {
        val startingSignatures = GuestbookData.signatures.size
        
        client.post("/") {
            header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
            setBody(listOf("name" to "John Smith").formUrlEncode())
        }
        
        assertEquals(startingSignatures + 1, GuestbookData.signatures.size)
        
        val newSig = GuestbookData.signatures.last()
        assertEquals("John Smith", newSig.name)
        
        val today = LocalDateTime.now().toLocalDate()
        assertEquals(today, newSig.timestamp.toLocalDate())
    }
    
    @Test
    fun testAddSignatureTrimsSpaces() = testAppModule {
        val startingSignatures = GuestbookData.signatures.size
        
        client.post("/") {
            header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
            setBody(listOf("name" to "    John Smith     ").formUrlEncode())
        }
        
        assertEquals(startingSignatures + 1, GuestbookData.signatures.size)
        
        val newSig = GuestbookData.signatures.last()
        assertEquals("John Smith", newSig.name)
    }
    
    @Test
    fun testRootPageReturnsHtml() = testAppModule {
        val response = client.get("/")
        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("Welcome to our Guestbook!"))
    }
}