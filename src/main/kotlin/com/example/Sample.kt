package com.airbnb.foo

import javax.xml.validation.Schema

class XMLParser {
    fun parse(xmlText: String) {
        // ruleid: unsafe-xml-parser-schema-validator
        val validator = Schema.newValidator()
    }
}

