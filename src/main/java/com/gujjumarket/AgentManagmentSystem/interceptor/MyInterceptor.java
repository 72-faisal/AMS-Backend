//package com.gujjumarket.AgentManagmentSystem.interceptor;
//
//import org.springframework.stereotype.Component;
//import org.springframework.web.servlet.HandlerInterceptor;
//
//import com.gujjumarket.AgentManagmentSystem.utils.JWT;
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jws;
//import io.jsonwebtoken.JwtException;
//import io.jsonwebtoken.Jwts;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//
//@Component
//public class MyInterceptor implements HandlerInterceptor {
//
//	@Override
//	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
//			throws Exception {
//		String uri = request.getRequestURI();
//		if ("OPTIONS".equals(request.getMethod())) {
//			// Allow OPTIONS requests to proceed without authentication
//			return true;
//		}
//		// Allow static resources without authentication
//        if (uri.startsWith("/static/") || uri.endsWith(".html") || uri.endsWith(".js") || uri.endsWith(".css") || uri.endsWith(".png") || uri.endsWith(".jpg") || uri.endsWith(".jpeg")) {
//            return true;
//        }
//        else if (request.getRequestURI().equals("/admin/loginAdmin")) {
//			return true;
//		}else if (request.getRequestURI().equals("/")) {
//				return true;
//		}else if (request.getServletPath().startsWith("/user")) {
//			return true;
//		}else if (request.getRequestURI().startsWith("/transactions")) {
//			return true;
//		}
////		}else if (request.getRequestURI().equals("/producttype/products")) {
////			return true;
//
//		else {
//			String token2 = request.getHeader("Authorization");
//			  if (token2 == null || !token2.startsWith("Bearer ")) {
//		            // Return unauthorized if there's no token or it doesn't start with "Bearer "
//		            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authorization header is missing or invalid");
//		            return false;
//		        }
////			  String token = token2.substring(7); // Extract JWT token
////		        if (!JWT.validateToken(token)) {
////		            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token is invalid or expired");
////		            return false;
////		        }
////			System.out.println("INXISE AUTH");
////			System.out.println(token2 + " token2");
//			String token = token2.substring(7);
////			System.out.println(token+ " token");
//			if (token2 != null && token2.startsWith("Bearer ") && JWT.validateToken(token)
//					&& request.getAttribute("forwarded") == null && !response.isCommitted()) {
//				try {
//					Jws<Claims> claims = Jwts.parser().verifyWith(JWT.getJwtsecret()).build().parseSignedClaims(token);
//
//					Integer userId = claims.getPayload().get("userId", Integer.class);
//					String role = claims.getPayload().getSubject();
//					request.setAttribute("userId", userId);
//					
//					if ("ADMIN".equals(role.toUpperCase()) && request.getServletPath().startsWith("admin")) {
//						System.out.println("here");
//						request.setAttribute("forwarded", true);
//						request.getRequestDispatcher(request.getServletPath()).forward(request, response);
//						return false;
//					} else if ("ADMIN".equals(role) && request.getServletPath().startsWith("product")) {
//						request.setAttribute("forwarded", true);
//						request.getRequestDispatcher(request.getServletPath()).forward(request, response);
//						return false;
//					} else if ("COUNTRYHEAD".equals(role) && request.getServletPath().startsWith("COUNTRYHEAD")) {
//						request.setAttribute("forwarded", true);
//						request.getRequestDispatcher(role).forward(request, response);
//						return true;
//					} else if ("AGENT".equals(role) && request.getServletPath().startsWith("AGENT")) {
//						request.setAttribute("forwarded", true);
//						request.getRequestDispatcher(role).forward(request, response);
//						return false;
//					} else if ("SUBAGENT".equals(role) && request.getServletPath().startsWith("SUBAGENT")) {
//						request.setAttribute("forwarded", true);
//						request.getRequestDispatcher(role).forward(request, response);
//						return true;
//					} else {
//						return true;
//					}
//
//				} catch (JwtException e) {
//					// TODO: handle exception
//					e.getMessage();
//					response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "token is invalid");
//
//					System.out.println("Pre-handle method is called");
//					return false;
//				}
//			}
//			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "token is invalid");
//			return false;
//		} // If true, the execution chain will proceed; if false, it stops here
//	}
//
//	@Override
//	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
//			org.springframework.web.servlet.ModelAndView modelAndView) throws Exception {
//		// Your logic after the controller handler is invoked, but before the view is
//		// rendered
//		System.out.println("Post-handle method is called");
//	}
//
//	@Override
//	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
//			throws Exception {
//		// Your logic after the view is rendered or after an exception is thrown
//		System.out.println("After-completion method is called");
//	}
//}
package com.gujjumarket.AgentManagmentSystem.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.gujjumarket.AgentManagmentSystem.utils.JWT;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class MyInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();

        // Allow OPTIONS requests to proceed without authentication
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }

        // Allow public endpoints without authentication
        if (uri.equals("/admin/loginAdmin") || uri.equals("/") || uri.startsWith("/user") || uri.startsWith("/transactions")) {
            return true;
        }

        // Allow static resources without authentication
        if (uri.startsWith("/static/") || uri.endsWith(".html") || uri.endsWith(".js") || uri.endsWith(".css") || uri.endsWith(".png") || uri.endsWith(".jpg") || uri.endsWith(".jpeg")) {
            return true;
        }

        String token2 = request.getHeader("Authorization");
        if (token2 == null || !token2.startsWith("Bearer ")) {
            // Return unauthorized if there's no token or it doesn't start with "Bearer "
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authorization header is missing or invalid");
            return false;
        }

        String token = token2.substring(7); // Extract JWT token
        if (token2 != null && token2.startsWith("Bearer ") && JWT.validateToken(token)
                && request.getAttribute("forwarded") == null && !response.isCommitted()) {
            try {
                Jws<Claims> claims = Jwts.parser().verifyWith(JWT.getJwtsecret()).build().parseSignedClaims(token);

                Integer userId = claims.getPayload().get("userId", Integer.class);
                String role = claims.getPayload().getSubject();
                request.setAttribute("userId", userId);

                if ("ADMIN".equals(role.toUpperCase()) && request.getServletPath().startsWith("admin")) {
                    request.setAttribute("forwarded", true);
                    request.getRequestDispatcher(request.getServletPath()).forward(request, response);
                    return false;
                } else if ("ADMIN".equals(role) && request.getServletPath().startsWith("product")) {
                    request.setAttribute("forwarded", true);
                    request.getRequestDispatcher(request.getServletPath()).forward(request, response);
                    return false;
                } else if ("COUNTRYHEAD".equals(role) && request.getServletPath().startsWith("COUNTRYHEAD")) {
                    request.setAttribute("forwarded", true);
                    request.getRequestDispatcher(role).forward(request, response);
                    return true;
                } else if ("AGENT".equals(role) && request.getServletPath().startsWith("AGENT")) {
                    request.setAttribute("forwarded", true);
                    request.getRequestDispatcher(role).forward(request, response);
                    return false;
                } else if ("SUBAGENT".equals(role) && request.getServletPath().startsWith("SUBAGENT")) {
                    request.setAttribute("forwarded", true);
                    request.getRequestDispatcher(role).forward(request, response);
                    return true;
                } else {
                    return true;
                }

            } catch (JwtException e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "token is invalid");
                return false;
            }
        }

        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "token is invalid");
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, org.springframework.web.servlet.ModelAndView modelAndView) throws Exception {
        // Your logic after the controller handler is invoked, but before the view is rendered
        System.out.println("Post-handle method is called");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // Your logic after the view is rendered or after an exception is thrown
        System.out.println("After-completion method is called");
    }
}

