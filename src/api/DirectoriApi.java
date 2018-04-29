package api;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import crypto.Token;

/**
 * Servlet implementation class DirectoriApi
 */
@WebServlet("/DirectoriApi")
public class DirectoriApi extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DirectoriApi() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("UTF-8");
		String JSONString=request.getParameter("token");
		JSONObject token=new JSONObject(JSONString);
		String cryptedJSONString= token.getString("crypted");
		try {
			token = Token.getDecryptedToken(cryptedJSONString);
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		CheckNewUser.checkIfnewAndadd(token);
		String action=request.getParameter("action");
		
		if (action.equals("addFriend")){
			String friendid=request.getParameter("friendid");
			Friends.addFriend(token, friendid);
		}
		
			
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
