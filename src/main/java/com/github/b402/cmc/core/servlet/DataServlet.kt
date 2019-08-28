package com.github.b402.cmc.core.servlet

import com.github.b402.cmc.core.service.DataService
import org.apache.log4j.Logger
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@WebServlet(
        urlPatterns = arrayOf("/Data/*")
)
class DataServlet : HttpServlet() {
    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        val path = req.pathInfo
        val ds = registeredDataService[path]
        if (ds == null) {
            resp.status = 404
            return
        }
        val json = req.getParameter("param")
        if (json == null) {
            resp.status = 404
            return
        }
        try {
            val rd = ds.input(json)
            resp.characterEncoding = "utf-8";
            resp.setHeader("Content-type", "text/html;charset=UTF-8");
            req.characterEncoding = "utf-8";
            val writer = resp.writer
            writer.write(rd.toJson())
        } catch (t: RuntimeException) {
            Logger.getLogger(DataServlet::class.java).error("@/Data/${ds.path}}处理数据${json}发生异常", t)
        }
    }

    companion object {
        val registeredDataService = mutableMapOf<String, DataService<*>>()

        fun register(ds: DataService<*>) {
            registeredDataService[ds.path] = ds
        }
    }
}