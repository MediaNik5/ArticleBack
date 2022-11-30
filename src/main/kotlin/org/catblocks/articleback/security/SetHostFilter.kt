package org.catblocks.articleback.security

import org.springframework.core.annotation.Order
import java.util.*
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletRequestWrapper

@Order(Int.MAX_VALUE)
class SetHostFilter : Filter {
    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {
        if (request is HttpServletRequest) {
            chain!!.doFilter(DifferentHostHttpServletRequest(request), response)
        }
    }
}

class DifferentHostHttpServletRequest(request: HttpServletRequest?) : HttpServletRequestWrapper(request) {
    override fun getHeader(name: String?): String? {
        if (name.equals("Host", true))
            return "https://comgrid.ru"
        val header: String = super.getHeader(name)
        return header ?: super.getParameter(name) // Note: you can't use getParameterValues() here.
    }

    override fun getHeaderNames(): Enumeration<String>? {
        val names: MutableList<String> = Collections.list(super.getHeaderNames())
        names.add("Host")
        return Collections.enumeration(names)
    }
}