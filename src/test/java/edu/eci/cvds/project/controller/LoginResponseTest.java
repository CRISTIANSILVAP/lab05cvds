package edu.eci.cvds.project.controller;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

public class LoginResponseTest {

    private LoginResponse loginResponse;

    @BeforeEach
    public void setUp() {
        loginResponse = new LoginResponse();
    }

    @Test
    public void testAddCookie() {
        Cookie cookie = new Cookie("test", "value");
        assertDoesNotThrow(() -> loginResponse.addCookie(cookie));
    }

    @Test
    public void testContainsHeader() {
        assertFalse(loginResponse.containsHeader("Authorization"));
    }

    @Test
    public void testEncodeURL() {
        assertEquals("", loginResponse.encodeURL("http://example.com"));
    }

    @Test
    public void testSetStatus() {
        assertDoesNotThrow(() -> loginResponse.setStatus(200));
    }

    @Test
    public void testGetStatus() {
        assertEquals(0, loginResponse.getStatus());
    }

    @Test
    public void testSetLocale() {
        assertDoesNotThrow(() -> loginResponse.setLocale(Locale.US));
    }

    @Test
    public void testGetLocale() {
        assertNull(loginResponse.getLocale());
    }

    @Test
    public void testGetHeader() {
        assertEquals("", loginResponse.getHeader("Content-Type"));
    }

    @Test
    public void testGetHeaders() {
        Collection<String> headers = loginResponse.getHeaders("Content-Type");
        assertTrue(headers.isEmpty());
    }

    @Test
    public void testGetHeaderNames() {
        Collection<String> headerNames = loginResponse.getHeaderNames();
        assertTrue(headerNames.isEmpty());
    }

    @Test
    public void testSendError() {
        assertDoesNotThrow(() -> loginResponse.sendError(404, "Not Found"));
    }

    @Test
    public void testSendRedirect() {
        assertDoesNotThrow(() -> loginResponse.sendRedirect("http://example.com"));
    }

    @Test
    public void testGetCharacterEncoding() {
        assertEquals("", loginResponse.getCharacterEncoding());
    }

    @Test
    public void testGetContentType() {
        assertEquals("", loginResponse.getContentType());
    }

    @Test
    public void testSetContentType() {
        assertDoesNotThrow(() -> loginResponse.setContentType("text/html"));
    }

    @Test
    public void testGetWriter() throws IOException {
        assertNull(loginResponse.getWriter());
    }

    @Test
    public void testFlushBuffer() {
        assertDoesNotThrow(() -> loginResponse.flushBuffer());
    }

    @Test
    public void testResetBuffer() {
        assertDoesNotThrow(() -> loginResponse.resetBuffer());
    }

    @Test
    public void testIsCommitted() {
        assertFalse(loginResponse.isCommitted());
    }
}
