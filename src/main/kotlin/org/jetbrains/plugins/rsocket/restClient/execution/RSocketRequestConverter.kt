package org.jetbrains.plugins.rsocket.restClient.execution

import com.intellij.httpClient.execution.common.RequestConverter
import com.intellij.httpClient.http.request.HttpRequestVariableSubstitutor
import com.intellij.httpClient.http.request.psi.HttpRequest
import com.intellij.openapi.application.ApplicationManager
import com.intellij.psi.SmartPsiElementPointer


@Suppress("UnstableApiUsage")
class RSocketRequestConverter : RequestConverter<RSocketRequest>() {

    override val requestType: Class<RSocketRequest> get() = RSocketRequest::class.java

    override fun psiToCommonRequest(requestPsiPointer: SmartPsiElementPointer<HttpRequest>, substitutor: HttpRequestVariableSubstitutor): RSocketRequest {
        var url = ""
        var requestType = ""
        var requestBody: String? = null
        var headers: Map<String, String>? = null
        ApplicationManager.getApplication().runReadAction {
            val httpRequest = requestPsiPointer.element
            url = httpRequest?.getHttpUrl(substitutor) ?: ""
            headers = httpRequest?.headerFieldList?.associate { it.name to it.getValue(substitutor) }
            requestType = httpRequest?.httpMethod!!
            requestBody = httpRequest.requestBody?.text
        }
        if (requestType == "RSOCKET") {
            requestType = "RPC"
        }
        return RSocketRequest(url, requestType, requestBody, headers)
    }

    override fun toExternalFormInner(request: RSocketRequest, fileName: String?): String {
        val builder = StringBuilder()
        builder.append("### rsocket request").append("\n")
        builder.append("RSOCKET ${request.rsocketURI}").append("\n")
        builder.append("\n");
        builder.append(request.textToSend ?: "")
        return builder.toString()
    }

}