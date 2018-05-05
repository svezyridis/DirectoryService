package api;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.GeneralSecurityException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import crypto.Token;
import images.Gallery;
import images.Image;

/**
 * Servlet implementation class DirectoriApi
 */
@WebServlet("/DirectoriApi")
public class DirectoryApi extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DirectoryApi() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		request.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		response.setContentType("application/json;charset=UTF-8");
		
		String JSONString=request.getParameter("token");
		if (JSONString.equals("")) {
			JSONObject resJSON = new JSONObject();
			resJSON.put("error","no token found");
			out.print(resJSON);
			out.flush();
			return;
		}
		
		
		JSONObject token=new JSONObject(JSONString);
		String cryptedJSONString= token.getString("crypted");
		// Decrypt token
		try {
			token = Token.getDecryptedToken(cryptedJSONString);
		} catch (GeneralSecurityException e) {
			JSONObject resJSON = new JSONObject();
			resJSON.put("error","token could not be decrypted");
			out.print(resJSON);
			out.flush();
			return;
		}
		
		String username=CheckNewUser.checkIfnewAndadd(token);
		String action=request.getParameter("action");
		
		if (action.equals("addFriend")){
			String friend=request.getParameter("friendname");
			JSONObject resJSON = Friends.addFriend(username, friend);
			out.print(resJSON);
			out.flush();
			return;
		}
		else if(action.equals("deleteFriend")) {
			String friend=request.getParameter("friendname");
			JSONObject resJSON = Friends.deleteFriend(username, friend);
			out.print(resJSON);
			out.flush();
			return;	
		}
		else if(action.equals("getMyGalleries")) {
			JSONObject resJSON = Gallery.getUserGalleries(username);
			out.print(resJSON);
			out.flush();
			return;	
		}
		else if(action.equals("getFriendGalleries")) {
			String friendname =request.getParameter("friendname");
			JSONObject resJSON = Gallery.getFriendGalleries(username, friendname);
			out.print(resJSON);
			out.flush();
			return;	
		}
		else if(action.equals("createGallery")) {
			String galleryname=request.getParameter("galleryname");
			JSONObject resJSON = Gallery.createGallery(username, galleryname);
			out.print(resJSON);
			out.flush();
			return;
			
		}
		else if(action.equals("deleteGallery")) {
			String galleryname=request.getParameter("galleryname");
			JSONObject resJSON = Gallery.deleteGallery(username, galleryname);
			out.print(resJSON);
			out.flush();
			return;
			
		}
		else if(action.equals("deleteImage")) {
			String imageid=request.getParameter("imageid");
			JSONObject resJSON = Image.deleteImage(username, imageid);
			out.print(resJSON);
			out.flush();
			return;
			
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
