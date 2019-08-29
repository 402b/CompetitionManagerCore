package com.github.b402.cmc.core.servlet

import com.github.b402.cmc.core.service.DataService
import com.github.b402.cmc.core.service.RegisterService
import com.github.b402.cmc.core.service.data.ERROR
import com.github.b402.cmc.core.service.data.ERROR_TIMTOUT
import com.github.b402.cmc.core.service.data.returnData
import kotlinx.coroutines.*
import org.apache.log4j.Logger
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@WebServlet(
        urlPatterns = arrayOf("/Data/*"),
        asyncSupported = true
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
        resp.characterEncoding = "utf-8";
        resp.setHeader("Content-type", "application/json;charset=UTF-8");
        req.characterEncoding = "utf-8";
        val async = req.asyncContext
        GlobalScope.launch {
            val resp = async.response
            val returndata = withTimeoutOrNull(5000) {
                try {
                    return@withTimeoutOrNull ds.input(json,this)
                } catch (timeout: TimeoutCancellationException) {
                    Logger.getLogger(DataServlet::class.java).error("@/Data/${ds.path}}处理数据${json}时超时", timeout)
                    return@withTimeoutOrNull returnData(ERROR_TIMTOUT) {
                        this.json.addProperty("reason", "请求超时")
                    }
                } catch (t: RuntimeException) {
                    Logger.getLogger(DataServlet::class.java).error("@/Data/${ds.path}}处理数据${json}发生异常", t)
                    return@withTimeoutOrNull returnData(ERROR) {
                        this.json.addProperty("reason", "请求异常")
                    }
                }
            }
            val writer = resp.writer
            if (returndata == null) {
                val rd = returnData(ERROR_TIMTOUT) {
                    this.json.addProperty("reason", "请求超时")
                }
                writer.write(rd.toJson())
            } else {
                writer.write(returndata.toJson())
            }
            async.complete()
        }
    }

    companion object {
        val registeredDataService = mutableMapOf<String, DataService<*>>()

        fun register(ds: DataService<*>) {
            registeredDataService["/${ds.path}"] = ds
        }

        fun init() {
            register(RegisterService())
        }
    }
}