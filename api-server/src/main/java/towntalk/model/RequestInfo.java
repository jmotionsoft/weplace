package towntalk.model;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * Created by sin31 on 2016-09-10.
 */
public class RequestInfo {
    private String url;
    private String method;
    private String query;
    private String params;
    private String remoteAddress;
    private String userAgent;

    public RequestInfo(HttpServletRequest request) {
        url = request.getRequestURL().toString();
        method = request.getMethod();
        query = request.getQueryString();
        params = getParamHeader(request);
        remoteAddress = request.getRemoteAddr();
        userAgent = request.getHeader("User-Agent");
    }

    public String getParamHeader(HttpServletRequest request) {
        String param = "";

        Enumeration paramNames = request.getParameterNames();
        while(paramNames.hasMoreElements()) {
            String paramName = (String)paramNames.nextElement();

            param += paramName +"/";

            String[] paramValues = request.getParameterValues(paramName);

            if (paramValues.length == 1) {
                String paramValue = paramValues[0];
                if (paramValue.length() == 0)
                    param += "null";
                else
                    param += paramValue;
            } else {
                for(int i=0; i<paramValues.length; i++) {
                    param += "[" + paramValues[i] +"]";
                }
            }

            if(paramNames.hasMoreElements()){
                param += ", ";
            }
        }

        return param;
    }

    @Override
    public String toString() {
        String rst = "";

        rst += "URL: " + url + "\n";
        rst += "Method: " + method + "\n";
        rst += "Query: " + query + "\n";
        rst += "Params: " + params + "\n";
        rst += "Remote Address: " + remoteAddress + "\n";
        rst += "User Agent: "+ userAgent + "\n";

        return rst;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }
}
