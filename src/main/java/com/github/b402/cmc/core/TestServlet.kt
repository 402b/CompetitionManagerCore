package com.github.b402.cmc.core

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.PrintWriter
import java.lang.Thread.sleep

import javax.servlet.AsyncContext
import javax.servlet.ServletException
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@WebServlet("/test",asyncSupported = true)
class TestServlet : HttpServlet() {

    @Throws(ServletException::class, IOException::class)
    override fun doPost(req: HttpServletRequest, resp: HttpServletResponse) {
        val writer = resp.writer
        writer.println("{\"message\": \"Hello world\"}")
    }

    @Throws(ServletException::class, IOException::class)
    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        val async = req.startAsync(req, resp)
        GlobalScope.launch {
            val writer = async.response.writer
            sleep(3000L)
            writer.println("{\"message\": \"Hello world\"}")
            async.complete()
        }
    }
}
