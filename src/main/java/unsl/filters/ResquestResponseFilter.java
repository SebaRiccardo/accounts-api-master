
package unsl.filters;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class ResquestResponseFilter implements Filter {
    int failRatio = 30;
    int minResponseTime = 100;
    int maxResponseTime = 1000;
    
    public ResquestResponseFilter() {
    }

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        long responseTime = Math.round(Math.random() * (double)(this.maxResponseTime - this.minResponseTime) + (double)this.minResponseTime);
        long fail = Math.round(Math.random() * 100.0D);

        try {
            Thread.sleep(responseTime);
        } catch (InterruptedException var9) {
            throw new RuntimeException();
        }

        if (fail < (long)this.failRatio) {
            throw new RuntimeException();
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    public void destroy() {
    }

}
