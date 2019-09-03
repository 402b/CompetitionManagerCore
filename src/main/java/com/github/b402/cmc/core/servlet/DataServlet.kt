package com.github.b402.cmc.core.servlet

import com.github.b402.cmc.core.service.DataService
import com.github.b402.cmc.core.service.data.*
import com.github.b402.cmc.core.service.impl.*
import com.github.b402.cmc.core.service.impl.game.*
import kotlinx.coroutines.*
import org.apache.log4j.Logger
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@WebServlet(
        urlPatterns = ["/Data/*"],
        asyncSupported = true
)
class DataServlet : HttpServlet() {
    override fun doGet(req: HttpServletRequest, response: HttpServletResponse) {
        req.characterEncoding = "utf-8";
        val path = req.pathInfo
        val ds = dataService[path]
        if (ds == null) {
            response.status = 404
            return
        }
        val json = req.getParameter("param")
        Logger.getLogger(DataServlet::class.java).debug("json: $json")
        if (json == null) {
            response.status = 404
            return
        }
        Logger.getLogger(DataServlet::class.java).debug("req.asyncContext")
        val async = req.startAsync(req, response)
        Logger.getLogger(DataServlet::class.java).debug("GlobalScope.launch ")
        GlobalScope.launch {
            response.characterEncoding = "utf-8";
            response.setHeader("Content-type", "application/json;charset=UTF-8");
            Logger.getLogger(DataServlet::class.java).debug("async")
            val resp = async.response
            resp.characterEncoding = "utf-8";
            Logger.getLogger(DataServlet::class.java).debug(" async.response")
            val returndata = withTimeoutOrNull(5000) {
                try {
                    return@withTimeoutOrNull ds.input(json)
                } catch (timeout: TimeoutCancellationException) {
                    Logger.getLogger(DataServlet::class.java).error("@/Data/${ds.path}}处理数据${json}时超时", timeout)
                    return@withTimeoutOrNull returnData(ERROR_TIMEOUT, "请求超时")
                } catch (t: RuntimeException) {
                    Logger.getLogger(DataServlet::class.java).error("@/Data/${ds.path}}处理数据${json}发生异常", t)
                    return@withTimeoutOrNull returnData(ERROR, "请求异常")
                }
            }
            Logger.getLogger(DataServlet::class.java).debug("returndata: ${returndata?.toString()
                    ?: "=null"}")
            val writer = resp.writer
            if (returndata == null) {
                val rd = returnData(ERROR_TIMEOUT, "请求超时")
                writer.write(rd.toJson())
            } else {
                writer.write(returndata.toJson())
            }
            async.complete()
        }
    }

    companion object {
        val dataService = mutableMapOf<String, DataService<*>>()

        fun register(ds: DataService<*>) {
            dataService["/${ds.path}"] = ds
        }

        @JvmStatic
        fun initDataService() {
            register(RegisterService)
            register(LoginService)
            register(UserInfoService)
            register(CreateGameService)
            register(GameListService)
            register(GameInfoService)
        }
    }
}