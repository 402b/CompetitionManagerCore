package com.github.b402.cmc.core.servlet

import org.apache.log4j.Logger
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@WebServlet(
        urlPatterns = arrayOf("/Data/*")
)
open class DataServlet : HttpServlet() {
    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        val map = mutableMapOf<String, String>()
        for (name in req.parameterNames) {
            map[name] = req.getParameter(name)
        }
        resp.writer.println("""
            {
                "context": "${req.pathInfo}",
                "data": "$map"
            }
        """.trimIndent())
    }
}