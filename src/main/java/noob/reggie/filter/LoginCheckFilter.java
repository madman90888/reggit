package noob.reggie.filter;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import noob.reggie.domain.pojo.R;
import noob.reggie.util.BaseContext;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    private final static String[] urls = {
            "/employee/login",
            "/employee/logout",
            "/backend/**",
            "/front/**",
            "/user/sendMsg",
            "/user/login"
    };

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // 1.获取请求 URI 判断是否处理
        final String uri = request.getRequestURI();
        log.debug("拦截到的请求：", uri);
        if (check(uri)) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        // 2.判断是否登录
        if (request.getSession().getAttribute("employee") != null) {
            Long empId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);

            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        if (request.getSession().getAttribute("user") != null) {
            Long id = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(id);

            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        log.debug("用户未登录");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
    }

    /**
     * 检测是否放行
     * @param requestUri
     * @return
     */
    public boolean check(String requestUri) {
        for (String url : urls) {
            if (PATH_MATCHER.match(url, requestUri)) {
                return true;
            }
        }
        return false;
    }
}
