package com.github.b402.cmc.core.servlet

import com.github.b402.cmc.core.configuration.Configuration
import com.github.b402.cmc.core.service.DataService
import com.github.b402.cmc.core.service.data.*
import com.github.b402.cmc.core.service.impl.game.*
import com.github.b402.cmc.core.service.impl.user.*
import com.github.b402.cmc.core.service.impl.judge.*
import com.github.b402.cmc.core.service.impl.score.*
import kotlinx.coroutines.*
import org.apache.log4j.Logger
import java.io.BufferedReader
import java.io.InputStreamReader
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
        if (json == null) {
            response.status = 404
            return
        }
        val async = req.startAsync(req, response)
        GlobalScope.launch {
            response.characterEncoding = "utf-8";
            response.setHeader("Content-type", "application/json;charset=UTF-8");
            val resp = async.response
            resp.characterEncoding = "utf-8";
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


    override fun doPost(req: HttpServletRequest, response: HttpServletResponse) {
        req.characterEncoding = "utf-8";
        val data = Configuration.parser.parse(InputStreamReader(req.inputStream))
        val path = req.pathInfo
        val ds = dataService[path]
        if (ds == null) {
            response.status = 404
            return
        }
        val json = data.asJsonObject.getAsJsonObject("param")
        if (json == null) {
            response.status = 404
            return
        }
        val async = req.startAsync(req, response)
        GlobalScope.launch {
            response.characterEncoding = "utf-8";
            response.setHeader("Content-type", "application/json;charset=UTF-8");
            val resp = async.response
            resp.characterEncoding = "utf-8";
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

        @JvmStatic
        fun register(ds: DataService<*>) {
            dataService["/${ds.path}"] = ds
        }

        @JvmStatic
        fun unregister(path: String): DataService<*>? {
            return dataService.remove("/${path}")
        }

        @JvmStatic
        fun initDataService() {
            register(RegisterService)
            register(LoginService)
            register(UserInfoService)
            register(CreateGameService)
            register(GameListService)
            register(GameInfoService)
            register(UnverifiedUsersService)
            register(VerifyUserService)
            register(JoinedGameService)
            register(ApplyJudgeService)
            register(JudgeInfoService)
            register(UnverifiedJudgeService)
            register(VerifyJudgeService)
            register(AppointMainJudgeService)
            register(UploadScoreService)
            register(ScoreDataService)
            register(GameJoinInfoService)
            register(SortListService)
            register(UnverifiedScoreService)
            register(VerifyScoreService)
            register(GameJoinService)
            register(GameResultService)
            register(AppointProjectJudgeService)
            register(GameCompleteService)
            register(UnverifiedJoinGameInfoService)
            register(VerifyJoinGameService)
            register(UserPermissionService)
        }
    }
}