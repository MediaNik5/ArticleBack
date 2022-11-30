package org.catblocks.articleback.security

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletRequestWrapper

@Component
class SetHostFilter(
    @Value("\${catblocks.host}") private val host: String,
    @Value("\${catblocks.port}") private val port: Int,
    @Value("\${catblocks.scheme}") private val scheme: String,
) : Filter {
    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {
        if (request is HttpServletRequest) {
            chain!!.doFilter(DifferentHostHttpServletRequest(request, host, port, scheme), response)
        }
    }
}

class DifferentHostHttpServletRequest(
    request: HttpServletRequest?,
    private val host: String,
    private val port: Int,
    private val scheme: String,
) : HttpServletRequestWrapper(request) {
    override fun getServerName(): String {
        return host
    }

    override fun getScheme(): String {
        return scheme
    }

    override fun getServerPort(): Int {
        return port
    }

    override fun getHeader(name: String?): String? {
        if (name.equals("Host", true))
            return "https://comgrid.ru"
        val header: String? = super.getHeader(name)
        return header ?: super.getParameter(name) // Note: you can't use getParameterValues() here.
    }

    override fun getHeaderNames(): Enumeration<String>? {
        val names: MutableList<String> = Collections.list(super.getHeaderNames())
        names.add("Host")
        return Collections.enumeration(names)
    }
}